package com.profdev.bank.service.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.profdev.bank.service.AmountFormatter;
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
            this.monetaryAmount = amount;
            this.amount = formatter.format(this.monetaryAmount);
            return this;
        }
    }
}
