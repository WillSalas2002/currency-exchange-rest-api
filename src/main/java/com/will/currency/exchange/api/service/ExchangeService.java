package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.ExchangeRateDto;
import com.will.currency.exchange.api.model.ExchangeRate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();

    public ExchangeRateDto calculateDirectExchange(ExchangeRate exchangeRate, BigDecimal amount) {
        return new ExchangeRateDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                exchangeRate.getRate().multiply(amount)
        );
    }

    public static ExchangeService getINSTANCE() {
        return INSTANCE;
    }
}
