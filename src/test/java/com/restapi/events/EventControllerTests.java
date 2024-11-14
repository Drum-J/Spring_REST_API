package com.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.common.RestDocsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
public class EventControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
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
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                //HATEOAS 적용
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new Event"),
                                fieldWithPath("name").description("name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                subsectionWithPath("_links").ignored()
                        )
                    )
                )
        ;
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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

    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    void createEvent_Bad_Request_Empty_Input() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .build();

        //when

        //then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description(("REST API Development with Spring"))
                .beginEnrollmentDateTime(now.plusDays(3))
                .closeEnrollmentDateTime(now.plusDays(2))
                .beginEventDateTime(now.plusDays(1))
                .endEventDateTime(now) // 이벤트 끝나는 날짜가 더 빠름
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("home")
                .build();

        //when

        //then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                //.andExpect(jsonPath("_links.index").exists())
                //.andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    void queryEvents() throws Exception {
        //given
        IntStream.range(0,30).forEach(this::generateEvent);

        //when
        mockMvc.perform(get("/api/events")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("첫 페이지"),
                                linkWithRel("prev").description("이전 페이지"),
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("next").description("다음 페이지"),
                                linkWithRel("last").description("마지막 페이지"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded").description("PagedModel<EntityModel<Event>> 사용 시 생김"),
                                fieldWithPath("_embedded.eventList").description("PagedModel<EntityModel<Event>> 사용 시 생김, 이벤트 리스트"),
                                fieldWithPath("_embedded.eventList[].id").description("identifier of new Event"),
                                fieldWithPath("_embedded.eventList[].name").description("name of new Event"),
                                fieldWithPath("_embedded.eventList[].description").description("description of new Event"),
                                fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("closeEnrollmentDateTime of new Event"),
                                fieldWithPath("_embedded.eventList[].beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("_embedded.eventList[].endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("_embedded.eventList[].location").description("location of new Event"),
                                fieldWithPath("_embedded.eventList[].basePrice").description("basePrice of new Event"),
                                fieldWithPath("_embedded.eventList[].maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("limitOfEnrollment of new Event"),
                                fieldWithPath("_embedded.eventList[].free").description("it tells is this event is free or not"),
                                fieldWithPath("_embedded.eventList[].offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("_embedded.eventList[].eventStatus").description("event status"),
                                fieldWithPath("page").description("PagedModel<EntityModel<Event>> 사용 시 page, Page<Event>만 사용시 pageable"),
                                fieldWithPath("page.size").description("한 페이지 당 데이터 수"),
                                fieldWithPath("page.totalElements").description("전체 데이터 수"),
                                fieldWithPath("page.totalPages").description("전체 페이지 수 (1,2,3 으로 카운트)"),
                                fieldWithPath("page.number").description("현재 페이지 (0,1,2 로 카운트)"),
                                subsectionWithPath("_embedded.eventList[]._links").ignored(),
                                subsectionWithPath("_links").ignored()
                        )
                    )
                )
        ;

        //then

    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    void getEvent() throws Exception {
        //given
        Event event = generateEvent(100);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("이벤트 목록 Link"),
                                linkWithRel("update-event").description("이벤트 업데이트 Link"),
                                linkWithRel("profile").description("상세 설명 문서 Link")
                        ),
                        pathParameters(
                                parameterWithName("id").description("이벤트 ID")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header : [application/hal+json]")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new Event"),
                                fieldWithPath("name").description("name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells is this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                subsectionWithPath("_links").ignored()
                        )
                ))
        ;
        //then

    }

    @Test
    @DisplayName("없는 이벤트는 조회했을 때 404 응답받기")
    void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/11234"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("기존의 이벤트 업데이트 하기")
    void updateEvent() throws Exception {
        //given
        Event event = generateEvent(123);
        String updateName = "Updated Event";
        LocalDateTime now = LocalDateTime.now();
        EventDto eventDto = EventDto.builder()
                .name(updateName)
                .description("test event")
                .beginEnrollmentDateTime(now)
                .closeEnrollmentDateTime(now.plusDays(1))
                .beginEventDateTime(now.plusDays(2))
                .endEventDateTime(now.plusDays(3))
                .location("home")
                .basePrice(0)
                .maxPrice(0)
                .limitOfEnrollment(100)
                .build();

        //when
        mockMvc.perform(put("/api/events/{id}",event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateName))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;
        //then

    }

    @Test
    @DisplayName("없는 이벤트를 업데이트 했을 때 404 응답받기")
    void updateEvent404() throws Exception {
        EventDto eventDto = new EventDto();
        mockMvc.perform(put("/api/events/11234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("업데이트 : 비어있는 데이터를 보냈을 때 400")
    void updateEvent400_Empty() throws Exception {
        //given
        Event event = generateEvent(123);
        EventDto eventDto = new EventDto();

        //when
        mockMvc.perform(put("/api/events/{id}",event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then

    }

    @Test
    @DisplayName("업데이트 : 잘못된 데이터를 보냈을 때 400")
    void updateEvnet400_Wrong() throws Exception {
        //given
        Event event = generateEvent(123);
        String updateName = "Updated Event";
        LocalDateTime now = LocalDateTime.now();
        EventDto eventDto = EventDto.builder()
                .name(updateName)
                .description("test event")
                .beginEnrollmentDateTime(now)
                .closeEnrollmentDateTime(now.plusDays(1))
                .beginEventDateTime(now.plusDays(2))
                .endEventDateTime(now.plusDays(3))
                .location("home")
                .basePrice(20000) // base Price > max Price : validate 에서 에러
                .maxPrice(100)
                .limitOfEnrollment(100)
                .build();

        //when
        mockMvc.perform(put("/api/events/{id}",event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then

    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event "+index)
                .description("test event")
                .build();

        eventRepository.save(event);
        return event;
    }
}
