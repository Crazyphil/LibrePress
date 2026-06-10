package it.kapfer.librepress.server;

import java.util.Objects;

/**
 * Abstract base class for objects that originated from a server-side message that has to be deleted after executing what the message tells the client to do.
 */
public abstract class DeletableMessage {
    protected final int messageId;

    protected DeletableMessage(int messageId) {
        this.messageId = messageId;
    }

    /**
     * {@return the originating message ID on the server}
     */
    public int messageId() {
        return messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DeletableMessage that = (DeletableMessage) o;
        return messageId == that.messageId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(messageId);
    }
}
