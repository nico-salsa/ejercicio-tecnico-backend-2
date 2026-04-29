package com.sofka.banking.customerservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "person_id")
public class Customer extends Person {

    @Column(nullable = false, unique = true, length = 50)
    private String customerId;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(nullable = false)
    private Boolean status;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(status);
    }
}
