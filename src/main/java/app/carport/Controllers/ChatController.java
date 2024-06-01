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

    private static void onMessage(WsMessageContext ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");

        if (currentUser.isAdmin()) {
            onMessageAdmin(ctx, currentUser);

        } else if (!currentUser.isAdmin()) {
            onMessageUser(ctx, currentUser);
        }
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

    private static synchronized void onMessageUser(WsMessageContext ctx, User currentUser) {
        User admin = ChatUtils.getAdmin();
        WsContext adminContext = ChatUtils.getContextByUser(admin);
        System.out.println(admin.getUserID());

        if (adminContext != null) {
            Map<String, String> customerChat = admin.getUserChat();

            String message = ctx.message();
            String htmlMessage = ChatUtils.createHtmlMessageFromSender(currentUser.getFullName(), message);
            customerChat.put("userMessage", htmlMessage);
            adminContext.send(customerChat);
            ctx.send(customerChat);

        } else {
            String errorMessage = ChatUtils.HTMLErrorMessage("We're waiting for the second user.");
            ctx.send(Map.of("userMessage", errorMessage));
        }
    }

    private static synchronized void onMessageAdmin(WsMessageContext ctx, User currentUser) {
        String customerName = ctx.sessionAttribute("customerUsername");
        User customer = ChatUtils.getUserFromContext(customerName);
        WsContext customerCtx = ChatUtils.getContextByUser(customer);

        if (customerCtx != null && customerCtx.session.isOpen()) {
            Map<String, String> customerChat = customer.getUserChat();

            String message = ctx.message();
            String htmlMessage = ChatUtils.createHtmlMessageFromSender(currentUser.getFullName(), message);
            customerChat.put("userMessage", htmlMessage);
            customerCtx.send(customerChat);
            ctx.send(customerChat);

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
        System.out.println(ctx.error());
    }
}


