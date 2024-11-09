package com.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void createEvent() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        EventDto event = EventDto.builder()
                .name("Spring")
                .description(("REST API Development with Spring"))
                .beginEnrollmentDateTime(now)
                .closeEnrollmentDateTime(now.plusDays(1))
                .beginEventDateTime(now.plusDays(2))
                .endEventDateTime(now.plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("home")
                .build();

        //then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }

    @Test
    void createEvent_Bad_Request() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description(("REST API Development with Spring"))
                .beginEnrollmentDateTime(now)
                .closeEnrollmentDateTime(now.plusDays(1))
                .beginEventDateTime(now.plusDays(2))
                .endEventDateTime(now.plusDays(3))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("home")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();

        //then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
