package com.sofka.banking.accountservice.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sofka.banking.accountservice.infrastructure.persistence.AccountRepository;
import com.sofka.banking.accountservice.infrastructure.persistence.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AccountMovementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void cleanUp() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void shouldCreateAccountAndMovement() throws Exception {
        String accountBody = """
                {
                  "accountNumber": "478758",
                  "accountType": "Ahorro",
                  "initialBalance": 2000.00,
                  "status": true
                }
                """;

        String accountResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("478758"))
                .andExpect(jsonPath("$.availableBalance").value(2000.0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accountId = Long.valueOf(accountResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        String movementBody = """
                {
                  "movementDate": "2026-04-28T10:00:00",
                  "movementType": "DEPOSITO",
                  "amount": 500.00,
                  "accountId": %d
                }
                """.formatted(accountId);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movementBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.accountNumber").value("478758"))
                .andExpect(jsonPath("$.movementType").value("DEPOSITO"))
                .andExpect(jsonPath("$.balance").value(2500.0));

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(2500.0));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movementType").value("DEPOSITO"));
    }

    @Test
    void shouldReturnNotFoundForUnknownMovement() throws Exception {
        mockMvc.perform(get("/movimientos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void shouldReturnBadRequestForInvalidAccountPayload() throws Exception {
        String body = """
                {
                  "accountNumber": "",
                  "accountType": "",
                  "initialBalance": -1,
                  "status": true
                }
                """;

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldUpdatePatchAndDeleteAccountAndMovement() throws Exception {
        String accountBody = """
                {
                  "accountNumber": "496825",
                  "accountType": "Ahorros",
                  "initialBalance": 540.00,
                  "status": true
                }
                """;

        String accountResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accountId = Long.valueOf(accountResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        String updateAccountBody = """
                {
                  "accountNumber": "496825",
                  "accountType": "Corriente",
                  "initialBalance": 640.00,
                  "status": true
                }
                """;

        mockMvc.perform(put("/cuentas/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateAccountBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("Corriente"))
                .andExpect(jsonPath("$.initialBalance").value(640.0))
                .andExpect(jsonPath("$.availableBalance").value(640.0));

        String movementBody = """
                {
                  "movementDate": "2026-04-28T15:00:00",
                  "movementType": "RETIRO",
                  "amount": -140.00,
                  "accountId": %d
                }
                """.formatted(accountId);

        String movementResponse = mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movementBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long movementId = Long.valueOf(movementResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        String patchMovementBody = """
                {
                  "amount": 40.00
                }
                """;

        mockMvc.perform(patch("/movimientos/{id}", movementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchMovementBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movementType").value("DEPOSITO"))
                .andExpect(jsonPath("$.balance").value(680.0));

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(680.0));

        mockMvc.perform(delete("/movimientos/{id}", movementId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(640.0));

        mockMvc.perform(delete("/cuentas/{id}", accountId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }
}
