package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Services.ChatUtils;
import io.javalin.Javalin;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.exceptions.WebSocketException;

import java.time.Duration;
import java.util.Map;

public class ChatController {
    public static void addRoutes(Javalin app) {
        app.ws("/websocket", ws -> {
            ws.onConnect(ctx -> onConnect(ctx));
            ws.onMessage(ctx -> onMessage(ctx));
            ws.onClose(ctx -> onClose(ctx));
            ws.onError(ctx -> onError(ctx));
        });
    }

    private static void onConnect(WsContext ctx) {
        try {
            // Override Jettys default idle timer of 5ish seconds to avoid using pings.
            ctx.session.setIdleTimeout(Duration.ofSeconds(300));
            ChatUtils.addNewChatSession(ctx);

        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }


    private static void onMessage(WsMessageContext ctx) {
        String customerName = ctx.sessionAttribute("customerUsername");
        String adminName = ctx.sessionAttribute("adminName");

        User customer = ChatUtils.getUserFromContext(customerName);
        User admin = ChatUtils.getUserFromContext(adminName);

        WsContext customerCtx = ChatUtils.getContextByUser(customer);
        WsContext adminCtx = ChatUtils.getContextByUser(admin);

        System.out.println(adminCtx + " adminctx");

        System.out.println(customerCtx + " customerCtx");

        String message = ctx.message();

        if (customerCtx != null && adminCtx != null) {
            Map<WsContext, User> activeChatSessions = ChatUtils.getActiveChats();
            for (Map.Entry<WsContext, User> entry : activeChatSessions.entrySet()) {
                User user = entry.getValue();

                Map<String, String> userChat = user.getUserChat();
                String htmlMessage = ChatUtils.createHtmlMessageFromSender(user.getFullName(), message);

                if (user.equals(admin) || user.equals(customer)) {
                    userChat.put("userMessage", htmlMessage);

                    customerCtx.send(userChat);
                    adminCtx.send(userChat);

                    customerCtx.sendPing();
                    adminCtx.sendPing();
                  return;
                }
            }
        } else {
            String errorMessage = ChatUtils.HTMLErrorMessage("We're waiting for the second user.");
            ctx.send(Map.of("userMessage", errorMessage));

            customerCtx.sendPing();
            adminCtx.sendPing();
        }

    }


    private static void onClose(WsCloseContext ctx) {
        Map<WsContext, User> activeChatSessions = ChatUtils.getActiveChats();
        User user = activeChatSessions.get(ctx);

        String leavingMessage = ChatUtils.userLeftChatMessage(user);
        ctx.send(Map.of("userMessage", leavingMessage));

        // Remove the session from the map and clear the users chat history.
        user.getUserChat().clear();
        activeChatSessions.remove(ctx);
    }

    private static void onError(WsErrorContext ctx) {
        Map<WsContext, User> activeChatSessions = ChatUtils.getActiveChats();
        User user = activeChatSessions.get(ctx);
        user.getUserChat().clear();
        activeChatSessions.remove(ctx);

        // Attempt to render index.
        ctx.attribute("message", "Something went wrong with your chat session.");
        ctx.getUpgradeCtx$javalin().render("index.html");
    }
}


