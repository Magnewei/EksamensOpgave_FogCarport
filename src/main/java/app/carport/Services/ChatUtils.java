package app.carport.Services;

import app.carport.Entities.User;
import io.javalin.websocket.WsContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

public class ChatUtils {

    private static final Map<WsContext, User> userUsernameMap = new ConcurrentHashMap<>();


    public static String userLeftChatMessage(User user) {
        return article(
                b(user.getFullName() + "has left the chat."),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date()))).
                render();
    }


    // Builds a HTML element with a sender-name, a message, and a timestamp
    public static String createHtmlMessageFromSender(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    public static void addNewChatSession(WsContext ctx) {
        User user;

        if (ctx.sessionAttribute("currentUser") != null) {
            user = ctx.sessionAttribute("currentUser");
        } else {

            // The user's name is taken from the input field in the HTML-header.
            String chatUsername = ctx.sessionAttribute("chatUsername");
            user = new User(chatUsername, " ");
        }

        userUsernameMap.put(ctx, user);
    }


    public static Map<WsContext, User> getActiveChats() {
        return userUsernameMap;
    }

    public static WsContext getChatContext(int hashcode) {
        for (WsContext context : userUsernameMap.keySet()) {
            if (context.hashCode() == hashcode) {
                return context;
            }
        }

        System.out.println("No chat context found for hashcode " + hashcode);
        return null;
    }
}
