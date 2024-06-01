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
                b(user.getFullName() + " has left the chat."),
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

    public static String createHtmlMessageFromAdmin(String message) {
        return article(
                b("Admin says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    public static String HTMLErrorMessage(String message) {
        return article(
                b("Error!"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    public static void addNewChatSession(WsContext ctx) {
        User user  = ctx.sessionAttribute("currentUser");

        userUsernameMap.put(ctx, user);
    }

    public static Map<WsContext, User> getActiveChats() {
        return userUsernameMap;
    }

    public static WsContext getChatContext(int hashcode) {
        for (WsContext context : userUsernameMap.keySet()) {
            System.out.println("Checking context: " + context.hashCode() + " against " + hashcode);
            if (context.hashCode() == hashcode) {
                return context;
            }
        }

        System.out.println("No chat context found for hashcode " + hashcode);
        return null;
    }

    public static User getUserFromContext(String customerChatName) {
        for (Map.Entry<WsContext, User> entry : userUsernameMap.entrySet()) {
            User user = entry.getValue();
            if (user.getFullName().equals(customerChatName)) {
                return user;
            }
        }
        return null; // or throw an exception if user not found
    }

    public static WsContext getContextByUser(User user) {
        for (Map.Entry<WsContext, User> entry : userUsernameMap.entrySet()) {
            if (entry.getValue().equals(user)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
