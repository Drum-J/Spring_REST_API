package com.restapi.events;

import com.restapi.accounts.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private final EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    private Account manager;

    public void update() {
        // update free
        this.free = this.basePrice == 0 && this.maxPrice == 0;

        // update offline
        this.offline = this.location != null && !this.location.isBlank();
    }

    public void updateWithDto(EventDto eventDto) {
        this.name = eventDto.getName();
        this.description = eventDto.getDescription();
        this.beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        this.closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        this.beginEventDateTime = eventDto.getBeginEventDateTime();
        this.endEventDateTime = eventDto.getEndEventDateTime();
        this.location = eventDto.getLocation();
        this.basePrice = eventDto.getBasePrice();
        this.maxPrice = eventDto.getMaxPrice();
        this.limitOfEnrollment = eventDto.getLimitOfEnrollment();
    }
}
