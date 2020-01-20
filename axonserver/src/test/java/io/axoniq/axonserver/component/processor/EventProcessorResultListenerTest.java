package io.axoniq.axonserver.component.processor;

import io.axoniq.axonserver.applicationevents.EventProcessorEvents;
import org.junit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link EventProcessorResultListener}.
 *
 * @author Sara Pellegrini
 */
public class EventProcessorResultListenerTest {

    private List<EventProcessorIdentifier> refreshed = new ArrayList<>();

    private EventProcessorResultListener testSubject = new EventProcessorResultListener(refreshed::add);

    @Before
    public void setUp() throws Exception {
        refreshed.clear();
    }

    @Test
    public void onSplit() {
        assertTrue(refreshed.isEmpty());
        EventProcessorIdentifier eventIdentifier = new NameBasedEventProcessorIdentifier("ProcessorA");
        testSubject.on(new EventProcessorEvents.SplitSegmentsSucceeded(eventIdentifier));
        assertEquals(refreshed, Collections.singletonList(eventIdentifier));
    }

    @Test
    public void onMerge() {
        assertTrue(refreshed.isEmpty());
        EventProcessorIdentifier eventIdentifier = new NameBasedEventProcessorIdentifier("ProcessorB");
        testSubject.on(new EventProcessorEvents.MergeSegmentsSucceeded(eventIdentifier));
        assertEquals(refreshed, Collections.singletonList(eventIdentifier));
    }
}