package com.will.currency.exchange.api.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
