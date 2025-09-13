package nitchcorp.backend.titan.events.Application.service.ImplService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.dtos.request.EventsRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.EventsResponse;
import nitchcorp.backend.titan.events.Application.mappers.EventsMapper;
import nitchcorp.backend.titan.events.Application.service.EventsService;
import nitchcorp.backend.titan.events.domain.exception.EventNotFoundException;
import nitchcorp.backend.titan.events.domain.model.Events;
import nitchcorp.backend.titan.events.infrastructure.EventsRepository;
import nitchcorp.backend.titan.shared.minio.enums.FileType;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.shared.securite.user.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final EventsMapper eventsMapper;
    private final MinioService minioService;
    private final UserRepository userRepository;

    @Override
    public EventsResponse createEvent(EventsRequest request, List<MultipartFile> images) {
        log.info("Creating event: {}", request);

        Events event = eventsMapper.toEntity(request);
        Events savedEvent = eventsRepository.save(event);

        return eventsMapper.toResponse(savedEvent);
    }

    @Override
    public List<EventsResponse> getAllEvents() {
        log.info("Fetching all events");
        return eventsRepository.findAll()
                .stream()
                .map(eventsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventsResponse getEventByTrackingId(UUID trackingId) {
        log.info("Fetching event with trackingId: {}", trackingId);

        Events event = (Events) eventsRepository.getEventsByTrackingId(trackingId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with trackingId: " + trackingId));

        return eventsMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventsResponse updateEvent(UUID trackingId, EventsRequest request) {
        log.info("Updating event {} with request: {}", trackingId, request);

        Events existingEvent = (Events) eventsRepository.getEventsByTrackingId(trackingId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with trackingId: " + trackingId));

        existingEvent.setName(request.name());
        existingEvent.setDescription(request.description());
        existingEvent.setCapacity(request.capacity());
        existingEvent.setStartDateTime(request.startDateTime());
        existingEvent.setEndDateTime(request.endDateTime());
        existingEvent.setImages(request.images());

        Events updatedEvent = eventsRepository.save(existingEvent);

        return eventsMapper.toResponse(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID trackingId) {
        log.info("Deleting event with trackingId: {}", trackingId);

        Events event = (Events) eventsRepository.getEventsByTrackingId(trackingId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with trackingId: " + trackingId));

        // TODO: supprimer les images dans Minio via minioService.deleteFile()
        eventsRepository.delete(event);
    }




    @Override
    @Transactional
    public EventsResponse createEventWithPhotos(EventsRequest request, List<MultipartFile> images) {
        log.info("Creating event with photos: {}", request);

        EventsResponse event = createEvent(request, images);

        if (images != null && !images.isEmpty()) {
            Events savedEvent = (Events) eventsRepository.getEventsByTrackingId(event.trackingId())
                    .orElseThrow(() -> new EventNotFoundException("Event not found with trackingId: " + event.trackingId()));

            var uploadResponses = minioService.uploadMultipleFiles(images, FileType.IMAGE);

            List<String> photoUrls = uploadResponses.stream()
                    .map(r -> r.getFileUrl())
                    .collect(Collectors.toList());

            savedEvent.setImages(photoUrls);
            Events updatedEvent = eventsRepository.save(savedEvent);

            return eventsMapper.toResponse(updatedEvent);
        }

        return event;
    }

    @Override
    @Transactional
    public EventsResponse addImagesToEvent(UUID trackingId, List<MultipartFile> photos) {
        log.info("Adding photos to event {}", trackingId);

        Events event = (Events) eventsRepository.getEventsByTrackingId(trackingId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with trackingId: " + trackingId));

        if (photos == null || photos.isEmpty()) {
            log.warn("No photos provided for event {}", trackingId);
            return eventsMapper.toResponse(event);
        }

        var uploadResponses = minioService.uploadMultipleFiles(photos, FileType.IMAGE);

        List<String> photoUrls = uploadResponses.stream()
                .map(r -> r.getFileUrl())
                .collect(Collectors.toList());

        List<String> existingPhotos = event.getImages() != null ? event.getImages() : new ArrayList<>();
        existingPhotos.addAll(photoUrls);

        event.setImages(existingPhotos);
        Events updatedEvent = eventsRepository.save(event);

        return eventsMapper.toResponse(updatedEvent);
    }

    @Override
    @Transactional
    public EventsResponse removeImagesFromEvent(UUID trackingId, List<Long> imageIds) {
        log.info("Removing photos {} from event {}", imageIds, trackingId);

        Events event = (Events) eventsRepository.getEventsByTrackingId(trackingId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with trackingId: " + trackingId));

        // TODO: suppression réelle via minioService.deleteFile()
        return eventsMapper.toResponse(event);
    }
}
