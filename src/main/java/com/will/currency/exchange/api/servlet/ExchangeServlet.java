package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.ExchangeDto;
import com.will.currency.exchange.api.service.ExchangeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = ExchangeService.getINSTANCE();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        String amountStr = req.getParameter("amount");
        //TODO: validate and handle exceptions
        BigDecimal amount = null;
        if (amountStr != null && amountStr.matches("^[-+]?\\d*\\.?\\d+$"))
            amount = new BigDecimal(amountStr);

        ExchangeDto exchangeDto = exchangeService.calculateExchange(from, to, amount);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeDto);
    }
}
