package com.profdev.bank.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(
        @NotBlank
        String dataType,
        @NotBlank
        String dataFile,
        @NotBlank
        String currencySymbol
) {
}
