package com.restapi.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test
    void builder() throws Exception {
        //given
        Event event = Event.builder()
                .name("Spring REST API")
                .description("REST API development with Spring")
                .build();

        //then
        assertNotNull(event);
    }

    @Test
    void javaBean() throws Exception {
        //given
        Event event = new Event();
        //when
        event.setName("Spring REST API");
        event.setDescription("REST API development with Spring");
        //then
        assertEquals("Spring REST API", event.getName());
    }

    @Test
    void testFree() throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertTrue(event.isFree());
    }

    @Test
    void testFreeFalse() throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        Event event2 = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        //when
        event.update();
        event2.update();

        //then
        assertAll(
                () -> assertFalse(event.isFree()),
                () -> assertFalse(event2.isFree())
        );
    }

    @Test
    void isOfflineTrue() throws Exception {
        //given
        Event event = Event.builder()
                .location("home")
                .build();

        //when
        event.update();

        //then
        assertTrue(event.isOffline());
    }

    @Test
    void isOfflineFalse() throws Exception {
        //given
        Event event = Event.builder()
                .location(null)
                .build();

        //when
        event.update();

        //then
        assertFalse(event.isOffline());

    }
}