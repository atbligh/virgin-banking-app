package com.profdev.bank.model.mapper;

import com.profdev.bank.data.DataRecord;
import com.profdev.bank.model.Transaction;
import com.profdev.bank.model.parser.TransactionAmountParser;
import org.springframework.stereotype.Service;

@Service
public class TransactionMapper {

    private final TransactionAmountParser transactionAmountParser;

    public TransactionMapper(TransactionAmountParser transactionAmountParser) {
        this.transactionAmountParser = transactionAmountParser;
    }

    public Transaction mapModelFromData(DataRecord data) {
        Transaction transaction = new Transaction();
        transaction.setDate(data.getTransactionDate());
        transaction.setVendor(data.getVendor());
        transaction.setType(data.getType());
        transaction.setCategory(data.getCategory());
        transaction.setAmount(data.getAmount());
        transaction.setMonetaryAmount(transactionAmountParser.parse(data.getAmount()));
        return transaction;
    }
}
