package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.ExchangeRateDto;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeRateRepository exchangeRateRepository = ExchangeRateRepository.getINSTANCE();

    public ExchangeRateDto calculateExchange(String baseCurrency, String targetCurrency, BigDecimal amount) {

        Optional<ExchangeRate> tryDirectExchangeRate = findByCurrencyCodes(baseCurrency, targetCurrency);
        if (tryDirectExchangeRate.isPresent()) {
            return calculateDirectExchange(tryDirectExchangeRate.get(), amount);
        }

        Optional<ExchangeRate> tryReverseExchangeRate = findByCurrencyCodes(targetCurrency, baseCurrency);
        if (tryReverseExchangeRate.isPresent()) {
            return calculateReverseExchange(tryReverseExchangeRate.get(), amount);
        }

        throw new RuntimeException("NOT FOUND EXCHANGE RATE");
    }

    public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrency, String targetCurrency) {
        return exchangeRateRepository.findByCurrencyCodes(targetCurrency, baseCurrency);
    }

    private ExchangeRateDto calculateDirectExchange(ExchangeRate exchangeRate, BigDecimal amount) {
        return new ExchangeRateDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                exchangeRate.getRate().multiply(amount)
        );
    }

    private ExchangeRateDto calculateReverseExchange(ExchangeRate exchangeRate, BigDecimal amount) {
        ExchangeRate revertedExchangeRate = revertExchangeRate(exchangeRate);
        return calculateDirectExchange(revertedExchangeRate, amount);
    }

    private ExchangeRate revertExchangeRate(ExchangeRate exchangeRate) {
        return new ExchangeRate(
                exchangeRate.getId(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_EVEN)
        );
    }

    public static ExchangeService getINSTANCE() {
        return INSTANCE;
    }
}
