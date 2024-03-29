package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.CurrencyDto;
import com.will.currency.exchange.api.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCode = req.getPathInfo().substring(1).toUpperCase();
        // TODO: Validation of the currency code and exception handling!!!
        Optional<CurrencyDto> resultOptional = currencyService.findByCurrencyCode(currencyCode);
        if (resultOptional.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), "Not found");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), resultOptional.get());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
