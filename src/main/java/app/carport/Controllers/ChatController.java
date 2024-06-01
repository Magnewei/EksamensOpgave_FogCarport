package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Services.ChatUtils;
import io.javalin.Javalin;
import io.javalin.websocket.*;
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
        Map<WsContext, User> activeChatSessions = ChatUtils.getActiveChats();
        User user = activeChatSessions.get(ctx);
        Map<String, String> userChat = user.getUserChat();

        String message = ctx.message();
        String htmlMessage = ChatUtils.createHtmlMessageFromSender(user.getFullName(), message);

        userChat.put("userMessage", htmlMessage);
        ctx.send(userChat);
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


