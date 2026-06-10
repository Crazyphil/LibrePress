package it.kapfer.librepress.server;

import it.kapfer.librepress.pdf.NewspaperReader;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A newspaper issue that has been activated. Contains all necessary information to download the newspaper issue from the server and open it using the
 * {@link NewspaperReader}.
 */
public class NewspaperIssue {
    private final String issue;
    private final String title;
    private final LocalDateTime urlExpirationTime;
    private final List<String> downloadUrls;
    private final byte[] encryptionKey;

    /**
     * Creates a new newspaper issue object.
     *
     * @param issue             internal ID of the individual issue that was activated
     * @param title             title of the newspaper to be downloaded
     * @param urlExpirationTime the time after which the download URLs are no longer valid
     * @param downloadUrls      mirror URLs to download the newspaper issue from (each URL points to the identical file). The URLs are valid until the expiration time.
     * @param encryptionKey     the encryption key required to open the newspaper issue
     */
    public NewspaperIssue(String issue, String title, LocalDateTime urlExpirationTime, List<String> downloadUrls, byte[] encryptionKey) {
        this.issue = issue;
        this.title = title;
        this.urlExpirationTime = urlExpirationTime;
        this.downloadUrls = downloadUrls;
        this.encryptionKey = encryptionKey;
    }

    /**
     * {@return internal ID of the individual issue that was activated}
     */
    public String issue() {
        return issue;
    }

    /**
     * {@return title of the newspaper to be downloaded}
     */
    public String title() {
        return title;
    }

    /**
     * {@return the time after which the download URLs are no longer valid}
     */
    public LocalDateTime urlExpirationTime() {
        return urlExpirationTime;
    }

    /**
     * {@return mirror URLs to download the newspaper issue from (each URL points to the identical file). The URLs are valid until the expiration time.}
     */
    public List<String> downloadUrls() {
        return downloadUrls;
    }

    /**
     * {@return the encryption key required to open the newspaper issue}
     */
    public byte[] encryptionKey() {
        return encryptionKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NewspaperIssue that = (NewspaperIssue) o;
        return Objects.equals(issue, that.issue) && Objects.equals(title, that.title) && Arrays.equals(encryptionKey, that.encryptionKey)
                && Objects.equals(downloadUrls, that.downloadUrls) && Objects.equals(urlExpirationTime, that.urlExpirationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issue, title, urlExpirationTime, downloadUrls, Arrays.hashCode(encryptionKey));
    }

    @Override
    public String toString() {
        return "NewspaperIssue{" +
                "title='" + title + '\'' +
                ", issue='" + issue + '\'' +
                ", urlExpirationTime=" + urlExpirationTime +
                ", downloadUrls=" + downloadUrls +
                ", encryptionKey=" + "*".repeat(encryptionKey.length) +
                "}";
    }
}
