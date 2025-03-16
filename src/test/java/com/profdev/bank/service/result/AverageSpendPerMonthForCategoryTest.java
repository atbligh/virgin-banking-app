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
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AverageSpendPerMonthForCategoryTest {

    private static final String CURRENCY_SYMBOL = "£";
    private static final String GROCERIES = "Groceries";
    private static final String JANUARY = "January";
    private static final String VEHICLE = "Vehicle";
    private static final String MARCH = "March";
    private static final String HEALTH = "Health";
    private static final String NOVEMBER = "November";

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
        String amount = "123.45";
        BigDecimal monetaryAmount = MoneyUtils.fromString(amount);

        // When
        AverageSpendPerMonthForCategory result = AverageSpendPerMonthForCategory.builder()
                .category(GROCERIES)
                .month(JANUARY)
                .monetaryAmount(monetaryAmount, af)
                .build();

        // Then
        assertEquals(GROCERIES, result.category());
        assertEquals(JANUARY, result.month());
        assertEquals(CURRENCY_SYMBOL + "123.45", result.amount());
        assertEquals(monetaryAmount, result.monetaryAmount());
    }

    @Test
    void builderBuild_withValidMonetaryAmountMoreThanTwDecimalPlaces_shouldValueValuesCorrectly() {
        // Given
        String amount = "123.45678";
        BigDecimal monetaryAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);

        // When
        AverageSpendPerMonthForCategory result = AverageSpendPerMonthForCategory.builder()
                .category(GROCERIES)
                .month(JANUARY)
                .monetaryAmount(monetaryAmount, af)
                .build();

        // Then
        assertEquals(GROCERIES, result.category());
        assertEquals(JANUARY, result.month());
        assertEquals(CURRENCY_SYMBOL + "123.46", result.amount());
        assertEquals(monetaryAmount, result.monetaryAmount());
    }

    @Test
    void givenNegativeAmount_whenBuild_thenCorrectlyFormatted() {
        // Given
        String amount = "-123.45";
        BigDecimal monetaryAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);

        // When
        AverageSpendPerMonthForCategory result = AverageSpendPerMonthForCategory.builder()
                .category(VEHICLE)
                .month(MARCH)
                .monetaryAmount(monetaryAmount, af)
                .build();

        // Then
        assertEquals(VEHICLE, result.category());
        assertEquals(MARCH, result.month());
        assertEquals(CURRENCY_SYMBOL + "-123.45", result.amount());
        assertEquals(monetaryAmount, result.monetaryAmount());
    }

    @Test
    void givenZeroAmount_whenBuild_thenCorrectlyFormatted() {
        // Given
        BigDecimal monetaryAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // When
        AverageSpendPerMonthForCategory result = AverageSpendPerMonthForCategory.builder()
                .category(HEALTH)
                .month(NOVEMBER)
                .monetaryAmount(monetaryAmount, af)
                .build();

        // Then
        assertEquals(HEALTH, result.category());
        assertEquals(NOVEMBER, result.month());
        assertEquals("£0.00", result.amount());
        assertEquals(monetaryAmount, result.monetaryAmount());
    }
}