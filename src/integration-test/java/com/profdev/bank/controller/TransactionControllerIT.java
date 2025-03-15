package com.profdev.bank.controller;

import com.profdev.bank.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("integration-test")
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerIT {

    @LocalServerPort
    private int port;

    private static final String BASE_URL = "http://localhost:%s/banking-app/transaction";

    @Nested
    class AllTransactions {

        private static final String PATH = "/all";

        @Test
        void getAll_shouldReturnAllTransactions() {
            // Given
            RestClient restClient = RestClient.builder()
                    .baseUrl(getBaseUrl())
                    .build();

            // When
            List<Transaction> transactions = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(PATH)
                            .build())
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            // Then
            assertThat(transactions)
                    .extracting("date", "vendor", "type", "amount", "category")
                    .containsExactly(
                            tuple(LocalDate.of(2020, 11, 1), "Morrisons", "card", "£10.40", "Groceries"),
                            tuple(LocalDate.of(2020, 11, 2), "Tesco", "card", "£25.31", "Groceries"),
                            tuple(LocalDate.of(2020, 11, 5), "Shell Garage", "card", "£50", "Vehicle"),
                            tuple(LocalDate.of(2020, 11, 6), "PureGym", "direct debit", "£38.50", "Health"));
        }
    }

    // TODO add nested test classes for controller endpoints GetForCategory, AverageSpendPerMonthForCategory,
    //  LowestSpendForCategoryAndYearTests and LowestSpendForCategoryAndYearTests

    private String getBaseUrl() {
        return String.format(BASE_URL, port);
    }
}