package nitchcorp.backend.titan.coursier.Application.services;

import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryCompanyRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryCompanyResponse;

import java.util.List;
import java.util.UUID;

public interface DeliveryCompanyService {
    DeliveryCompanyResponse createCompany(DeliveryCompanyRequest request);
    List<DeliveryCompanyResponse> getAllCompanies();
    List<DeliveryCompanyResponse> getAllCompaniesActive();
    DeliveryCompanyResponse getCompanyByTrackingId(UUID trackingId);
    DeliveryCompanyResponse updateCompany(UUID trackingId, DeliveryCompanyRequest request);
    void deactivateCompany(UUID trackingId);
    public void activateCompany(UUID trackingId);
    void deleteCompany(UUID trackingId);
}
