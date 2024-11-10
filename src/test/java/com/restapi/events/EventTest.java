package com.restapi.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false"
    })
    void freeTestWithParams(int basePrice, int maxPrice, boolean isFree) throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //when
        event.update();

        //then
        assertEquals(event.isFree(),isFree);
    }

    @ParameterizedTest
    @MethodSource("paramsForTestFree")
    void freeTestWithParams_typeSafe(int basePrice, int maxPrice, boolean isFree) throws Exception {
        //given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //when
        event.update();

        //then
        assertEquals(event.isFree(),isFree);
    }

    private static Stream<Arguments> paramsForTestFree() {
        return Stream.of(
                Arguments.of(0, 0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }

    @ParameterizedTest
    @MethodSource("paramsForTestOffline")
    void testOfflineWithParams(String location, boolean isOffline) {
        //given
        Event event = Event.builder()
                .location(location)
                .build();

        //when
        event.update();

        //then
        assertEquals(event.isOffline(),isOffline);
    }

    private static Object[] paramsForTestOffline() {
        return new Object[]{
                new Object[] {"강남",true},
                new Object[] {null, false},
                new Object[] {"   ", false}
        };
    }
}