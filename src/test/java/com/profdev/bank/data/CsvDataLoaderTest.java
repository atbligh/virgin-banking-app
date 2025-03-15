package com.profdev.bank.data;

import com.profdev.bank.config.AppProperties;
import com.profdev.bank.data.load.CsvDataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvDataLoaderTest {

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private CsvDataLoader underTest;

    @DisplayName("retrieveData with a valid CSV file should return a list of records")
    @Test
    void retrieveData_withValidCsvFile_shouldReturnListOfRecords() {
        // Given
        when(appProperties.dataFile()).thenReturn("test-transaction-data.csv");

        // When
        List<DataRecord> transactions = underTest.retrieveData();

        // Then
        assertThat(transactions)
                .extracting("transactionDate", "vendor", "type", "amount", "category")
                .containsExactly(
                        tuple(LocalDate.of(2020, 11, 1), "Morrisons", "card", "£10.40", "Groceries"),
                        tuple(LocalDate.of(2020, 11, 5), "Shell Garage", "card", "£50", "Vehicle"),
                        tuple(LocalDate.of(2020, 11, 6), "PureGym", "direct debit", "£38.50", "Health"));
    }

    @DisplayName("retrieveData with an empty CSV file should return an empty list")
    @Test
    void retrieveData_withEmptyCsvFile_shouldReturnEmptyList() {
        // Given
        when(appProperties.dataFile()).thenReturn("test-transaction-data-empty.csv");

        // When
        List<DataRecord> transactions = underTest.retrieveData();

        // Then
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @DisplayName("retrieveData with an invalid CSV file path should throw a RuntimeException")
    @Test
    void retrieveData_withInvalidCsvFilePath_shouldThrowRuntimeException() {
        // Given
        when(appProperties.dataFile()).thenReturn("invalid_path.csv");

        // When & Then
        assertThrows(RuntimeException.class, () -> underTest.retrieveData());
    }

    @DisplayName("retrieveData with a malformed CSV file content should throw a RuntimeException")
    @Test
    void retrieveData_withMalformedCsvFileContent_shouldThrowRuntimeException() {
        // Given
        when(appProperties.dataFile()).thenReturn("test-transaction-data-malformed.csv");

        // When & Then
        assertThrows(RuntimeException.class, () -> underTest.retrieveData());
    }
}