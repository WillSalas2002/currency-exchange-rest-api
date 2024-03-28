package com.will.currency.exchange.api.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();



    public static ExchangeService getINSTANCE() {
        return INSTANCE;
    }
}
