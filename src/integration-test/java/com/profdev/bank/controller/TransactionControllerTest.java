package com.profdev.bank.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profdev.bank.model.Transaction;
import com.profdev.bank.service.AmountFormatter;
import com.profdev.bank.service.TransactionService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({TransactionController.class, AmountFormatter.class})
class TransactionControllerTest {

    private static ObjectMapper om;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        om = new ObjectMapper();
        om.findAndRegisterModules();
    }

    @Nested
    class AllTransactions {

        @DisplayName("all transactions with valid data should return all transactions correctly")
        @Test
        void allTransactions_withValidData_shouldReturnAllTransactionsCorrectly() throws Exception {

            // Given
            List<Transaction> expected = Instancio.ofList(Transaction.class).size(2).create();
            when(transactionService.getAll()).thenReturn(expected);

            // When
            MvcResult result = mockMvc.perform(get("/transaction/all"))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            List<Transaction> actual = om.readValue(json, new TypeReference<>() {
            });

            // Then
            assertThat(actual)
                    .extracting("date", "vendor", "type", "amount", "category")
                    .containsExactly(
                            tuple(expected.get(0).getDate(),
                                    expected.get(0).getVendor(),
                                    expected.get(0).getType(),
                                    expected.get(0).getAmount(),
                                    expected.get(0).getCategory()),
                            tuple(expected.get(1).getDate(),
                                    expected.get(1).getVendor(),
                                    expected.get(1).getType(),
                                    expected.get(1).getAmount(),
                                    expected.get(1).getCategory()));
        }

        @DisplayName("all transactions with no data should return empty list")
        @Test
        void allTransactions_withNoData_shouldReturnEmptyList() throws Exception {

            // Given
            when(transactionService.getAll()).thenReturn(Collections.emptyList());

            // When
            MvcResult result = mockMvc.perform(get("/transaction/all"))
                    .andExpect(status().isOk())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            List<Transaction> actual = om.readValue(json, new TypeReference<>() {
            });

            // Then
            assertThat(actual).isEmpty();
        }
    }

    // TODO add nested test classes for controller endpoints GetForCategory, AverageSpendPerMonthForCategory,
    //  LowestSpendForCategoryAndYearTests and LowestSpendForCategoryAndYearTests including failure test cases
}