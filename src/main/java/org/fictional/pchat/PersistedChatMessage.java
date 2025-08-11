package org.fictional.pchat;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

/**
 * Represents a chat message to be persisted in a database.
 * Extends PanacheEntity so that we get a bunch of helper methods for free without additional boilerplate.
 */
@Entity
public class PersistedChatMessage extends PanacheEntity {

    // The username of the sender
    public String username;

    // The content of the message
    public String message;

    // The timestamp when the message was sent
    public LocalDateTime timestamp;

    /**
     * Default constructor for JPA.
     */
    public PersistedChatMessage() {
    }

    /**
     * Constructor to create a new chat message.
     * @param username The username of the sender.
     * @param message The content of the message.
     * @param timestamp The timestamp of the message.
     */
    public PersistedChatMessage(String username, String message, LocalDateTime timestamp) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

   @Override
    public String toString() {
        return "PersistedChatMessage{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", message='" + message + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}
