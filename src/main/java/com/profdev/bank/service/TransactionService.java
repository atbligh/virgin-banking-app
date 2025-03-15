package com.profdev.bank.service;

import com.profdev.bank.model.Transaction;
import com.profdev.bank.model.sort.TransactionBeanSortOrder;
import com.profdev.bank.service.result.AverageSpendPerMonthForCategory;
import com.profdev.bank.service.result.TotalPerCategory;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    List<Transaction> getAll();

    List<Transaction> getForCategory(String category, TransactionBeanSortOrder sortOrder);

    List<TotalPerCategory> getTotalPerCategory();

    BigDecimal getHighestSpendForCategoryAndYear(String category, int year);

    BigDecimal getLowestSpendForCategoryAndYear(String category, int year);

    List<AverageSpendPerMonthForCategory> getAverageSpendPerMonthForCategory(String category);
}
