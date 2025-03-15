package com.profdev.bank.service.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.profdev.bank.service.AmountFormatter;
import com.profdev.bank.utils.MoneyUtils;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TotalPerCategory(
        String category,
        String total,
        @JsonIgnore
        BigDecimal monetaryAmount) {

    public static class TotalPerCategoryBuilder {

        public TotalPerCategoryBuilder monetaryAmount(BigDecimal amount, AmountFormatter af) {
            this.monetaryAmount = MoneyUtils.parse(amount);
            this.total = af.format(this.monetaryAmount);
            return this;
        }
    }
}
