package it.kapfer.librepress.integration;

import it.kapfer.librepress.server.*;
import it.kapfer.librepress.server.exception.HttpException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class DeviceRegistrationServiceIntegrationTest {
    private static final Credentials VALID_CREDENTIALS = SecretsProvider.get().getCredentials();
    private static final Credentials INVALID_CREDENTIALS = new Credentials("invalid@example.com", "wrongpassword".toCharArray());
    private static final DeviceRegistration DEVICE_REGISTRATION = SecretsProvider.get().getDeviceRegistration();

    private DeviceRegistrationService deviceRegistrationService;

    @BeforeEach
    void beforeEach() {
        deviceRegistrationService = DeviceRegistrationService.createService();
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.0.8.503</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <error-code>401</error-code>
     *         <error-message>The login name or password you entered is incorrect</error-message>
     *         <error-help-url />
     *     </response>
     * </nd>
     * </pre>
     */
    @Test
    void getAvailableProviders_withInvalidCredentials_throwsException() {
        var availableProvidersRequest = deviceRegistrationService.getAvailableProviders(INVALID_CREDENTIALS);

        CompletionException e = assertThrows(CompletionException.class, availableProvidersRequest::join);

        assertInstanceOf(HttpException.class, e.getCause());
        assertTrue(e.getCause().getMessage().contains("The login name or password you entered is incorrect"));
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.0.8.503</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <service-name display-service-name="PressReader">PressDisplay.com</service-name>
     *     </response>
     * </nd>
     * </pre>
     */
    @Test
    void getAvailableProviders_withValidCredentials_returnsProviders() {
        List<NewspaperProvider> newspaperProviders = deviceRegistrationService.getAvailableProviders(VALID_CREDENTIALS)
                .join();

        assertEquals(1, newspaperProviders.size());
        NewspaperProvider newspaperProvider = newspaperProviders.get(0);
        assertEquals("PressDisplay.com", newspaperProvider.serviceId());
        assertEquals("PressReader", newspaperProvider.displayName());
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.0.8.503</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <error-code>203</error-code>
     *         <error-message>Unable to find activation</error-message>
     *         <error-help-url />
     *     </response>
     * </nd>
     * </pre>
     */
    @Test
    void getRegisteredDevices_withInvalidRegistration_throwsException() {
        DeviceRegistration registration = new DeviceRegistration(UUID.randomUUID().toString(), 0);

        var registeredDevicesRequest = deviceRegistrationService.getRegisteredDevices(registration);
        Exception e = assertThrows(CompletionException.class, registeredDevicesRequest::join);

        assertInstanceOf(HttpException.class, e.getCause());
        assertTrue(e.getCause().getMessage().contains("Unable to find activation"));
    }

    /*
     * Response:
     * <pre>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <nd version="1.0">
     *     <versions>
     *         <required-version>5.10.0426.0</required-version>
     *         <latest-version>6.20.1118</latest-version>
     *     </versions>
     *     <response id="0">
     *         <activations>
     *             <activation id="12345678" client-name="Static Test Registration" activation-number="c192b580-febb-445d-bec7-7953b6084aad" />
     *             <activation id="98765432" client-name="TestRegistration" activation-number="52735991-57af-43c5-aebb-75551b48f42c" />
     *         </activations>
     *     </response>
     * </nd>
     * </pre>
     */
    @Test
    void getRegisteredDevices_withValidRegistration_returnsRegisteredDevices() {
        List<RegisteredDevice> newspaperProviders = deviceRegistrationService.getRegisteredDevices(DEVICE_REGISTRATION).join();

        assertFalse(newspaperProviders.isEmpty());
        assertEquals("Static Test Registration", newspaperProviders.get(0).clientName());
    }

    @Nested
    class WithDeviceRegistration {
        private DeviceRegistration deviceRegistration;

        @AfterEach
        void removeDeviceRegistration() {
            if (deviceRegistration != null) {
                deviceRegistrationService.unregisterDevice(VALID_CREDENTIALS.username(), deviceRegistration).join();
            }
        }

        /*
         * Response:
         * <pre>
         * <?xml version="1.0" encoding="UTF-8"?>
         * <nd version="1.0">
         *     <versions>
         *         <required-version>5.0.8.503</required-version>
         *         <latest-version>6.20.1118</latest-version>
         *     </versions>
         *     <response id="0">
         *         <error-code>401</error-code>
         *         <error-message>The login name or password you entered is incorrect</error-message>
         *         <error-help-url />
         *     </response>
         * </nd>
         * </pre>
         */
        @Test
        void registerDevice_withInvalidCredentials_throwsException() {
            var registerDeviceRequest = deviceRegistrationService.getAvailableProviders(INVALID_CREDENTIALS);

            CompletionException e = assertThrows(CompletionException.class, registerDeviceRequest::join);

            assertInstanceOf(HttpException.class, e.getCause());
        }

        /*
         * Response:
         * <pre>
         * <?xml version="1.0" encoding="UTF-8"?>
         * <nd version="1.0">
         *     <versions>
         *         <required-version>5.0.8.503</required-version>
         *         <latest-version>6.20.1118</latest-version>
         *     </versions>
         *     <response id="0">
         *         <activation-number>6abd3a74-f879-4359-b2c9-8f061802a960</activation-number>
         *         <service-name>PressDisplay.com</service-name>
         *         <display-service-name>PressReader</display-service-name>
         *         <service-url>http://www.pressdisplay.com/</service-url>
         *         <baseapplication-url>http://www.pressdisplay.com/pressdisplay/</baseapplication-url>
         *         <activation-id>12345678</activation-id>
         *         <user-profile>
         *             <status>1</status>
         *             <print-options>2</print-options>
         *             <first-name>Libre</first-name>
         *             <last-name>Press</last-name>
         *             <user-name>username@example.com</user-name>
         *             <logon-name>username@example.com</logon-name>
         *             <account-number>110101100</account-number>
         *         </user-profile>
         *     </response>
         * </nd>
         * </pre>
         */
        @Test
        void registerDevice_withValidCredentials_returnsDeviceRegistration() {
            List<NewspaperProvider> newspaperProviders = deviceRegistrationService.getAvailableProviders(VALID_CREDENTIALS).join();
            assertEquals(1, newspaperProviders.size());

            deviceRegistration = deviceRegistrationService.registerDevice(VALID_CREDENTIALS, newspaperProviders.get(0), "TestDevice")
                    .join();

            assertNotNull(deviceRegistration);
            assertNotNull(deviceRegistration.activationToken());
        }
    }
}
