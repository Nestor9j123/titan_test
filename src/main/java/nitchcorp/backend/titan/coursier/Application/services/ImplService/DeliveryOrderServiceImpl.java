package nitchcorp.backend.titan.coursier.Application.services.ImplService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryOrderRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryOrderResponse;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.PriceCalculationResponse;
import nitchcorp.backend.titan.coursier.Application.mappers.DeliveryOrderMapper;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryOrder;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryPerson;
import nitchcorp.backend.titan.coursier.Domain.enums.DeliveryStatus;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryCompanyRepository;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryOrderRepository;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryPersonRepository;
import nitchcorp.backend.titan.coursier.Application.services.DeliveryOrderService;
import nitchcorp.backend.titan.coursier.Application.services.DistanceCalculationService;
import nitchcorp.backend.titan.coursier.Application.services.PricingRuleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

    private final DeliveryOrderRepository repository;
    private final DeliveryCompanyRepository companyRepository;
    private final DeliveryPersonRepository personRepository;
    private final DeliveryOrderMapper mapper;
    private final DistanceCalculationService distanceService;
    private final PricingRuleService pricingService;

    @Override
    public PriceCalculationResponse calculatePrice(DeliveryOrderRequest request) {
        DeliveryCompany company = companyRepository.findByTrackingId(request.deliveryCompanyTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie de livraison non trouvée"));

        Double distance = distanceService.calculateDistance(
                request.pickupAddress().latitude(),
                request.pickupAddress().longitude(),
                request.deliveryAddress().latitude(),
                request.deliveryAddress().longitude()
        );

        BigDecimal price = pricingService.calculatePrice(company, distance);

        return new PriceCalculationResponse(
                distance,
                price,
                "Prix calculé avec succès"
        );
    }

    @Override
    public DeliveryOrderResponse createOrder(DeliveryOrderRequest request) {

        DeliveryCompany company = companyRepository.findByTrackingId(request.deliveryCompanyTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie de livraison non trouvée"));

        if (!company.getIsActive()) {
            throw new IllegalArgumentException("La compagnie de livraison n'est pas active");
        }

        // Vérifier le livreur assigné s'il est spécifié
        if (request.assignedDeliveryPersonTrackingId() != null) {
            DeliveryPerson person = personRepository.findByTrackingId(request.assignedDeliveryPersonTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Personne de livraison non trouvée"));

            if (!person.getIsActive() || !person.getIsAvailable()) {
                throw new IllegalStateException("Le livreur n'est pas disponible");
            }

            if (!person.getDeliveryCompany().equals(company)) {
                throw new IllegalArgumentException("Le livreur ne fait pas partie de la compagnie de livraison");
            }
        }

        // Calculer la distance et le prix
        PriceCalculationResponse calculation = calculatePrice(request);

        DeliveryOrder order = mapper.toEntity(request);
        order.setDistanceInKm(calculation.distanceInKm());
        order.setCalculatedPrice(calculation.calculatedPrice());
        order.setStatus(request.assignedDeliveryPersonTrackingId() != null ?
                DeliveryStatus.ASSIGNED : DeliveryStatus.PENDING);

        if (request.assignedDeliveryPersonTrackingId() != null) {
            order.setAssignedAt(LocalDateTime.now());
        }

        DeliveryOrder saved = repository.save(order);
        return mapper.toResponse(saved);
    }


    @Override
    public List<DeliveryOrderResponse> getAllOrders() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public DeliveryOrderResponse findByTrackingId(UUID trackingId) {
        DeliveryOrder order = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
        return mapper.toResponse(order);
    }

    @Override
    public List<DeliveryOrderResponse> getOrderByStatus(DeliveryStatus status) {
        return repository.findByStatus(status).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DeliveryOrderResponse> getOrderByCompany(UUID companyTrackingId) {
        return repository.findByDeliveryCompany(companyTrackingId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DeliveryOrderResponse> getOrderByDeliveryPerson(UUID deliveryPersonTrackingId) {
        return repository.findByAssignedDeliveryPerson(deliveryPersonTrackingId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public DeliveryOrderResponse assignToDeliveryPerson(UUID orderTrackingId, UUID deliveryPersonTrackingId) {
        DeliveryOrder order = repository.findByTrackingId(orderTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (order.getStatus() != DeliveryStatus.PENDING) {
            throw new IllegalStateException("La commande ne peut être assignée que si elle est en attente");
        }

        DeliveryPerson person = personRepository.findByTrackingId(deliveryPersonTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur non trouvé"));

        if (!person.getIsActive() || !person.getIsAvailable()) {
            throw new IllegalStateException("Le livreur n'est pas disponible");
        }

        if (!person.getDeliveryCompany().equals(order.getDeliveryCompany())) {
            throw new IllegalArgumentException("Le livreur ne fait pas partie de la compagnie de livraison");
        }

        order.setAssignedDeliveryPerson(person);
        order.setStatus(DeliveryStatus.ASSIGNED);
        order.setAssignedAt(LocalDateTime.now());

        DeliveryOrder updated = repository.save(order);
        return mapper.toResponse(updated);
    }

    @Override
    public DeliveryOrderResponse startDelivery(UUID orderTrackingId) {
        DeliveryOrder order = repository.findByTrackingId(orderTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (order.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("La commande doit être assignée pour démarrer la livraison");
        }

        order.setStatus(DeliveryStatus.IN_PROGRESS);
        order.setPickedUpAt(LocalDateTime.now());

        DeliveryOrder updated = repository.save(order);
        return mapper.toResponse(updated);
    }

    @Override
    public DeliveryOrderResponse completeDelivery(UUID orderTrackingId) {
        DeliveryOrder order = repository.findByTrackingId(orderTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (order.getStatus() != DeliveryStatus.IN_PROGRESS) {
            throw new IllegalStateException("La commande doit être en cours pour être livrée");
        }

        order.setStatus(DeliveryStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        // Libérer le livreur
        if (order.getAssignedDeliveryPerson() != null) {
            order.getAssignedDeliveryPerson().setIsAvailable(true);
        }

        DeliveryOrder updated = repository.save(order);
        return mapper.toResponse(updated);
    }

    @Override
    public DeliveryOrderResponse cancelOrder(UUID orderTrackingId, String reason) {
        DeliveryOrder order = repository.findByTrackingId(orderTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (order.getStatus() == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Une commande livrée ne peut pas être annulée");
        }

        order.setStatus(DeliveryStatus.CANCELLED);

        // Libérer le livreur si assigné
        if (order.getAssignedDeliveryPerson() != null) {
            order.getAssignedDeliveryPerson().setIsAvailable(true);
        }

        DeliveryOrder updated = repository.save(order);
        return mapper.toResponse(updated);
    }

    @Override
    public void deleteOrder(UUID orderTrackingId) {
        DeliveryOrder order = repository.findByTrackingId(orderTrackingId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        repository.delete(order);
    }
}
