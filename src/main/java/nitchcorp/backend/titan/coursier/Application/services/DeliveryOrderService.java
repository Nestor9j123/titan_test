package nitchcorp.backend.titan.coursier.Application.services;

import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryOrderRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryOrderResponse;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.PriceCalculationResponse;
import nitchcorp.backend.titan.coursier.Domain.enums.DeliveryStatus;

import java.util.List;
import java.util.UUID;

public interface DeliveryOrderService {
    PriceCalculationResponse calculatePrice(DeliveryOrderRequest request);

    DeliveryOrderResponse createOrder(DeliveryOrderRequest request);
    List<DeliveryOrderResponse> getAllOrders();

    DeliveryOrderResponse findByTrackingId(UUID trackingId);
    List<DeliveryOrderResponse> getOrderByStatus(DeliveryStatus status);
    List<DeliveryOrderResponse> getOrderByCompany(UUID companyTrackingId);
    List<DeliveryOrderResponse> getOrderByDeliveryPerson(UUID deliveryPersonTrackingId);

    DeliveryOrderResponse assignToDeliveryPerson(UUID orderTrackingId, UUID deliveryPersonTrackingId);

    DeliveryOrderResponse startDelivery(UUID orderTrackingId);

    DeliveryOrderResponse completeDelivery(UUID orderTrackingId);

    DeliveryOrderResponse cancelOrder(UUID orderTrackingId, String reason);

    void deleteOrder(UUID orderTrackingId);
}
