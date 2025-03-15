package com.profdev.bank.service;

import com.profdev.bank.config.AppProperties;
import com.profdev.bank.data.DataRecord;
import com.profdev.bank.data.load.DataLoader;
import com.profdev.bank.model.Transaction;
import com.profdev.bank.model.mapper.TransactionMapper;
import com.profdev.bank.model.parser.TransactionAmountParser;
import com.profdev.bank.model.sort.TransactionBeanSortOrder;
import com.profdev.bank.service.result.AverageSpendPerMonthForCategory;
import com.profdev.bank.service.result.TotalPerCategory;
import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    private static final String GROCERIES = "Groceries";
    private static final String UTILITIES = "Utilities";
    private static final String TRANSPORT = "Transport";
    private static final String ENTERTAINMENT = "Entertainment";
    private static final String OTHER = "Other";
    private static final List<String> CATEGORIES = List.of(GROCERIES, UTILITIES, TRANSPORT, ENTERTAINMENT, OTHER);

    private static final String CURRENCY_SYMBOL = "£";

    @Mock
    private DataLoader dataLoader;

    @Mock
    private AppProperties appProperties;

    private AmountFormatter af;

    private TransactionMapper mapper;

    private List<DataRecord> dataRecords;

    private TransactionServiceImpl underTest;

    @BeforeEach
    void setUp() {
        when(appProperties.currencySymbol()).thenReturn(CURRENCY_SYMBOL);
        af = new AmountFormatter(appProperties);
        dataRecords = getDataRecords(30);
        lenient().when(dataLoader.retrieveData()).thenReturn(dataRecords);
        mapper = new TransactionMapper(new TransactionAmountParser(appProperties));
        underTest = new TransactionServiceImpl(dataLoader, mapper, af);
    }

    @Nested
    class AllTransactionsTests {

        @DisplayName("getAll with valid data should return correct transactions")
        @Test
        void getAll_withValidData_shouldReturnCorrectTransactions() {
            // Given

            // When
            List<Transaction> actual = underTest.getAll();

            // Then
            List<Transaction> expected = getTransactionBeans(dataRecords);
            assertThat(actual).containsExactlyElementsOf(expected);
        }
    }

    @Nested
    class AllTransactionsForCategoryTests {

        @DisplayName("getForCategory with valid category and desc sort should return correct transactions in order")
        @ParameterizedTest
        @MethodSource("getCategories")
        void getForCategory_withValidCategoryAndDescSort_shouldReturnCorrectTransactionsInOrder(String category) {
            // Given
            List<Transaction> expected = dataRecords.stream()
                    .filter(r -> r.getCategory().equals(category))
                    .map(mapper::mapModelFromData)
                    .toList();

            // When
            List<Transaction> actual = underTest.getForCategory(category, TransactionBeanSortOrder.DESC);

            // Then
            assertThat(actual).size().isEqualTo(expected.size());
            assertThat(actual).extracting(Transaction::getCategory).containsOnly(category);
            assertThat(actual).isSortedAccordingTo(Comparator.comparing(Transaction::getDate).reversed());
        }

        @DisplayName("getForCategory with valid category and asc sort should return correct transactions in order")
        @ParameterizedTest
        @MethodSource("getCategories")
        void getForCategory_withValidCategoryAndAscSort_shouldReturnCorrectTransactionsInOrder(String category) {
            // Given
            List<Transaction> expected = dataRecords.stream()
                    .filter(r -> r.getCategory().equals(category))
                    .map(mapper::mapModelFromData)
                    .toList();

            // When
            List<Transaction> actual = underTest.getForCategory(category, TransactionBeanSortOrder.ASC);

            // Then
            assertThat(actual).size().isEqualTo(expected.size());
            assertThat(actual).extracting(Transaction::getCategory).containsOnly(category);
            assertThat(actual).isSortedAccordingTo(Comparator.comparing(Transaction::getDate));
        }

        @DisplayName("getForCategory with non existent category should return no transactions")
        @Test
        void getForCategory_nonExistentCategory_shouldReturnNoTransactions() {
            // Given

            // When
            List<Transaction> actual = underTest.getForCategory("non existent category", TransactionBeanSortOrder.DESC);

            // Then
            assertThat(actual).isEmpty();
        }

        @DisplayName("getForCategory with null category should return no transactions")
        @Test
        void getForCategory_nullCategory_shouldReturnNoTransactions() {
            // Given

            // When
            List<Transaction> actual = underTest.getForCategory(null, TransactionBeanSortOrder.DESC);

            // Then
            assertThat(actual).isEmpty();
        }

        @DisplayName("getForCategory with blank category should return no transactions")
        @Test
        void getForCategory_blankCategory_shouldReturnNoTransactions() {
            // Given

            // When
            List<Transaction> actual = underTest.getForCategory("", TransactionBeanSortOrder.DESC);

            // Then
            assertThat(actual).isEmpty();
        }

        private static Stream<String> getCategories() {
            return CATEGORIES.stream();
        }
    }

    @Nested
    class TotalPerCategoryTests {

        @DisplayName("getTotalPerCategory with valid data should return correct values")
        @Test
        void getTotalPerCategory_withValidData_shouldReturnCorrectValues() {
            // Given
            List<DataRecord> dataRecords = getDataRecords(100);
            when(dataLoader.retrieveData()).thenReturn(dataRecords);

            // When
            List<TotalPerCategory> actual = underTest.getTotalPerCategory();

            // Then
            actual.forEach(totalPerCategory -> {
                BigDecimal expected = dataRecords.stream()
                        .filter(dataRecord -> dataRecord.getCategory().equals(totalPerCategory.category()))
                        .map(dataRecord -> mapper.mapModelFromData(dataRecord).getMonetaryAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                assertThat(totalPerCategory.total()).isEqualTo(CURRENCY_SYMBOL + expected);
            });
        }

        @DisplayName("getTotalPerCategory with no matching transactions should return empty list")
        @Test
        void getTotalPerCategory_noMatchingTransactions_shouldReturnEmptyList() {
            // Given
            when(dataLoader.retrieveData()).thenReturn(Collections.emptyList());

            // When
            List<TotalPerCategory> actual = underTest.getTotalPerCategory();

            // Then
            assertThat(actual).isEmpty();

        }
    }

    @Nested
    class AverageSpendPerMonthForCategoryTests {

        @DisplayName("getAverageSpendPerMonthForCategory with valid data should return correct values")
        @ParameterizedTest
        @MethodSource("getCategories")
        void getAverageSpendPerMonthForCategory_withValidData_shouldReturnCorrectValues(String category) {
            // Given
            List<DataRecord> dataRecordsExcludeCategory = getDataRecordsExcludeCategory(500, category);
            List<DataRecord> dataRecordsIncludeCategory = getDataRecordsForCategory(50, category);
            List<DataRecord> allDataRecords = mergeRecords(dataRecordsExcludeCategory, dataRecordsIncludeCategory);
            when(dataLoader.retrieveData()).thenReturn(allDataRecords);

            // When
            List<AverageSpendPerMonthForCategory> actual = underTest.getAverageSpendPerMonthForCategory(category);

            // Then
            actual.forEach(averageSpend -> {
                BigDecimalSummaryStatistics stats = dataRecordsIncludeCategory.stream()
                        .filter(dataRecord -> dataRecord.getMonth().equals(averageSpend.month()))
                        .map(dataRecord -> mapper.mapModelFromData(dataRecord).getMonetaryAmount())
                        .collect(Collectors2.summarizingBigDecimal(data -> data));
                AverageSpendPerMonthForCategory expected = AverageSpendPerMonthForCategory.builder()
                        .month(averageSpend.month())
                        .category(category)
                        .monetaryAmount(stats.getAverage(), af)
                        .build();
                assertThat(averageSpend).isEqualTo(expected);
            });
        }

        @DisplayName("getAverageSpendPerMonthForCategory with no matching transactions should return empty list")
        @ParameterizedTest
        @MethodSource("getCategories")
        void getAverageSpendPerMonthForCategory_noMatchingTransactions_shouldReturnEmptyList(String category) {
            // Given
            List<DataRecord> dataRecords = getDataRecordsExcludeCategory(300, category);
            when(dataLoader.retrieveData()).thenReturn(dataRecords);

            // When
            List<AverageSpendPerMonthForCategory> actual = underTest.getAverageSpendPerMonthForCategory(category);

            // Then
            assertThat(actual).isEmpty();
        }

        private static Stream<String> getCategories() {
            return CATEGORIES.stream();
        }
    }

    @Nested
    class HighestSpendForCategoryAndYearTests {

        @DisplayName("getHighestSpendPerCategory with valid data should return correct value")
        @ParameterizedTest
        @CsvSource({"Groceries, 2019", "Utilities, 2020", "Transport, 2024", "Entertainment, 2025", "Other, 2015",})
        void getHighestSpendForCategoryAndYear_withValidData_shouldReturnCorrectValue(String category, int year) {
            // Given
            List<DataRecord> nonYearDataRecords = getDataRecordsExcludeYearAndCategory(50, category, year);
            List<DataRecord> yearDataRecords = getDataRecordsForCategoryAndYear(10, category, year);
            List<DataRecord> allDataRecords = mergeRecords(nonYearDataRecords, yearDataRecords);
            when(dataLoader.retrieveData()).thenReturn(allDataRecords);

            BigDecimal expected = yearDataRecords.stream()
                    .map(mapper::mapModelFromData)
                    .map(Transaction::getMonetaryAmount)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            // When
            BigDecimal actual = underTest.getHighestSpendForCategoryAndYear(category, year);

            // Then
            assertThat(actual).isEqualTo(expected);
        }


        @DisplayName("getHighestSpendPerCategory with no data for year should return zero")
        @Test
        void getHighestSpendForCategoryAndYear_noDataForYear_shouldReturnZero() {
            // Given
            int year = 2020;
            List<DataRecord> nonYearDataRecords = getDataRecordsExcludeYearAndCategory(150, GROCERIES, year);
            when(dataLoader.retrieveData()).thenReturn(nonYearDataRecords);

            BigDecimal expected = new BigDecimal("0");

            // When
            BigDecimal actual = underTest.getHighestSpendForCategoryAndYear(GROCERIES, year);

            // Then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("getHighestSpendPerCategory with no data for category should return zero")
        @Test
        void getHighestSpendForCategoryAndYear_noDataForCategory_shouldReturnZero() {
            // Given
            int year = 2020;
            List<DataRecord> dataRecords = getDataRecordsForCategoryAndYear(30, GROCERIES, year);
            when(dataLoader.retrieveData()).thenReturn(dataRecords);

            BigDecimal expected = new BigDecimal("0");

            // When
            BigDecimal actual = underTest.getHighestSpendForCategoryAndYear(UTILITIES, year);

            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class LowestSpendForCategoryAndYearTests {

        @DisplayName("getLowestSpendPerCategory with valid data should return correct value")
        @ParameterizedTest
        @CsvSource({"Groceries, 2019", "Utilities, 2020", "Transport, 2024", "Entertainment, 2025", "Other, 2015",})
        void getLowestSpendForCategoryAndYear_withValidData_shouldReturnCorrectValue(String category, int year) {
            // Given
            List<DataRecord> nonYearDataRecords = getDataRecordsExcludeYearAndCategory(99, category, year);
            List<DataRecord> yearDataRecords = getDataRecordsForCategoryAndYear(10, category, year);
            List<DataRecord> allDataRecords = mergeRecords(nonYearDataRecords, yearDataRecords);
            when(dataLoader.retrieveData()).thenReturn(allDataRecords);

            BigDecimal expected = yearDataRecords.stream()
                    .map(mapper::mapModelFromData)
                    .map(Transaction::getMonetaryAmount)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            // When
            BigDecimal actual = underTest.getLowestSpendForCategoryAndYear(category, year);

            // Then
            assertThat(actual).isEqualTo(expected);
        }


        @DisplayName("getLowestSpendPerCategory with no data for year should return zero")
        @Test
        void getLowestSpendForCategoryAndYear_noDataForYear_shouldReturnZero() {
            // Given
            int year = 2020;
            List<DataRecord> nonYearDataRecords = getDataRecordsExcludeYearAndCategory(350, GROCERIES, year);
            when(dataLoader.retrieveData()).thenReturn(nonYearDataRecords);

            BigDecimal expected = new BigDecimal("0");

            // When
            BigDecimal actual = underTest.getLowestSpendForCategoryAndYear(GROCERIES, year);

            // Then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("getLowestSpendPerCategory with no data for category should return zero")
        @Test
        void getLowestSpendForCategoryAndYear_noDataForCategory_shouldReturnZero() {
            // Given
            int year = 2020;
            List<DataRecord> dataRecords = getDataRecordsForCategoryAndYear(30, GROCERIES, year);
            when(dataLoader.retrieveData()).thenReturn(dataRecords);

            BigDecimal expected = new BigDecimal("0");

            // When
            BigDecimal actual = underTest.getLowestSpendForCategoryAndYear(UTILITIES, year);

            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    private List<DataRecord> getDataRecords(int numRecords) {
        return Instancio.ofList(DataRecord.class)
                .size(numRecords)
                .supply(field(DataRecord::getCategory), this::getRandomCategory)
                .supply(field(DataRecord::getAmount), this::getRandomAmount)
                .create();
    }

    private List<DataRecord> getDataRecordsExcludeYearAndCategory(int numRecords, String category, int year) {
        List<DataRecord> transactions = getDataRecords(numRecords);
        return transactions.stream()
                .filter(t -> t.getTransactionDate().getYear() != year && !t.getCategory().equals(category))
                .toList();
    }

    private List<DataRecord> getDataRecordsExcludeCategory(int numRecords, String category) {
        List<DataRecord> transactions = getDataRecords(numRecords);
        return transactions.stream()
                .filter(t -> !t.getCategory().equals(category))
                .toList();
    }

    private List<DataRecord> getDataRecordsForCategoryAndYear(int numRecords, String category, int year) {
        List<DataRecord> transactions = getDataRecords(numRecords);
        transactions.forEach(t -> {
            t.setTransactionDate(t.getTransactionDate().withYear(year));
            t.setCategory(category);
        });
        return transactions;
    }

    private List<DataRecord> getDataRecordsForCategory(int numRecords, String category) {
        List<DataRecord> transactions = getDataRecords(numRecords);
        transactions.forEach(t -> {
            t.setCategory(category);
        });
        return transactions;
    }

    private String getRandomCategory() {
        return CATEGORIES.get(ThreadLocalRandom.current().nextInt(CATEGORIES.size()));
    }

    private String getRandomAmount() {
        return CURRENCY_SYMBOL + ThreadLocalRandom.current().nextInt(1, 1000);
    }

    private List<Transaction> getTransactionBeans(List<DataRecord> data) {
        return data.stream()
                .map(mapper::mapModelFromData)
                .toList();
    }

    private List<DataRecord> mergeRecords(List<DataRecord> dr1, List<DataRecord> dr2) {
        return Stream.concat(dr1.stream(), dr2.stream()).toList();
    }
}