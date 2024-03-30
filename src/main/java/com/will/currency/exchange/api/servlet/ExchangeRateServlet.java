package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.ExchangeRateDto;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("patch"))
            doPatch(req, resp);
        else
            super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCodes = req.getPathInfo().substring(1).toUpperCase();
        String baseCurrencyCode = currencyCodes.substring(0, 3);
        String targetCurrencyCode = currencyCodes.substring(3, 6);
        //TODO: need to validate the fields and handle exceptions!!!
        Optional<ExchangeRateDto> resultOptional = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        if (resultOptional.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), "Not Found");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), resultOptional.get());
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCodes = req.getPathInfo().substring(1).toUpperCase();
        String baseCurrencyCode = currencyCodes.substring(0, 3);
        String targetCurrencyCode = currencyCodes.substring(3, 6);
        String rateStr = null;

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String requestBody = sb.toString();
        String[] params = requestBody.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("rate")) {
                rateStr = keyValue[1];
                break;
            }
        }
        //TODO: need to validate the fields and handle exceptions!!!
        BigDecimal rate = null;
        if (rateStr != null && rateStr.matches("^[-+]?\\d*\\.?\\d+$"))
            rate = new BigDecimal(rateStr);

        Optional<ExchangeRateDto> exchangeRateDtoOptional = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRateDtoOptional.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), "Not Found");
            return;
        }
        ExchangeRateDto exchangeRateDto = exchangeRateDtoOptional.get();
        exchangeRateDto.setRate(rate);
        ExchangeRateDto updatedDto = exchangeRateService.update(exchangeRateDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), updatedDto);
    }
}
