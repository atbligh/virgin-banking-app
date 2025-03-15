package com.profdev.bank.model.parser;

import com.profdev.bank.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionAmountParserTest {

    private static final String CURRENCY_SYMBOL = "£";

    @Mock
    private AppProperties appProperties;

    private TransactionAmountParser underTest;

    @BeforeEach
    void setUp() {
        when(appProperties.currencySymbol()).thenReturn(CURRENCY_SYMBOL);
        underTest = new TransactionAmountParser(appProperties);
    }

    @DisplayName("parse with valid amount including two decimal places should return correct amount")
    @Test
    void parse_withValidAmountIncludingTwoDecimalPlaces_shouldReturnCorrectAmount() {
        // Given
        String amountString = CURRENCY_SYMBOL + "123.45";
        BigDecimal expected = new BigDecimal("123.45");

        // When
        BigDecimal actual = underTest.parse(amountString);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("parse with valid amount including one decimal place should return correct amount")
    @Test
    void parse_withValidAmountIncludingOneDecimalPlace_shouldReturnCorrectAmount() {
        // Given
        String amountString = CURRENCY_SYMBOL + "123.4";
        BigDecimal expected = new BigDecimal("123.40");

        // When
        BigDecimal actual = underTest.parse(amountString);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("parse with valid amount excluding decimal places should return correct amount")
    @Test
    void parse_withValidAmountExcludingDecimalPlaces_shouldReturnCorrectAmount() {
        // Given
        String amountString = CURRENCY_SYMBOL + "123";
        BigDecimal expected = new BigDecimal("123.00");

        // When
        BigDecimal actual = underTest.parse(amountString);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("parse with valid amount excluding decimal places single digit should return correct amount")
    @Test
    void parse_withValidAmountExcludingDecimalPlacesSingleDigit_shouldReturnCorrectAmount() {
        // Given
        String amountString = CURRENCY_SYMBOL + "7";
        BigDecimal expected = new BigDecimal("7.00");

        // When
        BigDecimal actual = underTest.parse(amountString);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("parse with large amount should return correct amount")
    @Test
    void parse_withLargeAmount_shouldReturnCorrectAmount() {
        // Given
        String amountString = "£123,456,789.99";
        BigDecimal expected = new BigDecimal("123456789.99");

        // When
        BigDecimal actual = underTest.parse(amountString);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("parse with three decimal places should throw exception")
    @Test
    void parse_withThreeDecimalPlaces_shouldThrowException() {
        // Given
        String amountString = CURRENCY_SYMBOL + "123.234";

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.parse(amountString));
        assertThat(exception.getMessage()).isEqualTo("Invalid amount format: £123.234");
    }

    @DisplayName("parse with no currency symbol should throw exception")
    @Test
    void parse_withNoCurrencySymbol_shouldThrowException() {
        // Given
        String amountString = "123.234";

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.parse(amountString));
        assertThat(exception.getMessage()).isEqualTo("Invalid amount format: 123.234");
    }

    @DisplayName("parse with empty amount should throw exception")
    @Test
    void parse_withEmptyAmount_shouldThrowException() {
        // Given
        String amountString = "";

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.parse(amountString));
        assertThat(exception.getMessage()).isEqualTo("Amount cannot be null or empty");
    }

    @DisplayName("parse with null amount should throw exception")
    @Test
    void parse_withNullAmount_shouldThrowException() {
        // Given

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.parse(null));
        assertThat(exception.getMessage()).isEqualTo("Amount cannot be null or empty");
    }

    @DisplayName("parse with negative amount should throw exception")
    @Test
    void parse_withNegativeAmount_shouldThrowException() {
        // Given
        String amountString = "-" + CURRENCY_SYMBOL + "123.234";

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.parse(amountString));
        assertThat(exception.getMessage()).isEqualTo("Invalid amount format: -£123.234");
    }
}