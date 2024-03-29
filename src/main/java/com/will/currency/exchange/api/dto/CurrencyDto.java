package com.will.currency.exchange.api.dto;

public record CurrencyDto(Integer id,
                          String code,
                          String fullName,
                          String sign) {
}
