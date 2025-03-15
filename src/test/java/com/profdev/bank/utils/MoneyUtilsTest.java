package com.profdev.bank.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyUtilsTest {

    @Test
    void fromString_stringValidInput_shouldRoundUp() {
        // Given

        // When
        BigDecimal result = MoneyUtils.fromString("123.456");

        // Then
        assertThat(new BigDecimal("123.46")).isEqualTo(result);
    }

    @Test
    void fromString_stringValidInput_shouldRoundUpHalf() {
        // Given

        // When
        BigDecimal result = MoneyUtils.fromString("123.455");

        // Then
        assertThat(new BigDecimal("123.46")).isEqualTo(result);
    }

    @Test
    void fromString_stringValidInput_shouldRoundDown() {
        // Given

        // When
        BigDecimal result = MoneyUtils.fromString("123.453");

        // Then
        assertThat(new BigDecimal("123.45")).isEqualTo(result);
    }

    @Test
    void fromString_stringInvalidInput_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> MoneyUtils.fromString("invalid"));
    }

    @Test
    void fromString_stringNullInput_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MoneyUtils.fromString(null));
    }

    @Test
    void parse_bigDecimalValidInput_shouldRoundUp() {
        // Given
        BigDecimal in = new BigDecimal("123.456");

        // When
        BigDecimal result = MoneyUtils.parse(in);

        // Then
        assertThat(new BigDecimal("123.46")).isEqualTo(result);
    }

    @Test
    void parse_bigDecimalValidInput_shouldRoundUpHalf() {
        // Given
        BigDecimal in = new BigDecimal("123.455");

        // When
        BigDecimal result = MoneyUtils.parse(in);

        // Then
        assertThat(new BigDecimal("123.46")).isEqualTo(result);
    }

    @Test
    void parse_bigDecimalValidInput_shouldRoundUpDOwn() {
        // Given
        BigDecimal in = new BigDecimal("123.454");

        // When
        BigDecimal result = MoneyUtils.parse(in);

        // Then
        assertThat(new BigDecimal("123.45")).isEqualTo(result);
    }

    @Test
    void parse_bigDecimalNullInput_ReturnsNull() {
        assertThrows(NullPointerException.class, () -> MoneyUtils.parse(null));
    }
}