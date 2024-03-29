package com.will.currency.exchange.api.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
