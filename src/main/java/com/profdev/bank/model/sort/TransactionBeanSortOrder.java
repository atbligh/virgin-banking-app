package com.profdev.bank.model.sort;

import com.profdev.bank.model.Transaction;
import lombok.Getter;

import java.util.Comparator;

@Getter
public enum TransactionBeanSortOrder {
    ASC(Comparator.comparing(Transaction::getDate)),
    DESC(Comparator.comparing(Transaction::getDate).reversed());

    final Comparator<Transaction> comparator;

    TransactionBeanSortOrder(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }
}
