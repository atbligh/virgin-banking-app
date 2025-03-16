package com.profdev.bank.controller;

import com.profdev.bank.controller.response.StringResponse;
import com.profdev.bank.model.Transaction;
import com.profdev.bank.service.AmountFormatter;
import com.profdev.bank.service.TransactionService;
import com.profdev.bank.service.result.AverageSpendPerMonthForCategory;
import com.profdev.bank.service.result.TotalPerCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService service;

    private final AmountFormatter af;

    public TransactionController(TransactionService service, AmountFormatter af) {
        this.service = service;
        this.af = af;
    }

    @GetMapping(path = "/all", produces = "application/json")
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(path = "/total/category", produces = "application/json")
    public ResponseEntity<List<TotalPerCategory>> getTotalPerCategory() {
        return ResponseEntity.ok(service.getTotalPerCategory());
    }

    @GetMapping(path = "/average/month-category/{category}", produces = "application/json")
    public ResponseEntity<List<AverageSpendPerMonthForCategory>> getAverageSpendPerMonthForCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getAverageSpendPerMonthForCategory(category));
    }

    @GetMapping(path = "/highest-spend/category/{category}/year/{year}", produces = "application/json")
    public ResponseEntity<StringResponse> getHighestSpendForCategoryAndYear(@PathVariable String category, @PathVariable int year) {
        return ResponseEntity.ok(new StringResponse(af.format(service.getHighestSpendForCategoryAndYear(category, year))));
    }

    @GetMapping(path = "/lowest-spend/category/{category}/year/{year}", produces = "application/json")
    public ResponseEntity<StringResponse> getLowestSpendForCategoryAndYear(@PathVariable String category, @PathVariable int year) {
        return ResponseEntity.ok(new StringResponse(af.format(service.getLowestSpendForCategoryAndYear(category, year))));
    }
}
