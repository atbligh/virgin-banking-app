package com.profdev.bank.model.mapper;

import com.profdev.bank.config.AppProperties;
import com.profdev.bank.data.DataRecord;
import com.profdev.bank.model.Transaction;
import com.profdev.bank.model.parser.TransactionAmountParser;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    private static final String CURRENCY_SYMBOL = "£";

    @Mock
    private AppProperties appProperties;

    private TransactionMapper underTest;

    @BeforeEach
    void setUp() {
        when(appProperties.currencySymbol()).thenReturn(CURRENCY_SYMBOL);
        underTest = new TransactionMapper(new TransactionAmountParser(appProperties));
    }

    @DisplayName("mapModelFromData should map to TransactionBean correctly when given a valid TransactionDataRecord")
    @Test
    void mapModelFromData_shouldMapToTransactionBeanCorrectly_whenGivenAValidTransactionDataRecord() {
        // Given
        String amount = "100.00";
        DataRecord dataRecord = Instancio.create(DataRecord.class);
        dataRecord.setAmount(CURRENCY_SYMBOL + amount);

        // When
        Transaction transaction = underTest.mapModelFromData(dataRecord);

        // Then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getDate()).isEqualTo(dataRecord.getTransactionDate());
        assertThat(transaction.getVendor()).isEqualTo(dataRecord.getVendor());
        assertThat(transaction.getType()).isEqualTo(dataRecord.getType());
        assertThat(transaction.getMonetaryAmount()).isEqualTo(new BigDecimal(amount));
        assertThat(transaction.getAmount()).isEqualTo(CURRENCY_SYMBOL + amount);
        assertThat(transaction.getCategory()).isEqualTo(dataRecord.getCategory());
    }
}