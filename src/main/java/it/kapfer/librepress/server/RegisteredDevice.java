package it.kapfer.librepress.server;

/**
 * Represents a device that has been registered for the given user. This device can be used to download newspaper issues.
 * <p>
 * The {@link #deviceId()} must not be confused with {@link DeviceRegistration#clientId()}, and they cannot be used interchangeably.
 */
public class RegisteredDevice {
    private final String clientName;
    private final int deviceId;
    private final String activationToken;

    /**
     * Creates a new registered device object.
     *
     * @param clientName      user-facing name of the client
     * @param deviceId        unique ID of the device assigned during registration
     * @param activationToken UUID assigned to the client during registration
     */
    public RegisteredDevice(String clientName, int deviceId, String activationToken) {
        this.clientName = clientName;
        this.deviceId = deviceId;
        this.activationToken = activationToken;
    }

    /**
     * {@return user-facing name of the client}
     */
    public String clientName() {
        return clientName;
    }

    /**
     * {@return unique ID of the device assigned during registration}
     */
    public int deviceId() {
        return deviceId;
    }

    /**
     * {@return UUID assigned to the client during registration}
     */
    public String activationToken() {
        return activationToken;
    }
}
