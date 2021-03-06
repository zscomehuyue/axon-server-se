/*
 * Copyright (c) 2017-2019 AxonIQ B.V. and/or licensed to AxonIQ B.V.
 * under one or more contributor license agreements.
 *
 *  Licensed under the AxonIQ Open Source License Agreement v1.0;
 *  you may not use this file except in compliance with the license.
 *
 */

package io.axoniq.axonserver.message.query;

import io.axoniq.axonserver.grpc.query.QueryProviderInbound;
import io.axoniq.axonserver.grpc.query.QueryRequest;
import io.axoniq.axonserver.message.ClientIdentification;
import io.axoniq.axonserver.topology.Topology;
import io.grpc.stub.StreamObserver;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * @author Marc Gathier
 */
@RunWith(MockitoJUnitRunner.class)
public class QueryRegistrationCacheTest {
    private QueryRegistrationCache queryRegistrationCache;
    private DummyStreamObserver dummyStreamObserver = new DummyStreamObserver();
    @Mock
    private QueryHandlerSelector queryHandlerSelector;

    @Before
    public void setup() {
        queryRegistrationCache = new QueryRegistrationCache(queryHandlerSelector);
    }

    @Test
    public void remove() {
        DirectQueryHandler provider1 = new DirectQueryHandler(dummyStreamObserver, new ClientIdentification(Topology.DEFAULT_CONTEXT,
                                                              "client"), "component");
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   provider1);

        queryRegistrationCache.remove(provider1.getClient());
        Map<QueryDefinition, Map<String, Set<QueryHandler>>> all = queryRegistrationCache.getAll();
        assertEquals(0, all.size());
    }
    @Test
    public void removeWithRemaining() {
        DirectQueryHandler provider1 = new DirectQueryHandler(dummyStreamObserver, new ClientIdentification(Topology.DEFAULT_CONTEXT, "client"), "component");
        DirectQueryHandler provider2 = new DirectQueryHandler(dummyStreamObserver, new ClientIdentification(Topology.DEFAULT_CONTEXT,"client2"), "component");
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   provider1);
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   provider2);

        queryRegistrationCache.remove(provider1.getClient());
        Map<QueryDefinition, Map<String, Set<QueryHandler>>> all = queryRegistrationCache.getAll();
        assertEquals(1, all.size());
    }
    @Test
    public void remove1() {
        DirectQueryHandler provider1 = new DirectQueryHandler(dummyStreamObserver,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"client"), "component");
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   provider1);
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test1"),
                                   "test",
                                   provider1);

        queryRegistrationCache.remove(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test1"), provider1.getClient());
        Map<QueryDefinition, Map<String, Set<QueryHandler>>> all = queryRegistrationCache.getAll();
        assertEquals(1, all.size());
    }

    @Test
    public void add() {

        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   new DirectQueryHandler(dummyStreamObserver,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"client"), "component"));
        Map<QueryDefinition, Map<String, Set<QueryHandler>>> all = queryRegistrationCache.getAll();
        assertEquals(1, all.size());
        assertEquals(1, all.get(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test")).size());
        assertEquals(1, all.get(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test")).get("component").size());
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   new DirectQueryHandler(dummyStreamObserver,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"client3"), "component"));
        all = queryRegistrationCache.getAll();
        assertEquals(2, all.get(new QueryDefinition(Topology.DEFAULT_CONTEXT , "test")).get("component").size());
    }

    @Test
    public void find() {
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   new DirectQueryHandler(dummyStreamObserver,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"client"), "component"));
        QueryRequest request = QueryRequest.newBuilder().setQuery("test").build();

        assertNotNull(queryRegistrationCache.find(Topology.DEFAULT_CONTEXT, request,"client"));
        assertNull(queryRegistrationCache.find(Topology.DEFAULT_CONTEXT, request,  "client1"));
    }


    @Test
    public void getForClient() {
        DirectQueryHandler provider1 = new DirectQueryHandler(dummyStreamObserver,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"client"), "component");
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   provider1);
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test1"),
                                   "test",
                                   provider1);
        assertEquals(2, queryRegistrationCache.getForClient(provider1.getClient()).size());

    }

    @Test
    public void find1() {
        queryRegistrationCache.add(new QueryDefinition(Topology.DEFAULT_CONTEXT, "test"),
                                   "test",
                                   new DirectQueryHandler(dummyStreamObserver,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"client"), "component"));
        QueryRequest request = QueryRequest.newBuilder().setQuery("test").build();
        QueryRequest request2 = QueryRequest.newBuilder().setQuery("test1").build();
        when( queryHandlerSelector.select(anyObject(), anyObject(), anyObject())).thenReturn( new ClientIdentification(Topology.DEFAULT_CONTEXT,"client"));
        assertNotNull(queryRegistrationCache.find(Topology.DEFAULT_CONTEXT, request));
        assertTrue(queryRegistrationCache.find(Topology.DEFAULT_CONTEXT, request2).isEmpty());
    }

    @Test
    public void querySubscriptionTwice(){
        QueryDefinition queryDefinition = new QueryDefinition("MyContext", "MyQuery");
        QueryHandler queryHandler = new DirectQueryHandler(null,  new ClientIdentification(Topology.DEFAULT_CONTEXT,"MyClientName"), "MyComponentName") ;
        queryRegistrationCache.add(queryDefinition, "resultName", queryHandler);
        queryRegistrationCache.add(queryDefinition, "resultName", queryHandler);
        QueryRequest queryRequest = QueryRequest.newBuilder().setQuery("MyQuery").build();
        Collection<QueryHandler> handlers = queryRegistrationCache.findAll("MyContext", queryRequest);
        assertEquals(1, handlers.size());
    }

    private class DummyStreamObserver implements StreamObserver<QueryProviderInbound> {
        @Override
        public void onNext(QueryProviderInbound queryProviderInbound) {

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onCompleted() {

        }
    }
}
