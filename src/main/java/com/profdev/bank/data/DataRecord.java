package com.profdev.bank.data;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DataRecord {

    @CsvDate("dd/MMM/yyyy")
    @CsvBindByName(column = "Transaction Date")
    private LocalDate transactionDate;

    @CsvBindByName(column = "Vendor")
    private String vendor;

    @CsvBindByName(column = "Type")
    private String type;

    @CsvBindByName(column = "Amount")
    private String amount;

    @CsvBindByName(column = "Category")
    private String category;

    public String getMonth() {
        return transactionDate != null ? transactionDate.getMonth().name() : null;
    }
}
