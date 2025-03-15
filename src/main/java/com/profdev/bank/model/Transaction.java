package com.profdev.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Transaction {

    @JsonFormat(pattern="dd/MMM/yyyy")
    @NotNull
    private LocalDate date;

    @NotBlank
    private String vendor;

    @NotBlank
    private String type;

    @JsonIgnore
    @NotNull
    private BigDecimal monetaryAmount;

    @NotBlank
    private String category;

    @NotBlank
    String amount;

    @JsonIgnore
    public String getMonth() {
        return date.getMonth().name();
    }
}
