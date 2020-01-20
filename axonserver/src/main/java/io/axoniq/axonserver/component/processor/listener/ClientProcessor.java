/*
 * Copyright (c) 2017-2019 AxonIQ B.V. and/or licensed to AxonIQ B.V.
 * under one or more contributor license agreements.
 *
 *  Licensed under the AxonIQ Open Source License Agreement v1.0;
 *  you may not use this file except in compliance with the license.
 *
 */

package io.axoniq.axonserver.component.processor.listener;

import io.axoniq.axonserver.component.ComponentItem;
import io.axoniq.axonserver.component.processor.EventProcessorIdentifier;
import io.axoniq.axonserver.component.processor.NameBasedEventProcessorIdentifier;
import io.axoniq.axonserver.grpc.control.EventProcessorInfo;
import io.axoniq.axonserver.grpc.control.EventProcessorInfo.SegmentStatus;

import java.util.Iterator;

/**
 * Represents the Event Processor instance running in a client application connected to Axon Server.
 *
 * @author Sara Pellegrini
 */
public interface ClientProcessor extends ComponentItem, Iterable<SegmentStatus> {

    String clientId();

    default EventProcessorIdentifier processorId() {
        //TODO the implementation of the EventProcessorIdentifier will depends from the processor type. In case of Tracking Event Processor we need a TrackingProcessorIdentifier
        return new NameBasedEventProcessorIdentifier(eventProcessorInfo().getProcessorName());
    }

    EventProcessorInfo eventProcessorInfo();

    default Boolean running() {
        return eventProcessorInfo().getRunning();
    }

    @Override
    default Iterator<SegmentStatus> iterator() {
        return eventProcessorInfo().getSegmentStatusList().iterator();
    }
}
