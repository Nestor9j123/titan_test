package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.CustomerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.CustomerResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerByTrackingId(UUID trackingId) throws UserNotFoundException;
    CustomerResponse updateCustomer(UUID trackingId, CustomerRequest request) throws UserNotFoundException;
    void deleteCustomer(UUID trackingId) throws UserNotFoundException;
}