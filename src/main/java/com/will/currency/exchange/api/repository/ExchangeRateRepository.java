package com.will.currency.exchange.api.repository;

import com.will.currency.exchange.api.exception.CustomException;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateRepository {
    private final static ExchangeRateRepository INSTANCE = new ExchangeRateRepository();
    private final static String FIND_ALL_SQL = """
            SELECT er.id AS id,
                   c_base.id AS c_base_id,
                   c_base.code AS c_base_code,
                   c_base.full_name AS c_base_full_name,
                   c_base.sign AS c_base_sign,
                   c_target.id AS c_target_id,
                   c_target.code AS c_target_code,
                   c_target.full_name AS c_target_full_name,
                   c_target.sign AS c_target_sign,
                   er.rate AS rate
            FROM exchange_rate er
                     JOIN currency c_base ON er.base_currency_id = c_base.id
                     JOIN currency c_target ON er.target_currency_id = c_target.id
            """;

    private final static String FIND_BY_CURRENCY_CODES_SQL = FIND_ALL_SQL + """
            WHERE c_base_code = ? AND c_target_code = ?;
            """;

    private final static String SAVE_SQL = """
            INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?);
            """;

    private final static String UPDATE_SQL = """
            UPDATE exchange_rate
            SET rate = ?
            WHERE id = ?;
            """;

    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(getExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CURRENCY_CODES_SQL)) {
            ExchangeRate exchangeRate = null;
            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                exchangeRate = getExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeRate save(ExchangeRate exchangeRate) throws CustomException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, exchangeRate.getBaseCurrency().getId());
            statement.setInt(2, exchangeRate.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                exchangeRate.setId(keys.getInt(1));
            }
            return exchangeRate;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new CustomException("Exchange rate already exists", e);
            }
            throw new RuntimeException(e);
        }
    }

    public ExchangeRate update(ExchangeRate updatedExchangeRate) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBigDecimal(1, updatedExchangeRate.getRate());
            statement.setInt(2, updatedExchangeRate.getId());
            statement.executeUpdate();
            return updatedExchangeRate;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        ExchangeRate exchangeRate;
        Currency baseCurrency = new Currency(
                resultSet.getInt("c_base_id"),
                resultSet.getString("c_base_code"),
                resultSet.getString("c_base_full_name"),
                resultSet.getString("c_base_sign")
        );
        Currency targetCurrency = new Currency(
                resultSet.getInt("c_target_id"),
                resultSet.getString("c_target_code"),
                resultSet.getString("c_target_full_name"),
                resultSet.getString("c_target_sign")
        );
        exchangeRate = (
                new ExchangeRate(
                        resultSet.getInt("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getBigDecimal("rate")
                ));
        return exchangeRate;
    }

    public static ExchangeRateRepository getInstance() {
        return INSTANCE;
    }
}
