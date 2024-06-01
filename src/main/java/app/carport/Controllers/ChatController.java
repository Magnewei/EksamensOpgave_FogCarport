package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Services.ChatUtils;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
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
    private static final Map<WsContext, String> userUsernameMap = ChatUtils.getActiveChats();

    private static void onConnect(WsContext ctx) {
        try {

            // Override Jettys incredibly short default idle timeout to avoid doing intermittent pinging.
            // setIdleTimeout takes a Duration object.
            ctx.session.setIdleTimeout(Duration.ofSeconds(300));

            // If session contains a user object, then return user's name.
            // Else return the name that has been given by the user, before opening the chat.
            String username = ctx.sessionAttribute("currentUser") == null
                    ? ctx.sessionAttribute("chatUsername")
                    : ((User) ctx.sessionAttribute("currentUser")).getFullName();

            userUsernameMap.put(ctx, username);

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
    private static void broadcastMessage(String sender, String message, WsContext ctx) {
        ctx.send(List.of(
                "userMessage", createHtmlMessageFromSender(sender, message),
                "userlist", userUsernameMap.values()
        ));
    }

    /*
    private static void broadcastMessage(String sender, String message, WsContext ctx) {
        ctx.send(List.of(
                "userMessage", createHtmlMessageFromSender(sender, message),
                "userlist", userUsernameMap.values()
        ));
    }

     */

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


