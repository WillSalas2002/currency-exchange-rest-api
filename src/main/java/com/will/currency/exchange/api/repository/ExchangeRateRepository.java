package com.will.currency.exchange.api.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateRepository {
    private final static ExchangeRateRepository INSTANCE = new ExchangeRateRepository();


    public static ExchangeRateRepository getINSTANCE() {
        return INSTANCE;
    }
}
