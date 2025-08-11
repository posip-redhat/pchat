package org.fictional.pchat;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.time.LocalDateTime;


/**
 * WebSocket endpoint for sample persistent  chat application.
 * Handles incoming messages, broadcasts them, and persists them to the database.
 */
@WebSocket(path = "/pchat/{username}")
public class AppWebSocket {

    private static final Logger LOG = Logger.getLogger(AppWebSocket.class);

  @Inject
    WebSocketConnection connection;  

    @OnOpen
    @Transactional
    public void onOpen() {
        PersistedChatMessage pcm = new PersistedChatMessage(connection.pathParam("username"),"Connected",LocalDateTime.now());
        pcm.persist();
        LOG.info("User connected: " +connection.pathParam("username"));
        //Send stuff out over wire
        // This has been simplified to have web-ui split on the ":" 
        // Original version used a record object. Since we are building Entity object to write to db I just make a parseable string
        connection.broadcast().sendTextAndAwait("SYSTEM" +": "+ pcm.toString());
    }

    @OnTextMessage
    @Transactional
    public void onTextMessage(String message) {
        LOG.info("Message from " +connection.pathParam("username") + ": " + message);

        // Create a new chat message entity
        PersistedChatMessage pcm = new PersistedChatMessage(connection.pathParam("username"), message, LocalDateTime.now());

        // Persist the message to the database
        pcm.persist();

        connection.broadcast().sendTextAndAwait(pcm.username +": "+ pcm.toString());
    }

    @OnClose
    @Transactional
    public void onClose() {
        LOG.info("User disconnected: " + connection.pathParam("username"));
        PersistedChatMessage pcm = new PersistedChatMessage(connection.pathParam("username"),"Disconnected",LocalDateTime.now());
	    pcm.persist();
        // Notify all other users about the departure
        connection.broadcast().sendTextAndAwait("SYSTEM" +": "+ pcm.toString());
    }



}

