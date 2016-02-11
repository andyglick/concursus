package com.opencredo.concourse.domain.events;

import com.opencredo.concourse.data.tuples.TupleSchema;
import com.opencredo.concourse.domain.AggregateId;
import com.opencredo.concourse.domain.StreamTimestamp;
import com.opencredo.concourse.domain.VersionedName;
import com.opencredo.concourse.domain.events.batching.LoggingEventBatch;
import com.opencredo.concourse.domain.events.batching.SimpleEventBatch;
import com.opencredo.concourse.domain.events.publishing.EventPublisher;
import com.opencredo.concourse.domain.events.publishing.LoggingEventPublisher;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class EventBusTest {

    private final List<Event> loggedEvents = new ArrayList<>();
    private final List<Event> publishedEvents = new ArrayList<>();

    private final EventLog eventLog = loggedEvents::addAll;
    private final EventPublisher eventPublisher = publishedEvents::add;

    private final EventBus bus = () ->
            SimpleEventBatch.writingTo(
                    eventLog.filter(LoggingEventLog::logging)
                            .andPublish(eventPublisher.filter(LoggingEventPublisher::logging)))
            .filter(LoggingEventBatch::logging);


    @Test
    public void dispatchesEventsSinglyToLogAndPublisher() {

        Event event1 = Event.of(
                AggregateId.of("widget", UUID.randomUUID()),
                StreamTimestamp.of("testStream", Instant.now()),
                VersionedName.of("created", "0"),
                TupleSchema.empty().makeWith()
        );

        Event event2 = Event.of(
                AggregateId.of("widget", UUID.randomUUID()),
                StreamTimestamp.of("testStream", Instant.now()),
                VersionedName.of("created", "0"),
                TupleSchema.empty().makeWith()
        );

        bus.accept(event1);
        bus.accept(event2);

        assertThat(loggedEvents, contains(event1, event2));
        assertThat(publishedEvents, contains(event1, event2));
    }

    @Test
    public void dispatchesEventsInBatchToLogAndPublisher() {
        Event event1 = Event.of(
                AggregateId.of("widget", UUID.randomUUID()),
                StreamTimestamp.of("testStream", Instant.now()),
                VersionedName.of("created", "0"),
                TupleSchema.empty().makeWith()
        );

        Event event2 = Event.of(
                AggregateId.of("widget", UUID.randomUUID()),
                StreamTimestamp.of("testStream", Instant.now()),
                VersionedName.of("created", "0"),
                TupleSchema.empty().makeWith()
        );

        bus.dispatch(batch -> {
            batch.accept(event1);
            batch.accept(event2);
        });

        assertThat(loggedEvents, contains(event1, event2));
        assertThat(publishedEvents, contains(event1, event2));
    }

}