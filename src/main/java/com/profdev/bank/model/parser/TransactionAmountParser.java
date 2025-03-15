package com.profdev.bank.model.parser;

import com.profdev.bank.config.AppProperties;
import com.profdev.bank.utils.MoneyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionAmountParser {

    private final Pattern amountPattern;

    public TransactionAmountParser(AppProperties appProperties) {
        amountPattern = Pattern.compile("^" + appProperties.currencySymbol() + "[0-9]{1,9}+(\\.[0-9][0-9]?)?");
    }

    public BigDecimal parse(String amount) {
        if (StringUtils.isBlank(amount)) {
            throw new IllegalArgumentException("Amount cannot be null or empty");
        }
        String noCommas = amount.replaceAll(",", "");
        Matcher matcher = amountPattern.matcher(noCommas);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid amount format: %s".formatted(amount));
        }
        String monetaryAmount = noCommas.replaceAll("£", "");
        return MoneyUtils.fromString(monetaryAmount);
    }
}
