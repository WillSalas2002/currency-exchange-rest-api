package com.will.currency.exchange.api.repository;

import com.will.currency.exchange.api.exception.CustomException;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyRepository {
    private final static CurrencyRepository INSTANCE = new CurrencyRepository();
    private final static String FIND_ALL = """
            SELECT id, code, full_name, sign
            FROM currency;
            """;
    private final static String FIND_BY_CURRENCY_CODE = """
            SELECT id, code, full_name, sign
            FROM currency
            WHERE code = ?;
            """;

    private final static String SAVE_SQL = """
            INSERT INTO currency (code, full_name, sign)
            VALUES (?, ?, ?);
            """;

    public List<Currency> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            List<Currency> currencies = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> findByCurrencyCode(String currencyCode) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CURRENCY_CODE)) {
            Currency currency = null;
            statement.setString(1, currencyCode);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency save(Currency currency) throws CustomException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                currency.setId(keys.getInt(1));
            }
            return currency;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new CustomException("Specified currency already exists", e);
            }
            throw new RuntimeException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }

    public static CurrencyRepository getINSTANCE() {
        return INSTANCE;
    }
}
