package io.axoniq.axonserver.component.processor;

import io.axoniq.axonserver.component.processor.listener.ClientProcessor;
import io.axoniq.axonserver.component.processor.listener.ClientProcessors;

import java.util.Iterator;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

/**
 * Iterable of {@link ClientProcessor}s that have the same {@link EventProcessorIdentifier}
 *
 * @author Sara Pellegrini
 * @since 4.4
 */
public class ClientProcessorsByIdentifier implements ClientProcessors {

    private final ClientProcessors allClientProcessors;

    private final EventProcessorIdentifier eventProcessorIdentifier;

    /**
     * Creates an instance of {@link ClientProcessorsByIdentifier} based on the specified iterable of all registered
     * {@link ClientProcessor}s and on the specified {@link EventProcessorIdentifier}.
     *
     * @param allClientProcessors      all the {@link ClientProcessor}s received from connected clients
     * @param eventProcessorIdentifier the identifier of the event processor we are interested in.
     */
    public ClientProcessorsByIdentifier(
            ClientProcessors allClientProcessors,
            EventProcessorIdentifier eventProcessorIdentifier) {
        this.allClientProcessors = allClientProcessors;
        this.eventProcessorIdentifier = eventProcessorIdentifier;
    }

    @Nonnull
    @Override
    public Iterator<ClientProcessor> iterator() {
        return StreamSupport
                .stream(allClientProcessors.spliterator(), false)
                .filter(p -> eventProcessorIdentifier.equals(p.processorId()))
                .iterator();
    }
}
