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
}