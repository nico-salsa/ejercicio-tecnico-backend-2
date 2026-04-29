package com.sofka.banking.customerservice.application;

import com.sofka.banking.customerservice.api.dto.CustomerCreateRequest;
import com.sofka.banking.customerservice.api.dto.CustomerPatchRequest;
import com.sofka.banking.customerservice.api.dto.CustomerResponse;
import com.sofka.banking.customerservice.api.dto.CustomerUpdateRequest;
import com.sofka.banking.customerservice.domain.exception.ResourceNotFoundException;
import com.sofka.banking.customerservice.domain.model.Customer;
import com.sofka.banking.customerservice.infrastructure.persistence.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return toResponse(loadCustomer(id));
    }

    public CustomerResponse create(CustomerCreateRequest request) {
        Customer customer = new Customer();
        applyCreate(customer, request);
        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer customer = loadCustomer(id);
        applyUpdate(customer, request);
        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse patch(Long id, CustomerPatchRequest request) {
        Customer customer = loadCustomer(id);

        if (request.name() != null) {
            customer.setName(request.name());
        }
        if (request.gender() != null) {
            customer.setGender(request.gender());
        }
        if (request.age() != null) {
            customer.setAge(request.age());
        }
        if (request.identification() != null) {
            customer.setIdentification(request.identification());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
        if (request.phone() != null) {
            customer.setPhone(request.phone());
        }
        if (request.customerId() != null) {
            customer.setCustomerId(request.customerId());
        }
        if (request.password() != null) {
            customer.setPassword(request.password());
        }
        if (request.status() != null) {
            customer.setStatus(request.status());
        }

        return toResponse(customerRepository.save(customer));
    }

    public void delete(Long id) {
        Customer customer = loadCustomer(id);
        customerRepository.delete(customer);
    }

    private Customer loadCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
    }

    private void applyCreate(Customer customer, CustomerCreateRequest request) {
        customer.setName(request.name());
        customer.setGender(request.gender());
        customer.setAge(request.age());
        customer.setIdentification(request.identification());
        customer.setAddress(request.address());
        customer.setPhone(request.phone());
        customer.setCustomerId(request.customerId());
        customer.setPassword(request.password());
        customer.setStatus(request.status());
    }

    private void applyUpdate(Customer customer, CustomerUpdateRequest request) {
        customer.setName(request.name());
        customer.setGender(request.gender());
        customer.setAge(request.age());
        customer.setIdentification(request.identification());
        customer.setAddress(request.address());
        customer.setPhone(request.phone());
        customer.setCustomerId(request.customerId());
        customer.setPassword(request.password());
        customer.setStatus(request.status());
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getGender(),
                customer.getAge(),
                customer.getIdentification(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getCustomerId(),
                customer.getStatus()
        );
    }
}
