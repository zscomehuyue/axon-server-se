package io.axoniq.axonserver.component.processor;

import io.axoniq.axonserver.component.processor.listener.ClientProcessor;
import io.axoniq.axonserver.component.processor.listener.FakeClientProcessor;
import org.junit.*;

import java.util.Objects;

/**
 * Unit tests for {@link ClientProcessorsByIdentifier}
 *
 * @author Sara Pellegrini
 */
public class ClientProcessorsByIdentifierTest {

    private static final String CLIENT_A = "clientA";
    private static final String CLIENT_B = "clientB";
    private static final String CLIENT_C = "clientC";

    private static final boolean BELONGS_TO_CONTEXT = true;
    private static final boolean DOES_NOT_BELONG_TO_CONTEXT = false;

    private static final String BLUE_PROCESSOR = "Blue";
    private static final String GREEN_PROCESSOR = "Green";
    private static final String RED_PROCESSOR = "Red";
    private final EventProcessorIdentifier processorId1 = new TestEventProcessorIdentifier(1);
    private final EventProcessorIdentifier processorId2 = new TestEventProcessorIdentifier(2);
    private final EventProcessorIdentifier processorId3 = new TestEventProcessorIdentifier(3);
    private final ClientProcessor blueA = new FakeClientProcessor(CLIENT_A, BELONGS_TO_CONTEXT, BLUE_PROCESSOR);
    private final ClientProcessor greenA = new FakeClientProcessor(CLIENT_A, BELONGS_TO_CONTEXT, GREEN_PROCESSOR);
    private final ClientProcessor redA = new FakeClientProcessor(CLIENT_A, BELONGS_TO_CONTEXT, RED_PROCESSOR);

    @Test
    public void iterator() {

//        new ClientProcessorsByIdentifier(, new )
    }

    private static class TestEventProcessorIdentifier implements EventProcessorIdentifier {

        private final int id;

        private TestEventProcessorIdentifier(int id) {
            this.id = id;
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TestEventProcessorIdentifier that = (TestEventProcessorIdentifier) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}