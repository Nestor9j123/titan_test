package nitchcorp.backend.titan.coursier.Application.mappers;

import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.AdressDTO;
import nitchcorp.backend.titan.coursier.Application.dtos.ContactInfoDTO;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryOrderRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryOrderResponse;

import nitchcorp.backend.titan.coursier.Domain.models.*;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryCompanyRepository;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryPersonRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DeliveryOrderMapper {

    private final DeliveryCompanyRepository companyRepository;
    private final DeliveryPersonRepository personRepository;

    public DeliveryOrder toEntity(DeliveryOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de livraison ne peut pas être null");
        }

        DeliveryOrder order = new DeliveryOrder();
        order.setTrackingId(UUID.randomUUID());
        order.setTitle(request.title());
        order.setDetails(request.details());

        // Mapping des adresses
        order.setPickupAddress(mapAddressDTO(request.pickupAddress()));
        order.setDeliveryAddress(mapAddressDTO(request.deliveryAddress()));

        // Mapping des contacts
        order.setPickupContact(mapContactInfoDTO(request.pickupContact()));
        order.setDeliveryContact(mapContactInfoDTO(request.deliveryContact()));



        DeliveryCompany company = companyRepository.findByTrackingId(request.deliveryCompanyTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie de livraison non trouvée"));

        order.setDeliveryCompany(company);


        DeliveryPerson person = personRepository.findByTrackingId(request.assignedDeliveryPersonTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Personne de livraison non trouvée"));

        order.setAssignedDeliveryPerson(person);


        return order;
    }

    public DeliveryOrderResponse toResponse(DeliveryOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("L'ordre de livraison ne peut pas être null");
        }

        return new DeliveryOrderResponse(
                order.getTrackingId(),
                order.getTitle(),
                order.getDetails(),
                mapAddress(order.getPickupAddress()),
                mapContactInfo(order.getPickupContact()),
                mapAddress(order.getDeliveryAddress()),
                mapContactInfo(order.getDeliveryContact()),
                order.getDistanceInKm(),
                order.getCalculatedPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getAssignedAt(),
                order.getPickedUpAt(),
                order.getDeliveredAt(),
                order.getDeliveryCompany() != null ? order.getDeliveryCompany().getTrackingId() : null,
                order.getAssignedDeliveryPerson() != null ? order.getAssignedDeliveryPerson().getTrackingId() : null
        );
    }

    private Address mapAddressDTO(AdressDTO dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreetAddress(dto.streetAddress());
        address.setCity(dto.city());
        address.setPostalCode(dto.postalCode());
        address.setCountry(dto.country());
        address.setAdditionalInfo(dto.additionalInfo());
        address.setLatitude(dto.latitude());
        address.setLongitude(dto.longitude());
        return address;
    }

    private ContactInfo mapContactInfoDTO(ContactInfoDTO dto) {
        if (dto == null) return null;

        ContactInfo contact = new ContactInfo();
        contact.setContactName(dto.contactName());
        contact.setContactPhone(dto.contactPhone());
        return contact;
    }

    private AdressDTO mapAddress(Address address) {
        if (address == null) return null;

        return new AdressDTO(
                address.getStreetAddress(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry(),
                address.getAdditionalInfo(),
                address.getLatitude(),
                address.getLongitude()
        );
    }

    private ContactInfoDTO mapContactInfo(ContactInfo contact) {
        if (contact == null) return null;

        return new ContactInfoDTO(
                contact.getContactName(),
                contact.getContactPhone()
        );
    }
}
