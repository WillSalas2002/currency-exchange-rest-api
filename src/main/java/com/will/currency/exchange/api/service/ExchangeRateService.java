package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.ExchangeRateDto;
import com.will.currency.exchange.api.exception.CustomException;
import com.will.currency.exchange.api.mapper.CurrencyMapper;
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

    public ExchangeRate save(ExchangeRateDto exchangeRateDto) throws CustomException {
        ExchangeRate exchangeRate = mapToExchangeRate(exchangeRateDto);
        return exchangeRateRepository.save(exchangeRate);
    }

    public ExchangeRateDto update(ExchangeRateDto updatedExchangeRateDto) {
        ExchangeRate exchangeRate = mapToExchangeRate(updatedExchangeRateDto);
        ExchangeRate update = exchangeRateRepository.update(exchangeRate);
        return mapToExchangeRateDto(update);
    }

    private ExchangeRateDto mapToExchangeRateDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                CurrencyMapper.mapToCurrencyDto(exchangeRate.getBaseCurrency()),
                CurrencyMapper.mapToCurrencyDto(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate()
        );
    }

    private ExchangeRate mapToExchangeRate(ExchangeRateDto exchangeRateDto) {
        return new ExchangeRate(
                exchangeRateDto.getId(),
                CurrencyMapper.mapToCurrency(exchangeRateDto.getBaseCurrency()),
                CurrencyMapper.mapToCurrency(exchangeRateDto.getTargetCurrency()),
                exchangeRateDto.getRate()
        );
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
