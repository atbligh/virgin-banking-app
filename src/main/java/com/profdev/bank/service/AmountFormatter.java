package com.profdev.bank.service;

import com.profdev.bank.config.AppProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AmountFormatter {

    private final AppProperties appProperties;

    public AmountFormatter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String format(BigDecimal amount) {
        return appProperties.currencySymbol() + amount;
    }
}
