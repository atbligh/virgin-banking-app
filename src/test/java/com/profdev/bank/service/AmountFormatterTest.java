package com.profdev.bank.service;

import com.profdev.bank.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmountFormatterTest {

    private static final String CURRENCY_SYMBOL = "£";

    @Mock
    private AppProperties appProperties;

    private AmountFormatter formatter;

    @BeforeEach
    void setUp() {
        when(appProperties.currencySymbol()).thenReturn(CURRENCY_SYMBOL);
        formatter = new AmountFormatter(appProperties);
    }

    @Test
    void format_withValidAmount_shouldFormatCorrectly() {
        // Given
        BigDecimal amount = new BigDecimal("1234.56");
        String expected = CURRENCY_SYMBOL + amount;

        // When
        String actual = formatter.format(amount);

        // Then
        assertThat(actual).isEqualTo(expected);
    }
}