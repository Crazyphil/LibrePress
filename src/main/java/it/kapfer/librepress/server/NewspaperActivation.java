package it.kapfer.librepress.server;

/**
 * Represents a newspaper issue pushed to a device by the NewspaperDirect API. The information contained within can be used to activate and download the
 * newspaper issue to the device.
 *
 * @param messageId  the ID of the message, can be used to delete the message
 * @param title      title of the newspaper to be downloaded
 * @param issueId    internal ID of the individual issue that should be downloaded
 * @param licenseUrl URL to the activation endpoint that must be called to activate and download the newspaper issue
 */
public record NewspaperActivation(int messageId, String title, String issueId, String licenseUrl) implements DeletableMessage {
}
