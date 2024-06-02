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
        User currentUser = ctx.sessionAttribute("currentUser");


        // Load the appropriate method depending on user.role.
        if (currentUser.isAdmin()) {

            // admin user is not retrieved from currentUser in the method.
            onMessageAdmin(ctx);

        } else if (!currentUser.isAdmin()) {
            onMessageUser(ctx, currentUser);
        }
    }

    private static synchronized void onMessageUser(WsMessageContext ctx, User currentUser) {
        try {
            User admin = ChatUtils.getAdminUser();
            WsContext adminContext = ChatUtils.getContextByUser(admin);

            if (adminContext != null) {
                String messageText = ctx.message();
                String htmlMessage = ChatUtils.createHtmlMessageFromUser(currentUser.getFullName(), messageText);

                // Send the message to both user and admin socket, so both of them can see the messageText.
                adminContext.send(Map.of("userMessage", htmlMessage));
                ctx.send(Map.of("userMessage", htmlMessage));

            } else {
                String errorMessage = ChatUtils.HTMLErrorMessage("We're waiting for the second user.");
                ctx.send(Map.of("userMessage", errorMessage));
            }
        } catch (WebSocketException e) {
            e.printStackTrace();
            String errorMessage = ChatUtils.HTMLErrorMessage("An error occurred while sending the message.");
            ctx.send(Map.of("userMessage", errorMessage));;
        }
    }

    private static synchronized void onMessageAdmin(WsMessageContext ctx) {
        try {
            String customerName = ctx.sessionAttribute("customerUsername");

            // Load customer User object and the users WsContext.
            User customer = ChatUtils.getUserFromContext(customerName);
            WsContext customerCtx = ChatUtils.getContextByUser(customer);

            if (customerCtx != null && customerCtx.session.isOpen()) {
                String message = ctx.message();
                String htmlMessage = ChatUtils.createHtmlMessageFromAdmin(message);

                // Send the message to both user and admin socket, so both of them can see the messageText.
                customerCtx.send(Map.of("userMessage", htmlMessage));
                ctx.send(Map.of("userMessage", htmlMessage));

            } else {
                String errorMessage = ChatUtils.HTMLErrorMessage("We're waiting for the second user.");
                ctx.send(Map.of("userMessage", errorMessage));
            }
        } catch (WebSocketException e) {
            e.printStackTrace();
            String errorMessage = ChatUtils.HTMLErrorMessage("An error occurred while sending the message.");
            ctx.send(Map.of("userMessage", errorMessage));;
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
        System.out.println(ctx.error().getMessage());
        String errorMessage = ChatUtils.HTMLErrorMessage("An error occurred while sending the message.");
        ctx.send(Map.of("userMessage", errorMessage));
    }
}


