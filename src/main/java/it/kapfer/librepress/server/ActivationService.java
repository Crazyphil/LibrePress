package it.kapfer.librepress.server;

import it.kapfer.librepress.drm.EncryptionKeyProvider;
import it.kapfer.librepress.pdf.NewspaperReader;
import it.kapfer.librepress.server.exception.NewspaperActivationException;
import it.kapfer.librepress.server.xml.ActivationResponse;

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
}
