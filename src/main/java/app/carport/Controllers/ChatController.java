package app.carport.Controllers;

import app.carport.Persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

/*
TODO:
 1. Fix Javascript og HTML filer (adminchat & userchat)
 2. Find ud af hvordan, man adder en popup.
 3. Add en notifikation når der kommer en ny besked.
 4. Sørg for at der kun er en bruger og admin per chat.
 5. Skal jeg bruge noget andet en WsConnectContext? wsMessageContext?
 6. Sørg for at Ws først bliver aktiv, når chatten åbnes.
 7. Sørg for at ws lukker på sluttet session. (Når pop-up vinduet lukkes?)
 8. Skal der laves en controller specifikt til AdminChat?
 9. Lav en userstory til chat.
 10. Lav et diagram med D2, der viser hvordan classes/javascript taler på tværs af client/server.
*/

public class ChatController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.ws("/websocket", ws -> {

            // Customer actions
            ws.onConnect(ctx -> onConnect(ctx));
            ws.onMessage(ctx -> onMessage(ctx));
            ws.onClose(ctx -> onClose(ctx));
            ws.onError(ctx -> onError(ctx));
        });


    }

    private static void onConnect(WsConnectContext ctx) {
        // Handle new WebSocket connections



    }

    private static void onMessage(WsMessageContext ctx) {
        // Handle messages received from the client
        ctx.send("message");
    }

    private static void onClose(WsCloseContext ctx) {
        // Handle WebSocket connection close
    }

    private static void onError(WsErrorContext ctx) {
        // Handle errors
        ctx.closeSession();
    }
}
