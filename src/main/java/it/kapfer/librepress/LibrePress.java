package it.kapfer.librepress;

import it.kapfer.librepress.pdf.NewspaperReader;
import it.kapfer.librepress.server.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This is the central one-stop shop for using the LibrePress library. It manages devices, allows receiving push messages for newspaper downloads for offline
 * reading, activates the downloaded newspaper issues and provides a reader for the PDF files. For advanced use cases with more fine-grained flow control, use
 * the individual services gathered by this entry point.
 */
public class LibrePress {
    private final DeviceRegistrationService deviceRegistrationService;
    private final MessageService messageService;
    private final ActivationService activationService;

    /**
     * Creates a new instance of the {@link LibrePress} class.
     */
    public LibrePress() {
        deviceRegistrationService = DeviceRegistrationService.createService();
        messageService = MessageService.createService();
        activationService = ActivationService.createService();
    }

    /**
     * Registers a new device for the given user's account with the name provided. The returned {@link DeviceRegistration} object can be used to retrieve
     * pushed newspaper issues and activate them for offline reading.
     * <p>
     * If the user has multiple newspaper providers, the first one is used. If the user has <em>no</em> newspaper providers (which should never occur), {@code
     * null} is returned instead.
     *
     * @param credentials username and password of the user to authenticate to
     * @param deviceName  display name of the device, visible to the user in their account's device management and when pushing newspapers
     * @return the device registration, which can be used to retrieve pushed newspaper issues and activate them for offline reading. If the user has no
     * newspaper providers available (which should never occur), {@code null} is returned instead. Store the information in this object between application runs.
     */
    public CompletableFuture<DeviceRegistration> registerDevice(Credentials credentials, String deviceName) {
        return deviceRegistrationService.getAvailableProviders(credentials)
                .thenCompose(np -> registerForFirstProvider(credentials, deviceName, np));
    }

    private CompletableFuture<DeviceRegistration> registerForFirstProvider(Credentials credentials, String deviceName, List<NewspaperProvider> newspaperProviders) {
        if (newspaperProviders.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return deviceRegistrationService.registerDevice(credentials, newspaperProviders.get(0), deviceName);
    }

    /**
     * Unregisters a device from the user's account. The device will no longer receive push messages for newspaper issues and the device registration can no
     * longer be used to access the user's account data.
     *
     * @param username           the username of the account to unregister the device from
     * @param deviceRegistration the device registration of the device to unregister
     * @return a completable future that completes without exception when the registration is removed successfully
     */
    public CompletableFuture<Void> unregisterDevice(String username, DeviceRegistration deviceRegistration) {
        return deviceRegistrationService.unregisterDevice(username, deviceRegistration);
    }

    /**
     * Checks the server for newspapers that have been pushed to the device and are available to be downloaded. The returned {@link NewspaperIssue} objects
     * are already activated, so the objects can be used directly to download and open the PDF files.
     *
     * @param deviceRegistration the device for which to check for pushed newspapers
     * @return a list of newspaper issues that have been pushed to the device and are available to be downloaded
     */
    public CompletableFuture<Collection<NewspaperIssue>> retrievePushedNewspapers(DeviceRegistration deviceRegistration) {
        return messageService.getPushedNewspapers(deviceRegistration)
                .thenCompose(na -> activateNewspapers(deviceRegistration, na))
                .thenApply(na -> deleteActivatedNewspaperMessages(deviceRegistration, na));
    }

    private CompletableFuture<Map<NewspaperActivation, NewspaperIssue>> activateNewspapers(DeviceRegistration deviceRegistration, List<NewspaperActivation> newspaperActivations) {
        var activations = newspaperActivations.stream()
                .map(a -> Map.entry(a, activationService.activatePushedNewspaper(deviceRegistration, a)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return CompletableFuture.allOf(activations.values().toArray(new CompletableFuture[0]))
                .thenApply(unused -> activations.entrySet().stream()
                        .map(a -> Map.entry(a.getKey(), a.getValue().join()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private Collection<NewspaperIssue> deleteActivatedNewspaperMessages(DeviceRegistration deviceRegistration, Map<NewspaperActivation, NewspaperIssue> activatedNewspapers) {
        messageService.deleteMessages(deviceRegistration, new ArrayList<>(activatedNewspapers.keySet()));
        return activatedNewspapers.values();
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
        return activationService.openNewspaper(newspaperIssue);
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
        return activationService.openNewspaper(newspaperIssue, outputFile);
    }
}
