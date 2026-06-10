package it.kapfer.librepress.server;

/**
 * Represents a newspaper issue pushed to a device by the NewspaperDirect API. The information contained within can be used to activate and download the
 * newspaper issue to the device.
 */
public class NewspaperActivation extends DeletableMessage {
    private final String title;
    private final String issueId;
    private final String licenseUrl;

    /**
     * Creates a new newspaper activation object.
     *
     * @param messageId  the ID of the message, can be used to delete the message
     * @param title      title of the newspaper to be downloaded
     * @param issueId    internal ID of the individual issue that should be downloaded
     * @param licenseUrl URL to the activation endpoint that must be called to activate and download the newspaper issue
     */
    public NewspaperActivation(int messageId, String title, String issueId, String licenseUrl) {
        super(messageId);
        this.title = title;
        this.issueId = issueId;
        this.licenseUrl = licenseUrl;
    }

    /**
     * {@return title of the newspaper to be downloaded}
     */
    public String title() {
        return title;
    }

    /**
     * {@return internal ID of the individual issue that should be downloaded}
     */
    public String issueId() {
        return issueId;
    }

    /**
     * {@return URL to the activation endpoint that must be called to activate and download the newspaper issue}
     */
    public String licenseUrl() {
        return licenseUrl;
    }
}
