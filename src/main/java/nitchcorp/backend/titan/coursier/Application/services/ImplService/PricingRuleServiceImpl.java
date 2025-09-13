package nitchcorp.backend.titan.coursier.Application.services.ImplService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.PricingRuleRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.PricingRuleResponse;
import nitchcorp.backend.titan.coursier.Application.mappers.PricingRuleMapper;
import nitchcorp.backend.titan.coursier.Domain.models.DeliveryCompany;
import nitchcorp.backend.titan.coursier.Domain.models.PricingRule;
import nitchcorp.backend.titan.coursier.Infrastructure.PricingRuleRepository;
import nitchcorp.backend.titan.coursier.Application.services.PricingRuleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;
    private final PricingRuleMapper mapper;

    @Override
    public BigDecimal calculatePrice(DeliveryCompany company, Double distanceInKm) {
        Optional<PricingRule> rule = pricingRuleRepository
                .findByDeliveryCompanyAndDistanceRange(company, distanceInKm);

        if (rule.isPresent()) {
            PricingRule pricingRule = rule.get();
            BigDecimal basePrice = pricingRule.getBasePrice();
            BigDecimal pricePerKm = pricingRule.getPricePerKm();
            BigDecimal distancePrice = pricePerKm.multiply(BigDecimal.valueOf(distanceInKm));

            return basePrice.add(distancePrice);
        } else {
            BigDecimal defaultBasePrice = new BigDecimal("5.00");
            BigDecimal defaultPricePerKm = new BigDecimal("2.50");

            return defaultBasePrice.add(defaultPricePerKm.multiply(BigDecimal.valueOf(distanceInKm)));
        }
    }

    @Override
    public PricingRuleResponse getPricingRuleByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("Le trackingId ne peut pas être null");
        }

        PricingRule rule = pricingRuleRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune règle trouvée avec ce trackingId"));

        return mapper.toResponse(rule);
    }

    @Override
    public List<PricingRuleResponse> getAllPricingRulesByDeliveryCompany(DeliveryCompany deliveryCompany) {
        if (deliveryCompany.getTrackingId() == null) {
            throw new IllegalArgumentException("La compagnie n'existe pas");
        }
        List<PricingRule> rules = pricingRuleRepository.findAllByDeliveryCompany(deliveryCompany);
        return rules.stream().map(mapper::toResponse).toList();
    }

    @Override
    public PricingRuleResponse createPricingRule(PricingRuleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de réclamation ne peut pas être null");
        }

        PricingRule rule = mapper.toEntity(request);
        PricingRule savedPricingRule = pricingRuleRepository.save(rule);

        return mapper.toResponse(savedPricingRule);
    }

    @Override
    public PricingRuleResponse updatePricingRule(UUID trackingId, PricingRuleRequest request) {
        if (trackingId == null || request == null) {
            throw new IllegalArgumentException("Le trackingId et la demande ne peuvent pas être null");
        }

        PricingRule existingRule = pricingRuleRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune règle trouvée avec ce trackingId"));

        existingRule.setBasePrice(request.basePrice());
        existingRule.setPricePerKm(request.pricePerKm());
        existingRule.setMinDistanceKm(request.minDistanceKm());
        existingRule.setMaxDistanceKm(request.maxDistanceKm());

        if (!existingRule.getDeliveryCompany().getTrackingId().equals(request.deliveryCompanyTrackingId())){
            PricingRule tempPricingRule = mapper.toEntity(request);
            existingRule.setDeliveryCompany(tempPricingRule.getDeliveryCompany());
        }

        PricingRule updatedRule = pricingRuleRepository.save(existingRule);
        return mapper.toResponse(updatedRule);
    }

    @Override
    public void deletePricingRule(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("Le trackingId ne peut pas être null");
        }
        PricingRule rule = pricingRuleRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune règle trouvée avec ce trackingId"));
        pricingRuleRepository.delete(rule);
    }
}
