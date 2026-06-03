package it.kapfer.librepress.server;

/**
 * A service that provides newspapers to the user. The service is a public-facing consumer of NewspaperDirect's infrastructure, the most famous example
 * being PressReader.com.
 *
 * @param serviceId   internal unique identifier of the service
 * @param displayName user-facing name of the service to be displayed for example in selection menus
 */
public record NewspaperProvider(String serviceId, String displayName) {
}