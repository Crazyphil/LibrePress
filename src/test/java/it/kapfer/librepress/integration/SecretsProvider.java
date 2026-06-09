package it.kapfer.librepress.integration;

import it.kapfer.librepress.server.Credentials;
import it.kapfer.librepress.server.DeviceRegistration;

import java.io.IOException;
import java.util.Properties;

class SecretsProvider {
    private static SecretsProvider instance;

    private final Properties secrets;

    private SecretsProvider() {
        secrets = new Properties();
        try (var secretsFile = getClass().getResourceAsStream("credentials.secrets")) {
            if (secretsFile != null) {
                secrets.load(secretsFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load secrets, should not occur", e);
        }
    }

    public static SecretsProvider get() {
        if (instance == null) {
            instance = new SecretsProvider();
        }
        return instance;
    }

    public Credentials getCredentials() {
        return new Credentials(getUsername(), getPassword());
    }

    public DeviceRegistration getDeviceRegistration() {
        return new DeviceRegistration(getActivationToken(), getClientId());
    }

    private String getUsername() {
        String username = secrets.getProperty("username");
        if (username == null) {
            return System.getenv("CREDENTIALS_USERNAME");
        }
        return username;
    }

    private char[] getPassword() {
        String password = secrets.getProperty("password");
        if (password == null) {
            password = System.getenv("CREDENTIALS_PASSWORD");
        }
        return password != null ? password.toCharArray() : new char[0];
    }

    private String getActivationToken() {
        String activationToken = secrets.getProperty("activationToken");
        if (activationToken == null) {
            return System.getenv("CREDENTIALS_ACTIVATION_TOKEN");
        }
        return activationToken;
    }

    private int getClientId() {
        String clientId = secrets.getProperty("clientId");
        if (clientId == null) {
            clientId = System.getenv("CREDENTIALS_CLIENT_ID");
        }
        return clientId != null ? Integer.parseInt(clientId) : 0;
    }

    public String getLicenseUrl() {
        String licenseUrl = secrets.getProperty("licenseUrl");
        if (licenseUrl == null) {
            licenseUrl = System.getenv("CREDENTIALS_LICENSE_URL");
        }
        return licenseUrl;
    }
}
