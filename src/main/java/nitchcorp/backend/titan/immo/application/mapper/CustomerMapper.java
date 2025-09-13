package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.CustomerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.CustomerResponse;
import nitchcorp.backend.titan.immo.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        return new CustomerResponse(
                customer.getTrackingId(),
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCountry(),
                customer.isActif(),
                customer.getPreferredContactMethod()
        );
    }

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CustomerRequest cannot be null");
        }
        Customer customer = new Customer();
        customer.setTrackingId(UUID.randomUUID());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setCountry(request.country());
        customer.setPreferredContactMethod(request.preferredContactMethod());
        return customer;
    }
}