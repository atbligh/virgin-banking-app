package com.profdev.bank.service.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.profdev.bank.service.AmountFormatter;
import com.profdev.bank.utils.MoneyUtils;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AverageSpendPerMonthForCategory(
        String category,
        String month,
        String amount,
        @JsonIgnore
        BigDecimal monetaryAmount) {

    public static class AverageSpendPerMonthForCategoryBuilder {

        public AverageSpendPerMonthForCategoryBuilder monetaryAmount(BigDecimal amount, AmountFormatter formatter) {
            this.monetaryAmount = MoneyUtils.parse(amount);
            this.amount = formatter.format(this.monetaryAmount);
            return this;
        }
    }
}
