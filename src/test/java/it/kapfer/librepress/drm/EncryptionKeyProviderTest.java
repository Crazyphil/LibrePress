package it.kapfer.librepress.drm;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class EncryptionKeyProviderTest {

    @ParameterizedTest
    @ValueSource(strings = {"brain-games.certificate", "guardian-weekly-2021-30.certificate"})
    void testRealCertificate(String testParameterFilename) throws IOException {
        try (InputStream certificateFile = getClass().getResourceAsStream(testParameterFilename)) {
            assumeFalse(certificateFile == null, "Certificate file not found in resources");

            Properties testProperties = new Properties();
            testProperties.load(certificateFile);

            int clientNumber = Integer.parseInt(testProperties.getProperty("clientNumber"));
            String clientAddress = testProperties.getProperty("clientAddress");
            String activationPassword = testProperties.getProperty("activationPassword");
            String encodedCertificate = testProperties.getProperty("certificate");

            EncryptionKeyProvider provider = new EncryptionKeyProvider(clientNumber, clientAddress);
            assertNotNull(provider.getEncryptionKey(encodedCertificate));
        }
    }
}
