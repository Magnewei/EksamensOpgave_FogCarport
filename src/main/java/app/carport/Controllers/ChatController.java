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

import static app.carport.Services.ChatUtils.getUserFromContext;

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
        String customerName =   ctx.sessionAttribute("customerUsername");
        String adminName =   ctx.sessionAttribute("adminName");
        User customer = ChatUtils.getUserFromContext(customerName);
        User admin = ChatUtils.getUserFromContext(adminName);

        String message = ctx.message();

        if (admin != null && customer != null) {
            Map<WsContext, User> activeChatSessions = ChatUtils.getActiveChats();
            for (Map.Entry<WsContext, User> entry : activeChatSessions.entrySet()) {
                WsContext context = entry.getKey();
                User user = entry.getValue();

                Map<String, String> userChat = user.getUserChat();
                String htmlMessage = ChatUtils.createHtmlMessageFromSender(user.getFullName(), message);

                if (user.equals(admin) |) {
                    userChat.put("userMessage", htmlMessage);
                    context.send(userChat);

                }  if (user.equals(customer))
                    userChat.put("userMessage", htmlMessage);
                    context.send(userChat);
            }

        } else {
            String errorMessage = ChatUtils.HTMLErrorMessage("We're waiting for the second user.");
            ctx.send(Map.of("userMessage", errorMessage));
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


