package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Services.ChatUtils;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import static j2html.TagCreator.*;

public class ChatController {
    public static void addRoutes(Javalin app) {
        app.ws("/websocket", ws -> {
            ws.onConnect(ctx -> onConnect(ctx));
            ws.onMessage(ctx -> onMessage(ctx));
            ws.onClose(ctx -> onClose(ctx));
            ws.onError(ctx -> onError(ctx));
        });
    }

    // TODO: Refactor mappen væk.
    private static final Map<WsContext, User> userUsernameMap = ChatUtils.getActiveChats();

    private static void onConnect(WsContext ctx) {
        try {
            // Override Jettys default idle timer of 5ish seconds to avoid using pings.
            ctx.session.setIdleTimeout(Duration.ofSeconds(300));

            User user;
            if (ctx.sessionAttribute("currentUser") != null) {
                user = ctx.sessionAttribute("currentUser");
            } else {
                // Name taken from input within the header.
                String chatUsername = ctx.sessionAttribute("chatUsername");
                user = new User(chatUsername, " ");
            }

            userUsernameMap.put(ctx, user);

            // Trying to catch random errors while testing.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        broadcastMessage(userUsernameMap.get(ctx), message, ctx);
    }

    private static void onClose(WsCloseContext ctx) {
        userUsernameMap.remove(ctx); // remove the session from the map.
        //TODO: add user left chat
    }

    private static void onError(WsErrorContext ctx) {
        userUsernameMap.remove(ctx);

        // Error messaging on the server and client side.
        ctx.attribute("message", "Something went wrong with your chat session.");
        System.out.println(ctx.error().getMessage());

        // Attempt to rerender index.
        ctx.getUpgradeCtx$javalin().render("index.html");
    }

    // Sends a message from one user to all users, along with a list of current usernames
    private static void broadcastMessage(User user, String message, WsContext ctx) {
        Map userChat = user.getUserChat();
        userChat.put(
                "userMessage", createHtmlMessageFromSender(user.getFullName(), message));
        ctx.send(userChat);
    }

    private static void broadcastMessage(String sender, String message, WsContext ctx) {
        ctx.send(Map.of(
                "userMessage", createHtmlMessageFromSender(sender, message),
                "userlist", userUsernameMap.values()
        ));
    }



    // Builds a HTML element with a sender-name, a message, and a timestamp
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    private static String userLeftChatMessage(String sender, String message) {
        return article(
                b(sender + " left the chat."),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date()))).
                render();
    }
}


