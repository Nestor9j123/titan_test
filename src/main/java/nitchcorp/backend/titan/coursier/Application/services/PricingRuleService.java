package nitchcorp.backend.titan.coursier.Application.services;

import nitchcorp.backend.titan.coursier.Application.dtos.requests.PricingRuleRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.PricingRuleResponse;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PricingRuleService {
    BigDecimal calculatePrice(DeliveryCompany company, Double distanceInKm);
    PricingRuleResponse getPricingRuleByTrackingId(UUID trackingId);
    List<PricingRuleResponse> getAllPricingRulesByDeliveryCompany(DeliveryCompany deliveryCompany);

    PricingRuleResponse createPricingRule(PricingRuleRequest request);
    PricingRuleResponse updatePricingRule(UUID trackingId, PricingRuleRequest request);
    void deletePricingRule(UUID trackingId);


}
