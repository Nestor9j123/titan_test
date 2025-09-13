package nitchcorp.backend.titan.events.Application.service;

import nitchcorp.backend.titan.events.Application.dtos.request.EventsRequest;
import nitchcorp.backend.titan.events.Application.dtos.response.EventsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface EventsService {

    EventsResponse createEvent(EventsRequest request, List<MultipartFile> images);

    EventsResponse createEventWithPhotos(EventsRequest request, List<MultipartFile> photos);

    List<EventsResponse> getAllEvents();

    EventsResponse getEventByTrackingId(UUID trackingId);

    EventsResponse updateEvent(UUID trackingId, EventsRequest request);

    void deleteEvent(UUID trackingId);


    EventsResponse addImagesToEvent(UUID trackingId, List<MultipartFile> photos);

    EventsResponse removeImagesFromEvent(UUID trackingId, List<Long> imageIds);
}