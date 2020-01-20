package io.axoniq.axonserver.component.processor;

import io.axoniq.axonserver.applicationevents.EventProcessorEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Responsible to refresh the event processor status any time an operations of split or merge has been performed.
 *
 * @author Sara Pellegrini
 * @since 4.4
 */
@Component
public class EventProcessorResultListener {

    private final Consumer<EventProcessorIdentifier> refreshOperation;

    /**
     * Creates an instance of {@link EventProcessorResultListener} based on the {@link EventProcessorStatusRefresh}.
     *
     * @param refreshEventProcessorStatus used to require a refresh of the status of the event processors.
     */
    @Autowired
    public EventProcessorResultListener(EventProcessorStatusRefresh refreshEventProcessorStatus) {
        this(refreshEventProcessorStatus::run);
    }

    /**
     * Creates an instance of {@link EventProcessorResultListener} based on the specified refresh operation.
     *
     * @param refreshOperation used to require a refresh of the status of the event processors.
     */
    public EventProcessorResultListener(Consumer<EventProcessorIdentifier> refreshOperation) {
        this.refreshOperation = refreshOperation;
    }

    /**
     * Refresh the state of the event processor after a merge operation has been performed.
     *
     * @param event the event describing the event processor that has been merged
     */
    @EventListener
    public void on(EventProcessorEvents.MergeSegmentsSucceeded event) {
        refreshOperation.accept(event.processorIdentifier());
    }

    /**
     * Refresh the state of the event processor after a slit operation has been performed.
     *
     * @param event the event describing the event processor that has been split
     */
    @EventListener
    public void on(EventProcessorEvents.SplitSegmentsSucceeded event) {
        refreshOperation.accept(event.processorIdentifier());
    }
}
