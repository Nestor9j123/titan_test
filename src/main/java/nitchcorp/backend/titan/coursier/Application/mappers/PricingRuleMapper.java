package nitchcorp.backend.titan.coursier.Application.mappers;

import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.PricingRuleRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.PricingRuleResponse;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.PricingRule;
import nitchcorp.backend.titan.coursier.Infrastructure.DeliveryCompanyRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class PricingRuleMapper {

    private DeliveryCompanyRepository deliveryCompanyRepository;

    public PricingRule toEntity(PricingRuleRequest request){
        if (request == null) {
            throw new IllegalArgumentException("La règle de tarification n'existe pas");
        }
        PricingRule pricingRule = new PricingRule();
        pricingRule.setTrackingId(UUID.randomUUID());
        pricingRule.setMinDistanceKm(request.minDistanceKm());
        pricingRule.setMaxDistanceKm(request.maxDistanceKm());
        pricingRule.setBasePrice(request.basePrice());
        pricingRule.setPricePerKm(request.pricePerKm());

        DeliveryCompany deliveryCompany = deliveryCompanyRepository
                .findByTrackingId(request.deliveryCompanyTrackingId()).orElseThrow(() -> new IllegalArgumentException("La compagnie n'existe pas"));
        pricingRule.setDeliveryCompany(deliveryCompany);



        return pricingRule;
    }

    public PricingRuleResponse toResponse(PricingRule pricingRule){
        if (pricingRule == null) {
            throw new IllegalArgumentException("La règle de tarification n'existe pas");
        }
        return new PricingRuleResponse(
                pricingRule.getTrackingId(),
                pricingRule.getMinDistanceKm(),
                pricingRule.getMaxDistanceKm(),
                pricingRule.getBasePrice(),
                pricingRule.getPricePerKm(),
                pricingRule.getDeliveryCompany().getTrackingId()
        );
    }
}
