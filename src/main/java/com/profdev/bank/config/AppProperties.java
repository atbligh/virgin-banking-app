package com.profdev.bank.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;
import java.util.Locale;

@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(
        @NotBlank
        String dataType,
        @NotBlank
        String dataFile,

        String currencySymbol
) {
        public String currencySymbol() {
                return Currency.getInstance(Locale.getDefault()).getSymbol();
        }
}
