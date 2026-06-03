package it.kapfer.librepress.server;

/**
 * Marker interface for objects that originated from a server-side message that has to be deleted after executing what the message tells the client to do.
 */
public interface DeletableMessage {
    /**
     * {@return the originating message ID on the server}
     */
    int messageId();
}
