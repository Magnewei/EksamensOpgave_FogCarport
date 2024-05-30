package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static j2html.TagCreator.*;

public class ChatController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.ws("/websocket", ws -> {
            ws.onConnect(ctx -> onConnect(ctx));
            ws.onMessage(ctx -> onMessage(ctx));
            ws.onClose(ctx -> onClose(ctx));
            ws.onError(ctx -> onError(ctx));
        });
    }

    private static final Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();

    private static void onConnect(WsContext ctx) {
        // If session contains a user object, then return user's name.
        // Else return the name that has been given by the user, before opening the chat.
        String username = ctx.sessionAttribute("currentUser") == null
                ? ctx.sessionAttribute("chatUsername")
                : ((User) ctx.sessionAttribute("currentUser")).getFullName();

        userUsernameMap.put(ctx, username);
    }


    private static void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        broadcastMessage(userUsernameMap.get(ctx), message);
    }

    private static void onClose(WsCloseContext ctx) {
        System.out.println("WS connection closed");
        userUsernameMap.remove(ctx); // remove the session from the map.
    }

    private static void onError(WsErrorContext ctx) {
        throw new RuntimeException(ctx.error());
    }

    // Sends a message from one user to all users, along with a list of current usernames
    private static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
            session.send(
                    Map.of(
                            "userMessage", createHtmlMessageFromSender(sender, message),
                            "userlist", userUsernameMap.values()
                    )
            );
        });
    }

    // Builds a HTML element with a sender-name, a message, and a timestamp
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }
}


