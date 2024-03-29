package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.CurrencyDto;
import com.will.currency.exchange.api.repository.CurrencyRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyRepository currencyRepository = CurrencyRepository.getINSTANCE();

    public List<CurrencyDto> findByCurrencyCode(String code) {
        return currencyRepository.findByCurrencyCode(code)
                .stream()
                .map(currency -> new CurrencyDto(
                        currency.getId(),
                        currency.getCode(),
                        currency.getFullName(),
                        currency.getSign()))
                .collect(Collectors.toList());
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
