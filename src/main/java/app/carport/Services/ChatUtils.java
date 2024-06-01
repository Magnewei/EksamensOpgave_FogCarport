package app.carport.Services;

import io.javalin.websocket.WsContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

public class ChatUtils {

    private static final Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();

    // Builds a HTML element with a sender-name, a message, and a timestamp
    private static String createHtmlMessageFromUser(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    // Builds a HTML element with a sender-name, a message, and a timestamp
    private static String createHtmlMessageFromAdmin(String message) {
        return article(
                b("Admin" + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    public static Map getActiveChats() {
        return userUsernameMap;
    }

    public static void addNewChat(WsContext ctx, String username) {
        userUsernameMap.put(ctx, username);
    }

    public static void removeChat(WsContext ctx, String username) {
        userUsernameMap.remove(ctx, username);
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
