package com.will.currency.exchange.api.dto;

import com.will.currency.exchange.api.model.Currency;

import java.math.BigDecimal;

public record ExchangeRateDto (Currency baseCurrency,
                               Currency targetCurrency,
                               BigDecimal rate,
                               BigDecimal amount,
                               BigDecimal convertedAmount) {
}
