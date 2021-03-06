/*
 * Copyright (c) 2017-2019 AxonIQ B.V. and/or licensed to AxonIQ B.V.
 * under one or more contributor license agreements.
 *
 *  Licensed under the AxonIQ Open Source License Agreement v1.0;
 *  you may not use this file except in compliance with the license.
 *
 */

package io.axoniq.axonserver.component.processor;

import io.axoniq.axonserver.component.processor.listener.ClientProcessor;
import io.axoniq.axonserver.component.processor.listener.ClientProcessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Iterable of {@link EventProcessor}s defined by a specific component.
 *
 * @author Sara Pellegrini
 * @since 4.0
 */
public class ComponentEventProcessors implements Iterable<EventProcessor> {

    private final ClientProcessors componentClientProcessors;

    private final EventProcessorMapping mapping;

    /**
     * Creates an instance defined by the component, the context and the full list of all {@link ClientProcessor}s
     *
     * @param component       the component name of the client application
     * @param context         the context of the client application
     * @param eventProcessors all known {@link ClientProcessor}s
     */
    public ComponentEventProcessors(String component, String context,
                                    ClientProcessors eventProcessors) {
        this(new ComponentClientProcessors(eventProcessors, component, context), new EventProcessorMapping());
    }

    /**
     * Creates an instance defined by the component, the context and the full list of all {@link ClientProcessor}s
     *
     * @param componentClientProcessors {@link ClientProcessors} defined for a component
     * @param mapping                   the mapping function to get an {@link EventProcessor} from the processorName
     *                                  and
     *                                  the collection of all active {@link ClientProcessor}s instances for that
     *                                  processor
     */
    ComponentEventProcessors(ClientProcessors componentClientProcessors,
                             EventProcessorMapping mapping) {
        this.componentClientProcessors = componentClientProcessors;
        this.mapping = mapping;
    }

    @Override
    public Iterator<EventProcessor> iterator() {
        //group by processorName
        Map<String, Set<ClientProcessor>> processorsMap = new HashMap<>();
        for (ClientProcessor processor : componentClientProcessors) {
            String processorName = processor.eventProcessorInfo().getProcessorName();
            Set<ClientProcessor> clientProcessors = processorsMap.computeIfAbsent(processorName,
                                                                                  name -> new HashSet<>());
            clientProcessors.add(processor);
        }

        return processorsMap.entrySet().stream().map(
                entry -> mapping.apply(
                        entry.getKey(),
                        entry.getValue())
        ).iterator();
    }
}
