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
                  "customerId": "JL001",
                  "customerName": "Jose Lema",
                  "status": true
                }
                """;

        String accountResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("478758"))
                .andExpect(jsonPath("$.customerId").value("JL001"))
                .andExpect(jsonPath("$.customerName").value("Jose Lema"))
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
                  "customerId": "",
                  "customerName": "",
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
    void shouldCoverF6WithEndToEndBankingFlow() throws Exception {
        String accountBody = """
                {
                  "accountNumber": "778899",
                  "accountType": "Ahorro",
                  "initialBalance": 350.00,
                  "customerId": "JL001",
                  "customerName": "Jose Lema",
                  "status": true
                }
                """;

        String accountResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("778899"))
                .andExpect(jsonPath("$.availableBalance").value(350.0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long accountId = Long.valueOf(accountResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        String movementBody = """
                {
                  "movementDate": "2022-02-10T10:00:00",
                  "movementType": "DEPOSITO",
                  "amount": 150.00,
                  "accountId": %d
                }
                """.formatted(accountId);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movementBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.accountNumber").value("778899"))
                .andExpect(jsonPath("$.movementType").value("DEPOSITO"))
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.balance").value(500.0));

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("778899"))
                .andExpect(jsonPath("$.availableBalance").value(500.0));

        mockMvc.perform(get("/reportes")
                        .param("clienteId", "JL001")
                        .param("fechaInicio", "2022-02-01")
                        .param("fechaFin", "2022-02-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente").value("Jose Lema"))
                .andExpect(jsonPath("$[0].numeroCuenta").value("778899"))
                .andExpect(jsonPath("$[0].movimiento").value(150.0))
                .andExpect(jsonPath("$[0].saldoDisponible").value(500.0));
    }

    @Test
    void shouldUpdatePatchAndDeleteAccountAndMovement() throws Exception {
        String accountBody = """
                {
                  "accountNumber": "496825",
                  "accountType": "Ahorros",
                  "initialBalance": 540.00,
                  "customerId": "MM001",
                  "customerName": "Marianela Montalvo",
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
                  "customerId": "MM001",
                  "customerName": "Marianela Montalvo",
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

    @Test
    void shouldRejectMovementWhenInsufficientBalance() throws Exception {
        String accountBody = """
                {
                  "accountNumber": "585545",
                  "accountType": "Corriente",
                  "initialBalance": 100.00,
                  "customerId": "JL001",
                  "customerName": "Jose Lema",
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

        String movementBody = """
                {
                  "movementDate": "2026-04-29T10:00:00",
                  "movementType": "RETIRO",
                  "amount": -150.00,
                  "accountId": %d
                }
                """.formatted(accountId);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movementBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"))
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_BALANCE"))
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(100.0));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldRejectMovementPatchWhenInsufficientBalance() throws Exception {
        String accountBody = """
                {
                  "accountNumber": "225487",
                  "accountType": "Corriente",
                  "initialBalance": 100.00,
                  "customerId": "MM001",
                  "customerName": "Marianela Montalvo",
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

        String movementBody = """
                {
                  "movementDate": "2026-04-29T09:30:00",
                  "movementType": "DEPOSITO",
                  "amount": 50.00,
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

        String patchBody = """
                {
                  "amount": -150.00
                }
                """;

        mockMvc.perform(patch("/movimientos/{id}", movementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"))
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_BALANCE"))
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(get("/movimientos/{id}", movementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.balance").value(150.0));

        mockMvc.perform(get("/cuentas/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(150.0));
    }

    @Test
    void shouldReturnAccountStatementReportByCustomerAndDates() throws Exception {
        String savingsAccount = """
                {
                  "accountNumber": "225487",
                  "accountType": "Corriente",
                  "initialBalance": 100.00,
                  "customerId": "MM001",
                  "customerName": "Marianela Montalvo",
                  "status": true
                }
                """;

        String secondaryAccount = """
                {
                  "accountNumber": "496825",
                  "accountType": "Ahorros",
                  "initialBalance": 540.00,
                  "customerId": "MM001",
                  "customerName": "Marianela Montalvo",
                  "status": true
                }
                """;

        Long firstAccountId = Long.valueOf(mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(savingsAccount))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));

        Long secondAccountId = Long.valueOf(mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondaryAccount))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "movementDate": "2022-02-10T10:00:00",
                                  "movementType": "DEPOSITO",
                                  "amount": 600.00,
                                  "accountId": %d
                                }
                                """.formatted(firstAccountId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "movementDate": "2022-02-08T09:00:00",
                                  "movementType": "RETIRO",
                                  "amount": -540.00,
                                  "accountId": %d
                                }
                                """.formatted(secondAccountId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/reportes")
                        .param("clienteId", "MM001")
                        .param("fechaInicio", "2022-02-01")
                        .param("fechaFin", "2022-02-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente").value("Marianela Montalvo"))
                .andExpect(jsonPath("$[0].numeroCuenta").value("225487"))
                .andExpect(jsonPath("$[0].tipo").value("Corriente"))
                .andExpect(jsonPath("$[0].saldoInicial").value(100.0))
                .andExpect(jsonPath("$[0].estado").value(true))
                .andExpect(jsonPath("$[0].movimiento").value(600.0))
                .andExpect(jsonPath("$[0].saldoDisponible").value(700.0))
                .andExpect(jsonPath("$[1].numeroCuenta").value("496825"))
                .andExpect(jsonPath("$[1].movimiento").value(-540.0))
                .andExpect(jsonPath("$[1].saldoDisponible").value(0.0));
    }

    @Test
    void shouldReturnEmptyReportWhenCustomerHasNoMovementsInRange() throws Exception {
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "MM001")
                        .param("fechaInicio", "2022-02-01")
                        .param("fechaFin", "2022-02-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnBadRequestForInvalidReportDateRange() throws Exception {
        mockMvc.perform(get("/reportes")
                        .param("clienteId", "MM001")
                        .param("fechaInicio", "2022-02-11")
                        .param("fechaFin", "2022-02-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("REPORT_QUERY_INVALID"));
    }
}
