package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.ExchangeDto;
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
    private final ExchangeRateRepository exchangeRateRepository = ExchangeRateRepository.getInstance();

    public ExchangeDto calculateExchange(String baseCurrency, String targetCurrency, BigDecimal amount) {

        Optional<ExchangeRate> tryDirectExchangeRate = findByCurrencyCodes(baseCurrency, targetCurrency);
        if (tryDirectExchangeRate.isPresent()) {
            return calculateDirectExchange(tryDirectExchangeRate.get(), amount);
        }

        Optional<ExchangeRate> tryReverseExchangeRate = findByCurrencyCodes(targetCurrency, baseCurrency);
        if (tryReverseExchangeRate.isPresent()) {
            return calculateReverseExchange(tryReverseExchangeRate.get(), amount);
        }

        return calculateUsdBasedExchange(baseCurrency, targetCurrency, amount);
    }

    public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrency, String targetCurrency) {
        return exchangeRateRepository.findByCurrencyCodes(baseCurrency, targetCurrency);
    }

    private ExchangeDto calculateUsdBasedExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        String usdCode = "USD";

        Optional<ExchangeRate> currencyOptional1 = findByCurrencyCodes(usdCode, baseCurrencyCode);
        if (currencyOptional1.isEmpty())
            throw new RuntimeException("NOT FOUND EXCHANGE RATE");

        Optional<ExchangeRate> currencyOptional2 = findByCurrencyCodes(usdCode, targetCurrencyCode);
        if (currencyOptional2.isEmpty())
            throw new RuntimeException("NOT FOUND EXCHANGE RATE");

        BigDecimal usdToBase = currencyOptional1.get().getRate();
        BigDecimal usdToTarget = currencyOptional2.get().getRate();
        BigDecimal realRate = usdToTarget.divide(usdToBase, 6, RoundingMode.HALF_EVEN);

        ExchangeRate exchangeRate = new ExchangeRate(0,
                currencyOptional1.get().getTargetCurrency(),
                currencyOptional2.get().getTargetCurrency(),
                realRate);

        return calculateDirectExchange(exchangeRate, amount);
    }

    private ExchangeDto calculateDirectExchange(ExchangeRate exchangeRate, BigDecimal amount) {
        return new ExchangeDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                exchangeRate.getRate().multiply(amount)
        );
    }

    private ExchangeDto calculateReverseExchange(ExchangeRate exchangeRate, BigDecimal amount) {
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
