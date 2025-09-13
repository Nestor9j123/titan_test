package nitchcorp.backend.titan.coursier.Application.services;

import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryPersonRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryPersonResponse;

import java.util.List;
import java.util.UUID;

public interface DeliveryPersonService {

    DeliveryPersonResponse createDeliveryPerson(DeliveryPersonRequest request);

    List<DeliveryPersonResponse> getAllDeliveryPersons();

    DeliveryPersonResponse getDeliveryPersonByTrackingId(UUID trackingId);

    List<DeliveryPersonResponse> getDeliveryPersonByCompany(UUID companyTrackingId);

    List<DeliveryPersonResponse> getDeliveryPersonAvailableByCompany(UUID companyTrackingId);

    DeliveryPersonResponse updateDeliveryPerson(UUID trackingId, DeliveryPersonRequest request);

    void setAvailability(UUID trackingId, boolean isAvailable);

    void deactivateDeliveryPerson(UUID trackingId);

    void deleteDeliveryPerson(UUID trackingId);

}
