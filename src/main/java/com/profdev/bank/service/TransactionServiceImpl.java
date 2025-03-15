package com.profdev.bank.service;

import com.profdev.bank.data.load.DataLoader;
import com.profdev.bank.model.Transaction;
import com.profdev.bank.model.mapper.TransactionMapper;
import com.profdev.bank.model.sort.TransactionBeanSortOrder;
import com.profdev.bank.service.result.AverageSpendPerMonthForCategory;
import com.profdev.bank.service.result.TotalPerCategory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final DataLoader dataLoader;

    private final TransactionMapper mapper;

    private List<Transaction> transactions;

    private final AmountFormatter af;

    public TransactionServiceImpl(DataLoader dataLoader, TransactionMapper mapper, AmountFormatter af) {
        this.dataLoader = dataLoader;
        this.mapper = mapper;
        this.af = af;
    }

    @Override
    public List<Transaction> getAll() {
        load();
        return transactions;
    }

    @Override
    public List<Transaction> getForCategory(String category, TransactionBeanSortOrder sortOrder) {

        if (StringUtils.isEmpty(category)) {
            return Collections.emptyList();
        }

        load();
        return transactions.stream()
                .filter(transaction -> transaction.getCategory().equals(category))
                .sorted(sortOrder.getComparator())
                .toList();
    }

    @Override
    public List<TotalPerCategory> getTotalPerCategory() {
        load();
        return transactions.stream()
                .collect(Collectors.toMap(Transaction::getCategory, Transaction::getMonetaryAmount, BigDecimal::add))
                .entrySet().stream()
                .map(entry -> TotalPerCategory.builder()
                        .category(entry.getKey())
                        .monetaryAmount(entry.getValue(), af)
                        .build())
                .toList();
    }

    @Override
    public List<AverageSpendPerMonthForCategory> getAverageSpendPerMonthForCategory(String category) {
        load();

        Map<String, BigDecimalSummaryStatistics> averages = transactions.stream()
                .filter(transaction -> transaction.getCategory().equals(category))
                .collect(Collectors.groupingBy((Transaction::getMonth),
                        Collectors2.summarizingBigDecimal(Transaction::getMonetaryAmount)));

        return averages.entrySet().stream()
                .map(entry -> AverageSpendPerMonthForCategory.builder()
                        .category(category)
                        .month(entry.getKey())
                        .monetaryAmount(entry.getValue().getAverage(), af)
                        .build())
                .toList();
    }

    @Override
    public BigDecimal getHighestSpendForCategoryAndYear(String category, int year) {
        load();
        return transactions.stream()
                .filter(transaction -> transaction.getCategory().equals(category))
                .filter(transaction -> transaction.getDate().getYear() == year)
                .map(Transaction::getMonetaryAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getLowestSpendForCategoryAndYear(String category, int year) {
        load();
        return transactions.stream()
                .filter(transaction -> transaction.getCategory().equals(category))
                .filter(transaction -> transaction.getDate().getYear() == year)
                .map(Transaction::getMonetaryAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private void load() {
        if (transactions == null) {
            transactions = dataLoader.retrieveData()
                    .stream()
                    .map(mapper::mapModelFromData)
                    .toList();
        }
    }
}
