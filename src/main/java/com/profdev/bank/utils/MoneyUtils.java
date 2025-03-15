package com.profdev.bank.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MoneyUtils {

    public static BigDecimal fromString(String money) {
        return new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal parse(BigDecimal money) {
        return money.setScale(2, RoundingMode.HALF_UP);
    }
}
