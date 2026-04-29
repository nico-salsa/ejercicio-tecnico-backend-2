package com.sofka.banking.customerservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sofka.banking.customerservice.api.dto.CustomerCreateRequest;
import com.sofka.banking.customerservice.api.dto.CustomerPatchRequest;
import com.sofka.banking.customerservice.api.dto.CustomerUpdateRequest;
import com.sofka.banking.customerservice.domain.exception.ResourceNotFoundException;
import com.sofka.banking.customerservice.domain.model.Customer;
import com.sofka.banking.customerservice.infrastructure.persistence.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("Jose Lema");
        customer.setGender("M");
        customer.setAge(30);
        customer.setIdentification("123");
        customer.setAddress("Direccion");
        customer.setPhone("099");
        customer.setCustomerId("C-1");
        customer.setPassword("1234");
        customer.setStatus(true);
    }

    @Test
    void shouldCreateCustomer() {
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Jose Lema",
                "M",
                30,
                "123",
                "Direccion",
                "099",
                "C-1",
                "1234",
                true
        );

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = customerService.create(request);

        assertThat(response.name()).isEqualTo("Jose Lema");
        assertThat(response.customerId()).isEqualTo("C-1");
        assertThat(response.status()).isTrue();
    }

    @Test
    void shouldPatchCustomerStatus() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = customerService.patch(1L, new CustomerPatchRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        ));

        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldUpdateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = customerService.update(1L, new CustomerUpdateRequest(
                "Marianela Montalvo",
                "F",
                28,
                "999",
                "Amazonas",
                "097548965",
                "C-2",
                "5678",
                true
        ));

        assertThat(response.name()).isEqualTo("Marianela Montalvo");
        assertThat(response.identification()).isEqualTo("999");
        assertThat(response.customerId()).isEqualTo("C-2");
    }

    @Test
    void shouldDeleteCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.delete(1L);

        verify(customerRepository).delete(customer);
    }

    @Test
    void shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(java.util.List.of(customer));

        var response = customerService.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).customerId()).isEqualTo("C-1");
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }
}
