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

    public static String createHtmlMessageFromUser(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message).withId("customerMessage")
        ).render();
    }

    public static String createHtmlMessageFromAdmin(String message) {
        return article(
                b("Admin says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message).withId("adminMessage")
        ).render();
    }

    public static String HTMLErrorMessage(String message) {
        return article(
                b("Error!"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message).withId("error")
        ).render();
    }

    public static void addNewChatSession(WsContext ctx) {
        User user  = ctx.sessionAttribute("currentUser");
        userUsernameMap.put(ctx, user);
    }

    public static Map<WsContext, User> getActiveChats() {
        return userUsernameMap;
    }

    public static User getUserFromContext(String customerChatName) {
        for (Map.Entry<WsContext, User> entry : userUsernameMap.entrySet()) {
            User user = entry.getValue();
            if (user.getFullName().equals(customerChatName)) {
                return user;
            }
        }
        return null;
    }

    public static WsContext getContextByUser(User user) {
        for (Map.Entry<WsContext, User> entry : userUsernameMap.entrySet()) {
            if (entry.getValue().equals(user)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static User getAdminUser() {
        for (Map.Entry<WsContext, User> entry : userUsernameMap.entrySet()) {
            User user = entry.getValue();
            if (user.isAdmin()) {
                return user;
            }
        }
        return null;
    }
}
