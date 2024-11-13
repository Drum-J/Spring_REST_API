package com.restapi.events;

import com.restapi.common.ErrorsResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.Optional;

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
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = eventDto.toEntity();
        event.update();
        eventRepository.save(event);

        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class);
        URI createdUri = linkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkBuilder.withRel("query-events"));
        eventResource.add(linkBuilder.slash(event.getId()).withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity<Object> queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> model = assembler.toModel(page, EventResource::new);
        model.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEvent(@PathVariable("id") Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EventResource resource = new EventResource(event.get());
        resource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(resource);
    }


    private static ResponseEntity<Object> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
