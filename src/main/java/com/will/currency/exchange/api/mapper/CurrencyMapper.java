package com.will.currency.exchange.api.mapper;

import com.will.currency.exchange.api.dto.CurrencyDto;
import com.will.currency.exchange.api.model.Currency;

public class CurrencyMapper {

    public static Currency mapToCurrency(CurrencyDto currencyDto) {
        return new Currency(currencyDto.id(), currencyDto.code(), currencyDto.fullName(), currencyDto.sign());
    }

    public static CurrencyDto mapToCurrencyDto(Currency currency) {
        return new CurrencyDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }
}
