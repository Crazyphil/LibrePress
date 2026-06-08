package it.kapfer.librepress.server;

public class ActivationServiceTestInitializer {
    public static ActivationService withCustomRequestExecutor(MockRequestExecutor mockRequestExecutor) {
        return new ActivationService(mockRequestExecutor);
    }
}
