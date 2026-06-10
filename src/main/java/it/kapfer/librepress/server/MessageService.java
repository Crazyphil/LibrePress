package it.kapfer.librepress.server;

import it.kapfer.librepress.server.xml.RequestMessage;
import it.kapfer.librepress.server.xml.authentication.ActivationAuthentication;
import it.kapfer.librepress.server.xml.message.NewspaperMessage;
import it.kapfer.librepress.server.xml.request.DeleteMessagesRequest;
import it.kapfer.librepress.server.xml.request.GetMessagesRequest;
import it.kapfer.librepress.server.xml.response.EmptyResponse;
import it.kapfer.librepress.server.xml.response.GetMessagesResponse;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This service handles receiving and deleting messages pushed to a registered device by the NewspaperDirect API.
 * <p>
 * Currently, this is used to get notifications about download requests by the user, which contain the data needed to actually download the newspaper issue.
 */
public class MessageService {
    private final RequestExecutor requestExecutor;

    MessageService(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    /**
     * {@return a new instance of the service}
     */
    public static MessageService createService() {
        return new MessageService(new RequestExecutor());
    }

    /**
     * {@return a list of newspaper issues pushed to the given device}
     * <p>
     * As long as the returned messages aren't deleted, they will be returned again in future calls to this method. However, if a newspaper was already
     * activated, it is still returned by this method until the message is deleted, even though it cannot be activated again.
     *
     * @param deviceRegistration the device to retrieve messages for
     * @see #deleteMessages(DeviceRegistration, List)
     */
    public CompletableFuture<List<NewspaperActivation>> getPushedNewspapers(DeviceRegistration deviceRegistration) {
        GetMessagesRequest getMessagesRequest = new GetMessagesRequest(toAuthentication(deviceRegistration));

        return requestExecutor.executeRequest(getMessagesRequest, GetMessagesResponse.class)
                .thenApply(this::mapPushedNewspapers);
    }

    private ActivationAuthentication toAuthentication(DeviceRegistration deviceRegistration) {
        return new ActivationAuthentication(deviceRegistration.clientId(), deviceRegistration.activationToken());
    }

    private List<NewspaperActivation> mapPushedNewspapers(GetMessagesResponse response) {
        if (response == null) {
            return List.of();
        }

        return response.messages.stream()
                .filter(NewspaperMessage.class::isInstance)
                .map(NewspaperMessage.class::cast)
                .map(num -> new NewspaperActivation(num.id, num.title, num.issueId, num.getLicenseUrl))
                .collect(Collectors.toList());
    }

    /**
     * Deletes the given messages from the server and "acknowledges" them. The deleted messages are then not returned in future message retrievals.
     *
     * @param deviceRegistration the device to delete messages for
     * @param messagesToDelete   the messages to delete
     * @return a completable future that finishes without an exception if the operation succeeds. The server doesn't tell whether it actually deleted messages,
     * and only future calls to retrieve messages will reflect the result.
     */
    public CompletableFuture<Void> deleteMessages(DeviceRegistration deviceRegistration, Collection<? extends DeletableMessage> messagesToDelete) {
        if (messagesToDelete.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<RequestMessage> messages = messagesToDelete.stream()
                .map(m -> new RequestMessage(m.messageId()))
                .collect(Collectors.toList());
        DeleteMessagesRequest deleteMessagesRequest = new DeleteMessagesRequest(toAuthentication(deviceRegistration), messages);

        return requestExecutor.executeRequest(deleteMessagesRequest, EmptyResponse.class)
                .thenApply(r -> null);
    }
}
