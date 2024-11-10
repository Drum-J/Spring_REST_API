package com.restapi.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final EventValidator eventValidator;


    @PostMapping
    public ResponseEntity<Object> createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = eventDto.toEntity();
        event.update();
        eventRepository.save(event);

        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class);
        URI createdUri = linkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkBuilder.withRel("query-events"));
        eventResource.add(linkBuilder.slash(event.getId()).withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-event-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }
}
