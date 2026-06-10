package it.kapfer.librepress.server;

import it.kapfer.librepress.drm.EncryptionKeyProvider;
import it.kapfer.librepress.pdf.NewspaperReader;
import it.kapfer.librepress.server.exception.NewspaperActivationException;
import it.kapfer.librepress.server.xml.ActivationResponse;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

/**
 * This service handles activating the newspaper issues pushed to the device. The activation process is necessary to obtain the security data and encryption key
 * required to open the newspaper issue. Activation for a given newspaper issue can only be performed once.
 */
public class ActivationService {

    private final RequestExecutor requestExecutor;

    ActivationService(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    /**
     * {@return a new instance of the service}
     */
    public static ActivationService createService() {
        return new ActivationService(new RequestExecutor());
    }

    /**
     * Activates a newspaper issue previously pushed to the device. After completing, the result contains all necessary information to download the newspaper
     * issue from the server and open it using the {@link NewspaperReader}.
     * <p>
     * Activation of one individual {@link NewspaperActivation} object is possible only once, after which further attempts will throw a
     * {@link NewspaperActivationException}. The data contained within the returned object, however, is valid for longer, and even the download links can be
     * reused indefinitely until their expiration time.
     *
     * @param deviceRegistration  device for which to activate the newspaper, must be the same as the one the newspaper was pushed to
     * @param newspaperActivation the newspaper to activate
     * @return all necessary information to download the newspaper issue from the server and open it using the {@link NewspaperReader}
     */
    public CompletableFuture<NewspaperIssue> activatePushedNewspaper(DeviceRegistration deviceRegistration, NewspaperActivation newspaperActivation) {
        return requestExecutor.executeActivationRequest(newspaperActivation.licenseUrl(), deviceRegistration.activationToken(), deviceRegistration.clientId())
                .thenApply(ar -> mapNewspaperIssue(deviceRegistration, ar));
    }

    private NewspaperIssue mapNewspaperIssue(DeviceRegistration deviceRegistration, ActivationResponse response) {
        if (!response.statusCode.equals("Ok")) {
            throw new NewspaperActivationException(response);
        }

        EncryptionKeyProvider encryptionKeyProvider = new EncryptionKeyProvider(response.certificate, deviceRegistration.clientId(), null, null);
        return new NewspaperIssue(
                response.issue,
                response.documentInfo.title,
                response.urlExpirationTime,
                response.downloadUrls,
                encryptionKeyProvider.getEncryptionKey());
    }

    /**
     * Opens an activated newspaper issue for reading by automatically downloading it from the download URLs given in the object and returning a newspaper
     * reader handling the PDF file.
     * <p>
     * This method always directly operates on the download stream from the remote server, no caching takes place.
     *
     * @param newspaperIssue the newspaper issue to download and open
     * @return a newspaper reader for the downloaded PDF file
     */
    public CompletableFuture<NewspaperReader> openNewspaper(NewspaperIssue newspaperIssue) {
        return requestExecutor.executeDownloadRequest(newspaperIssue.downloadUrls())
                .thenApply(is -> new NewspaperReader(is, newspaperIssue.encryptionKey()));
    }

    /**
     * Opens an activated newspaper issue for reading by automatically downloading it from the download URLs given in the object and returning a newspaper
     * reader handling the PDF file.
     * <p>
     * This method first downloads the PDF file to the specified output file and then opens it from there. If the file already exists, all contents are
     * overwritten.
     *
     * @param newspaperIssue the newspaper issue to download and open
     * @param outputFile     file to which the downloaded PDF is written
     * @return a newspaper reader for the downloaded PDF file
     */
    public CompletableFuture<NewspaperReader> openNewspaper(NewspaperIssue newspaperIssue, File outputFile) {
        if (outputFile.exists() && !outputFile.isFile()) {
            throw new IllegalArgumentException("Output file must not be a directory");
        }

        return requestExecutor.executeDownloadRequest(newspaperIssue.downloadUrls())
                .thenApply(is -> writeInputStreamToFile(is, outputFile))
                .thenApply(f -> readFile(f, newspaperIssue));
    }

    private File writeInputStreamToFile(InputStream inputStream, File outputFile) {
        try (inputStream; var os = Files.newOutputStream(outputFile.toPath())) {
            inputStream.transferTo(os);
            return outputFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private NewspaperReader readFile(File inputFile, NewspaperIssue newspaperIssue) {
        try {
            return new NewspaperReader(inputFile, newspaperIssue.encryptionKey());
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}
