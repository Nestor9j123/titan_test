package nitchcorp.backend.titan.events.Application.mappers;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.events.Application.dtos.request.EventsRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.EventsResponse;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventsMapper {

    private final UserRepository userRepository;

    public EventsResponse toResponse(Events event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        return new EventsResponse(
                event.getTrackingId(),
                event.getName(),
                event.getDescription(),
                event.getCapacity(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getImages(),
                event.getOrganizer().getTrackingId(),
                event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName(),
                event.getOrganizer().getEmail()
        );
    }

    public Events toEntity(EventsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("EventsRequest cannot be null");
        }

        // Find the organizer user
        User organizer = userRepository.findByTrackingId(request.organizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found with ID: " + request.organizerId()));

        Events event = new Events();
        event.setTrackingId(UUID.randomUUID());
        event.setName(request.name());
        event.setCapacity(request.capacity());
        event.setDescription(request.description());
        event.setImages(request.images());
        event.setStartDateTime(request.startDateTime());
        event.setEndDateTime(request.endDateTime());
        event.setOrganizer(organizer);

        return event;
    }

    public Events toEntityFromResponse(EventsResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("EventsResponse cannot be null");
        }

        // Find the organizer user
        User organizer = userRepository.findByTrackingId(response.organizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found with ID: " + response.organizerId()));

        Events event = new Events();
        event.setTrackingId(response.trackingId());
        event.setName(response.name());
        event.setCapacity(response.capacity());
        event.setDescription(response.description());
        event.setImages(response.images());
        event.setStartDateTime(response.startDateTime());
        event.setEndDateTime(response.endDateTime());
        event.setOrganizer(organizer);

        return event;
    }

    public List<EventsResponse> toResponseList(List<Events> events) {
        if (events == null) {
            throw new IllegalArgumentException("Events list cannot be null");
        }
        return events.stream()
                .map(this::toResponse)
                .toList();
    }
}
