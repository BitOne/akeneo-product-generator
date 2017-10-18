package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.LocaleRepository;
import bitone.akeneo.product_generator.domain.model.Locale;

public class DbLocaleRepository implements LocaleRepository {

    private HashMap<String, Locale> locales;

    public Locale get(String code) {
        return locales.get(code);
    }

    public int count() {
        return locales.size();
    }

    public Locale[] all() {
        return (Locale[]) locales.values().toArray(new Locale[locales.size()]);
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Statement stmt;

        locales = new HashMap<String, Locale>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT id, code FROM pim_catalog_locale WHERE is_activated = 1");

        while(rs.next()){
            int id  = rs.getInt("id");
            String code = rs.getString("code");

            Locale locale = new Locale(id, code);
            locales.put(code, locale);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
