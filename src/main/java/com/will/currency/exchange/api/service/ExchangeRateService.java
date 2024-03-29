package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.ExchangeRateDto;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateRepository exchangeRateRepository = ExchangeRateRepository.getInstance();

    public Optional<ExchangeRateDto> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateRepository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .stream()
                .map(this::mapToExchangeRateDto)
                .findFirst();
    }

    public List<ExchangeRateDto> findAll() {
        return exchangeRateRepository.findAll()
                .stream()
                .map(this::mapToExchangeRateDto)
                .collect(Collectors.toList());
    }

    private ExchangeRateDto mapToExchangeRateDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(exchangeRate.getId(), exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate());
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
