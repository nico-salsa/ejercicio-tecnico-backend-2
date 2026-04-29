package com.sofka.banking.customerservice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void shouldReportActiveCustomerState() {
        Customer customer = new Customer();

        customer.setStatus(true);
        assertThat(customer.isActive()).isTrue();

        customer.setStatus(false);
        assertThat(customer.isActive()).isFalse();
    }
}
