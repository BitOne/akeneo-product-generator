package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.CurrencyRepository;
import bitone.akeneo.product_generator.domain.model.Currency;

public class DbCurrencyRepository implements CurrencyRepository {

    private HashMap<String, Currency> currencies;

    public Currency get(String code) {
        return currencies.get(code);
    }

    public int count() {
        return currencies.size();
    }

    public Currency[] all() {
        return (Currency[]) currencies.values().toArray(new Currency[currencies.size()]);
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Statement stmt;

        currencies = new HashMap<String, Currency>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT id, code FROM pim_catalog_currency WHERE is_activated = 1");

        while(rs.next()){
            int id  = rs.getInt("id");
            String code = rs.getString("code");

            Currency currency = new Currency(id, code);
            currencies.put(code, currency);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
