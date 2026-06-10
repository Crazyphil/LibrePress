package it.kapfer.librepress.server;

import java.util.Objects;

/**
 * A service that provides newspapers to the user. The service is a public-facing consumer of NewspaperDirect's infrastructure, the most famous example
 * being PressReader.com.
 */
public class NewspaperProvider {
    private final String serviceId;
    private final String displayName;

    /**
     * Creates a new newspaper provider object.
     *
     * @param serviceId   internal unique identifier of the service
     * @param displayName user-facing name of the service to be displayed for example in selection menus
     */
    public NewspaperProvider(String serviceId, String displayName) {
        this.serviceId = serviceId;
        this.displayName = displayName;
    }

    /**
     * {@return internal unique identifier of the service}
     */
    public String serviceId() {
        return serviceId;
    }

    /**
     * {@return user-facing name of the service to be displayed for example in selection menus}
     */
    public String displayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NewspaperProvider that = (NewspaperProvider) o;
        return Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}