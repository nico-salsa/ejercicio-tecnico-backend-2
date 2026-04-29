package com.sofka.banking.customerservice.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sofka.banking.customerservice.infrastructure.persistence.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void cleanUp() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateAndGetCustomer() throws Exception {
        String body = """
                {
                  "name": "Jose Lema",
                  "gender": "M",
                  "age": 30,
                  "identification": "123456",
                  "address": "Otavalo",
                  "phone": "098254785",
                  "customerId": "CL-001",
                  "password": "1234",
                  "status": true
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jose Lema"))
                .andExpect(jsonPath("$.customerId").value("CL-001"));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identification").value("123456"));
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer() throws Exception {
        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCustomerPayload() throws Exception {
        String body = """
                {
                  "name": "",
                  "gender": "M",
                  "age": -1,
                  "identification": "",
                  "address": "",
                  "phone": "",
                  "customerId": "",
                  "password": "",
                  "status": true
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldUpdatePatchAndDeleteCustomer() throws Exception {
        String createBody = """
                {
                  "name": "Juan Osorio",
                  "gender": "M",
                  "age": 35,
                  "identification": "ABC123",
                  "address": "Quito",
                  "phone": "098874587",
                  "customerId": "CL-002",
                  "password": "1245",
                  "status": true
                }
                """;

        String createdResponse = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = Long.valueOf(createdResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        String updateBody = """
                {
                  "name": "Juan Osorio Actualizado",
                  "gender": "M",
                  "age": 36,
                  "identification": "ABC124",
                  "address": "Quito Norte",
                  "phone": "098874588",
                  "customerId": "CL-002",
                  "password": "1245",
                  "status": true
                }
                """;

        mockMvc.perform(put("/clientes/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Osorio Actualizado"))
                .andExpect(jsonPath("$.age").value(36));

        String patchBody = """
                {
                  "status": false
                }
                """;

        mockMvc.perform(patch("/clientes/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));

        mockMvc.perform(delete("/clientes/{id}", customerId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/{id}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }
}
