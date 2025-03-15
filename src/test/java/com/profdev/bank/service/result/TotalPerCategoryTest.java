package com.profdev.bank.service.result;

import com.profdev.bank.config.AppProperties;
import com.profdev.bank.service.AmountFormatter;
import com.profdev.bank.utils.MoneyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotalPerCategoryTest {

    private static final String CURRENCY_SYMBOL = "£";
    private static final String FOOD = "Food";
    private static final String UTILITIES = "Utilities";
    private static final String REFUND = "Refund";

    @Mock
    private AppProperties appProperties;

    private AmountFormatter af;

    @BeforeEach
    void setUp() {
        when(appProperties.currencySymbol()).thenReturn(CURRENCY_SYMBOL);
        af = new AmountFormatter(appProperties);
    }

    @Test
    void builderBuild_withValidMonetaryAmount_shouldValueValuesCorrectly() {
        // Given
        BigDecimal amount = MoneyUtils.fromString("123.45");

        // When
        TotalPerCategory totalPerCategory = TotalPerCategory.builder()
                .category(FOOD)
                .monetaryAmount(amount, af)
                .build();

        // Then
        assertEquals(FOOD, totalPerCategory.category());
        assertEquals(CURRENCY_SYMBOL + "123.45", totalPerCategory.total());
        assertEquals(amount, totalPerCategory.monetaryAmount());
    }

    @Test
    void builderBuild_withZeroMonetaryAmount_shouldHaveValuesCorrectly() {
        BigDecimal amount = MoneyUtils.parse(BigDecimal.ZERO);

        // When
        TotalPerCategory totalPerCategory = TotalPerCategory.builder()
                .category(UTILITIES)
                .monetaryAmount(amount, af)
                .build();

        // Then
        assertEquals(UTILITIES, totalPerCategory.category());
        assertEquals(CURRENCY_SYMBOL + "0.00", totalPerCategory.total());
        assertEquals(amount, totalPerCategory.monetaryAmount());
    }

    @Test
    void builderBuild_withNegativeMonetaryAmount_shouldHaveCorrectValues() {
        // Given
        BigDecimal amount = MoneyUtils.fromString("-45.678");

        // When
        TotalPerCategory totalPerCategory = TotalPerCategory.builder()
                .category(REFUND)
                .monetaryAmount(amount, af)
                .build();

        // Then
        assertEquals(REFUND, totalPerCategory.category());
        assertEquals(CURRENCY_SYMBOL + "-45.68", totalPerCategory.total());
        assertEquals(amount, totalPerCategory.monetaryAmount());
    }
}