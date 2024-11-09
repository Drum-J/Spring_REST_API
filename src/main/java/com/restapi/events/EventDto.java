package com.restapi.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private LocalDateTime beginEnrollmentDateTime;
    @NotNull
    private LocalDateTime closeEnrollmentDateTime;
    @NotNull
    private LocalDateTime beginEventDateTime;
    @NotNull
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    @Min(0)
    private int basePrice; // (optional)
    @Min(0)
    private int maxPrice; // (optional)
    @Min(0)
    private int limitOfEnrollment;

    public Event toEntity() {
        return Event.builder()
                .name(this.name)
                .description(this.description)
                .beginEnrollmentDateTime(this.beginEnrollmentDateTime)
                .closeEnrollmentDateTime(this.closeEnrollmentDateTime)
                .beginEventDateTime(this.beginEventDateTime)
                .endEventDateTime(this.endEventDateTime)
                .location(this.location)
                .basePrice(this.basePrice)
                .maxPrice(this.maxPrice)
                .limitOfEnrollment(this.limitOfEnrollment)
                .eventStatus(EventStatus.DRAFT)
                .build();
    }
}