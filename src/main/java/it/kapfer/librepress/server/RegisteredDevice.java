package it.kapfer.librepress.server;

/**
 * Represents a device that has been registered for the given user. This device can be used to download newspaper issues.
 * <p>
 * The {@link #deviceId()} must not be confused with {@link DeviceRegistration#clientId()}, and they cannot be used interchangeably.
 *
 * @param clientName      user-facing name of the client
 * @param deviceId        unique ID of the device assigned during registration
 * @param activationToken UUID assigned to the client during registration
 */
public record RegisteredDevice(String clientName, int deviceId, String activationToken) {
}
