package it.kapfer.librepress.server;

import it.kapfer.librepress.server.exception.HttpException;
import it.kapfer.librepress.server.xml.Request;
import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;
import it.kapfer.librepress.server.xml.authentication.UsernameActivationAuthentication;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordAuthentication;
import it.kapfer.librepress.server.xml.authentication.UsernamePasswordClientAuthentication;
import it.kapfer.librepress.server.xml.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceRegistrationServiceTest {

    private static final Credentials AUTH = new Credentials("user@example.com", "secret".toCharArray());
    private static final NewspaperProvider PROVIDER = new NewspaperProvider("PressDisplay.com", "PressReader");
    private static final String ACTIVATION_TOKEN = "deadbeef-15f2-4681-aa47-75551b48f42c";
    private static final int CLIENT_ID = 12345678;
    private static final DeviceRegistration REGISTRATION = new DeviceRegistration(ACTIVATION_TOKEN, CLIENT_ID);

    @Mock
    private RequestExecutor requestExecutor;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    private DeviceRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new DeviceRegistrationService(requestExecutor);
    }

    @Nested
    class GetAvailableProviders {

        @Test
        void mapsServicesCorrectly() {
            var serviceEntry = new GetServicesResponse.ServiceName();
            serviceEntry.id = "PressDisplay.com";
            serviceEntry.displayName = "PressReader";
            var response = new GetServicesResponse();
            response.services = List.of(serviceEntry);
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            List<NewspaperProvider> result = service.getAvailableProviders(AUTH).join();

            assertEquals(1, result.size());
            assertEquals("PressDisplay.com", result.get(0).serviceId());
            assertEquals("PressReader", result.get(0).displayName());
        }

        @Test
        void propagatesErrorResponse() {
            var errorResponse = new ErrorResponse();
            errorResponse.errorCode = 401;
            errorResponse.errorMessage = "The login name or password you entered is incorrect";
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new HttpException(errorResponse)));

            var availableProvidersCall = service.getAvailableProviders(AUTH);
            CompletionException e = assertThrows(CompletionException.class, availableProvidersCall::join);

            assertInstanceOf(HttpException.class, e.getCause());
            HttpException httpException = (HttpException) e.getCause();
            assertEquals(httpException.getErrorMessage(), errorResponse.errorMessage);
            assertEquals(httpException.getErrorCode(), errorResponse.errorCode);
        }

        @Test
        void usesUsernamePasswordAuthentication() {
            var response = new GetServicesResponse();
            response.services = List.of();
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            service.getAvailableProviders(AUTH).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), any());
            var request = requestCaptor.getValue();
            assertInstanceOf(UsernamePasswordAuthentication.class, request.authentication);
            var auth = (UsernamePasswordAuthentication) request.authentication;
            assertEquals(AUTH.username(), auth.userName);
            assertEquals(new String(AUTH.password()), auth.userPassword);
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            var getAvailableProvidersCall = service.getAvailableProviders(AUTH);

            CompletionException e = assertThrows(CompletionException.class, getAvailableProvidersCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }
    }

    @Nested
    class RegisterDevice {

        @Test
        void returnsDeviceRegistrationWithGeneratedClientId() {
            var response = new RegisterClientResponse();
            response.activationNumber = ACTIVATION_TOKEN;
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            DeviceRegistration registration = service.registerDevice(AUTH, PROVIDER, "MyDevice").join();

            assertNotNull(registration);
            assertEquals(ACTIVATION_TOKEN, registration.activationToken());
            // clientId is generated by Random; verify it is consistent with the request's authentication
            verify(requestExecutor).executeRequest(requestCaptor.capture(), any());
            var request = requestCaptor.getValue();
            assertInstanceOf(UsernamePasswordClientAuthentication.class, request.authentication);
            var auth = (UsernamePasswordClientAuthentication) request.authentication;
            assertEquals(registration.clientId(), auth.clientNumber.intValue());
        }

        @Test
        void usesUsernamePasswordClientAuthenticationWithCorrectCredentials() {
            var response = new RegisterClientResponse();
            response.activationNumber = ACTIVATION_TOKEN;
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            service.registerDevice(AUTH, PROVIDER, "MyDevice").join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), any());
            var request = requestCaptor.getValue();
            assertInstanceOf(UsernamePasswordClientAuthentication.class, request.authentication);
            var auth = (UsernamePasswordClientAuthentication) request.authentication;
            assertEquals(AUTH.username(), auth.userName);
            assertEquals(new String(AUTH.password()), auth.userPassword);
        }

        @Test
        void passesServiceIdAndDeviceNameToRequest() {
            var response = new RegisterClientResponse();
            response.activationNumber = ACTIVATION_TOKEN;
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            service.registerDevice(AUTH, PROVIDER, "MyDevice").join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), any());
            var request = (it.kapfer.librepress.server.xml.request.RegisterClientRequest) requestCaptor.getValue();
            assertEquals("PressDisplay.com", request.serviceName);
            assertEquals("MyDevice", request.clientName);
        }

        @Test
        void propagatesErrorResponse() {
            var errorResponse = new ErrorResponse();
            errorResponse.errorCode = 401;
            errorResponse.errorMessage = "The login name or password you entered is incorrect";
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new HttpException(errorResponse)));

            var registerDeviceCall = service.registerDevice(AUTH, PROVIDER, "MyDevice");

            CompletionException e = assertThrows(CompletionException.class, registerDeviceCall::join);
            assertInstanceOf(HttpException.class, e.getCause());
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            var registerDeviceCall = service.registerDevice(AUTH, PROVIDER, "MyDevice");

            CompletionException e = assertThrows(CompletionException.class, registerDeviceCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }
    }

    @Nested
    class GetRegisteredDevices {

        @Test
        void mapsClientListCorrectly() {
            var activation1 = new GetClientListResponse.Activation();
            activation1.id = 12345678;
            activation1.clientName = "Static Test Registration";
            activation1.activationNumber = "a3ec99d2-b8e6-416f-99b4-18de84802fe9";

            var activation2 = new GetClientListResponse.Activation();
            activation2.id = 98765432;
            activation2.clientName = "Another Test Registration";
            activation2.activationNumber = "6427e587-0cb4-477a-badb-060eb2dd7c5a";

            var activations = new GetClientListResponse.Activations();
            activations.activations = List.of(activation1, activation2);

            var response = new GetClientListResponse();
            response.activations = activations;
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            List<RegisteredDevice> result = service.getRegisteredDevices(REGISTRATION).join();

            assertEquals(2, result.size());
            assertEquals(activation1.clientName, result.get(0).clientName());
            assertEquals(activation1.activationNumber, result.get(0).activationToken());
            assertEquals(activation1.id, result.get(0).deviceId());
            assertEquals(activation2.clientName, result.get(1).clientName());
            assertEquals(activation2.activationNumber, result.get(1).activationToken());
            assertEquals(activation2.id, result.get(1).deviceId());
        }

        @Test
        void usesActivationAuthentication() {
            var response = new GetClientListResponse();
            response.activations = new GetClientListResponse.Activations();
            response.activations.activations = List.of();
            when(requestExecutor.executeRequest(any(), any())).thenReturn(CompletableFuture.completedFuture(response));

            service.getRegisteredDevices(REGISTRATION).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), any());
            var request = requestCaptor.getValue();
            assertInstanceOf(ActivationAuthentication.class, request.authentication);
            var auth = (ActivationAuthentication) request.authentication;
            assertEquals(REGISTRATION.clientId(), auth.clientNumber.intValue());
            assertEquals(REGISTRATION.activationToken(), auth.activationNumber);
        }

        @Test
        void propagatesErrorResponse() {
            var errorResponse = new ErrorResponse();
            errorResponse.errorCode = 203;
            errorResponse.errorMessage = "Unable to find activation";
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new HttpException(errorResponse)));

            var getRegisteredDevicesCall = service.getRegisteredDevices(REGISTRATION);

            CompletionException e = assertThrows(CompletionException.class, getRegisteredDevicesCall::join);
            assertInstanceOf(HttpException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("Unable to find activation"));
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            var getRegisteredDevicesCall = service.getRegisteredDevices(REGISTRATION);

            CompletionException e = assertThrows(CompletionException.class, getRegisteredDevicesCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }
    }

    @Nested
    class UnregisterDevice {

        @Test
        void returnsVoidOnSuccess() {
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(new EmptyResponse()));

            var unregisterDeviceCall = service.unregisterDevice(AUTH.username(), REGISTRATION);

            assertDoesNotThrow(unregisterDeviceCall::join);
        }

        @Test
        void usesUsernameActivationAuthentication() {
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(new EmptyResponse()));

            service.unregisterDevice(AUTH.username(), REGISTRATION).join();

            verify(requestExecutor).executeRequest(requestCaptor.capture(), any());
            var request = requestCaptor.getValue();
            assertInstanceOf(UsernameActivationAuthentication.class, request.authentication);
            var auth = (UsernameActivationAuthentication) request.authentication;
            assertEquals(AUTH.username(), auth.userName);
            assertEquals(REGISTRATION.clientId(), auth.clientNumber.intValue());
            assertEquals(REGISTRATION.activationToken(), auth.activationNumber);
        }

        @Test
        void propagatesErrorResponse() {
            var errorResponse = new ErrorResponse();
            errorResponse.errorCode = 401;
            errorResponse.errorMessage = "The login name or password you entered is incorrect";
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new HttpException(errorResponse)));

            var unregisterDeviceCall = service.unregisterDevice(AUTH.username(), REGISTRATION);

            CompletionException e = assertThrows(CompletionException.class, unregisterDeviceCall::join);
            assertInstanceOf(HttpException.class, e.getCause());
        }

        @Test
        void wrapsExecutorException() {
            when(requestExecutor.executeRequest(any(), any()))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("network error")));

            var unregisterDeviceCall = service.unregisterDevice(AUTH.username(), REGISTRATION);

            CompletionException e = assertThrows(CompletionException.class, unregisterDeviceCall::join);
            assertEquals("network error", e.getCause().getMessage());
        }
    }
}
