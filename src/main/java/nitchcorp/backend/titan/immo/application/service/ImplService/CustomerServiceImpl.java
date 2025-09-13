package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.CustomerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.CustomerResponse;
import nitchcorp.backend.titan.immo.application.mapper.CustomerMapper;
import nitchcorp.backend.titan.immo.domain.model.Customer;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.CustomerRepository;
import nitchcorp.backend.titan.immo.application.service.CustomerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (request.email() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        Customer customer = customerMapper.toEntity(request);
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerByTrackingId(UUID trackingId) throws UserNotFoundException {
        Customer customer = customerRepository.getCustomerByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with trackingId: " + trackingId));
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(UUID trackingId, CustomerRequest request) throws UserNotFoundException {
        Customer customer = customerRepository.getCustomerByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with trackingId: " + trackingId));
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        // customer.setAddress(request.address()); // Address not available in User entity
        customer.setCountry(request.country());
        customer.setPreferredContactMethod(request.preferredContactMethod());
        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    public void deleteCustomer(UUID trackingId) throws UserNotFoundException {
        Customer customer = customerRepository.getCustomerByTrackingId(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with trackingId: " + trackingId));
        customerRepository.delete(customer);
    }
}
