package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.CurrencyDto;
import com.will.currency.exchange.api.dto.ErrorResponse;
import com.will.currency.exchange.api.dto.ExchangeRateDto;
import com.will.currency.exchange.api.exception.CustomException;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRateDto> exchangeRateDtoList = exchangeRateService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDtoList);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");
        if (baseCurrencyCode == null || baseCurrencyCode.length() != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid base currency"));
            return;
        }
        if (targetCurrencyCode == null || targetCurrencyCode.length() != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid target currency"));
            return;
        }
        BigDecimal rate;
        if (rateStr != null && rateStr.replace(",", ".").matches("^[-+]?\\d*\\.?\\d+$")) {
            rate = new BigDecimal(rateStr);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid rate specified."));
            return;
        }
        baseCurrencyCode = baseCurrencyCode.toUpperCase();
        targetCurrencyCode = targetCurrencyCode.toUpperCase();
        try {
            Optional<CurrencyDto> baseOptional = currencyService.findByCurrencyCode(baseCurrencyCode);
            if (baseOptional.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Base Currency Not found"));
                return;
            }
            Optional<CurrencyDto> targetOptional = currencyService.findByCurrencyCode(targetCurrencyCode);
            if (targetOptional.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Target Currency Not found"));
                return;
            }
            CurrencyDto baseCurrency = new CurrencyDto(baseOptional.get().id(), baseOptional.get().code(), baseOptional.get().fullName(), baseOptional.get().sign());
            CurrencyDto targetCurrency = new CurrencyDto(targetOptional.get().id(), targetOptional.get().code(), targetOptional.get().fullName(), targetOptional.get().sign());

            ExchangeRate saved = exchangeRateService.save(new ExchangeRateDto(0, baseCurrency, targetCurrency, rate));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), saved);
        } catch (CustomException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error"));
        }
    }
}
