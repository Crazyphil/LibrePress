package it.kapfer.librepress.server;

import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;
import it.kapfer.librepress.server.xml.authentication.UsernameActivationAuthentication;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordAuthentication;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordClientAuthentication;
import it.kapfer.librepress.server.xml.request.GetClientListRequest;
import it.kapfer.librepress.server.xml.request.GetServicesRequest;
import it.kapfer.librepress.server.xml.request.RegisterClientRequest;
import it.kapfer.librepress.server.xml.request.UnregisterClientRequest;
import it.kapfer.librepress.server.xml.response.EmptyResponse;
import it.kapfer.librepress.server.xml.response.GetClientListResponse;
import it.kapfer.librepress.server.xml.response.GetServicesResponse;
import it.kapfer.librepress.server.xml.response.RegisterClientResponse;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * This service handles registering and deregistering devices within the NewspaperDirect platform. Registered devices can use their registration information
 * instead of username and password authentication when accessing the API, and users can push newspaper issues to individual devices for offline consumption.
 */
public class DeviceRegistrationService {
    private final Random random = new Random();
    private final RequestExecutor requestExecutor;

    DeviceRegistrationService(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    /**
     * {@return a new instance of the service}
     */
    public static DeviceRegistrationService createService() {
        return new DeviceRegistrationService(new RequestExecutor());
    }

    /**
     * {@return a list of available newspaper providers for the given user} This list can be used to register a device for a specific provider. Applications
     * should let the user select a provider from this list.
     *
     * @param credentials the credentials of the user to authenticate with
     */
    public CompletableFuture<List<NewspaperProvider>> getAvailableProviders(Credentials credentials) {
        UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(credentials.username(), new String(credentials.password()));
        GetServicesRequest getServicesRequest = new GetServicesRequest(authentication);

        return requestExecutor.executeRequest(getServicesRequest, GetServicesResponse.class)
                .thenApply(this::mapNewspaperProviders);
    }

    private List<NewspaperProvider> mapNewspaperProviders(GetServicesResponse response) {
        return response.services.stream()
                .map(p -> new NewspaperProvider(p.id, p.displayName))
                .toList();
    }

    /**
     * Registers a new device for the given user and newspaper provider. The device can then be used by the user to push newspaper issues to the device for
     * offline consumption.
     *
     * @param credentials       the credentials of the user to authenticate with
     * @param newspaperProvider the newspaper provider to register the device for
     * @param deviceName        the name of the device to register, this name is visible to the user in NewspaperDirect web interfaces
     * @return the registration information under which the device was registered
     */
    public CompletableFuture<DeviceRegistration> registerDevice(Credentials credentials, NewspaperProvider newspaperProvider, String deviceName) {
        int clientId = random.nextInt();
        UsernamePasswordClientAuthentication authentication = new UsernamePasswordClientAuthentication(credentials.username(), new String(credentials.password()), clientId);
        RegisterClientRequest registerClientRequest = new RegisterClientRequest(authentication, newspaperProvider.serviceId(), deviceName);

        return requestExecutor.executeRequest(registerClientRequest, RegisterClientResponse.class)
                .thenApply(r -> mapDeviceRegistration(r, clientId));
    }

    private DeviceRegistration mapDeviceRegistration(RegisterClientResponse response, int clientId) {
        return new DeviceRegistration(response.activationNumber, clientId);
    }

    /**
     * {@return a list of devices registered for the given user} This list can be used to manage registered devices, but it does <em>not</em> contain all
     * necessary data to actually construct a {@link DeviceRegistration} from it, i.e. the information contained in the list doesn't allow authentication.
     *
     * @param deviceRegistration device registration to use for authentication
     */
    public CompletableFuture<List<RegisteredDevice>> getRegisteredDevices(DeviceRegistration deviceRegistration) {
        ActivationAuthentication authentication = new ActivationAuthentication(deviceRegistration.clientId(), deviceRegistration.activationToken());
        GetClientListRequest getClientListRequest = new GetClientListRequest(authentication);

        return requestExecutor.executeRequest(getClientListRequest, GetClientListResponse.class)
                .thenApply(this::mapClientList);
    }

    private List<RegisteredDevice> mapClientList(GetClientListResponse response) {
        return response.activations.activations.stream()
                .map(a -> new RegisteredDevice(a.clientName, a.id, a.activationNumber))
                .toList();
    }

    /**
     * Deregisters a device from the NewspaperDirect platform. This will remove the device from the user's list of registered devices and prevent the device
     * from logging in using its device registration information, and the user won't be able to send a newspaper issue to the device anymore.
     *
     * @param username           the username of the user to remove the device registration from. The device must have been registered previously for this user
     * @param deviceRegistration the device registration to remove
     * @return a completable future that finishes without an exception if the operation succeeds
     */
    public CompletableFuture<Void> unregisterDevice(String username, DeviceRegistration deviceRegistration) {
        UsernameActivationAuthentication authentication = new UsernameActivationAuthentication(username, deviceRegistration.clientId(), deviceRegistration.activationToken());
        UnregisterClientRequest unregisterClientRequest = new UnregisterClientRequest(authentication);

        return requestExecutor.executeRequest(unregisterClientRequest, EmptyResponse.class)
                .thenApply(r -> null);
    }
}
