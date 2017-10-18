package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.AttributeRepository;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.attribute.Option;
import bitone.akeneo.product_generator.domain.model.attribute.Properties;

public class DbAttributeRepository implements AttributeRepository {

    private HashMap<String, Attribute> attributes;

    public Attribute get(String code) {
        return attributes.get(code);
    }

    public int count() {
        return attributes.size();
    }

    public Attribute[] all() {
        return (Attribute[]) attributes.values().toArray();
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Statement stmt;

        attributes = new HashMap<String, Attribute>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT a.id, a.code, a.is_scopable, a.is_localizable, a.attribute_type, GROUP_CONCAT(ao.code) as options, "
            + "a.is_unique, a.metric_family, a.default_metric_unit, a.number_min, a.number_max, a.date_min, a.date_max "
            + "FROM pim_catalog_attribute a "
            + "LEFT JOIN pim_catalog_attribute_option ao ON ao.attribute_id = a.id "
            + "GROUP by a.id, a.code, a.is_scopable, a.is_localizable, a.attribute_type"
        );

        while(rs.next()){
            int id  = rs.getInt("a.id");
            String code = rs.getString("a.code");
            boolean scopable = rs.getBoolean("a.is_scopable");
            boolean localizable = rs.getBoolean("a.is_localizable");
            String type = rs.getString("a.attribute_type");
            boolean unique = rs.getBoolean("a.is_unique");
            String metricFamily = rs.getString("a.metric_family");
            String defaultMetricUnit = rs.getString("a.default_metric_unit");
            BigDecimal numberMin = rs.getBigDecimal("a.number_min");
            BigDecimal numberMax = rs.getBigDecimal("a.number_max");
            Date dateMin = rs.getDate("a.date_min");
            Date dateMax = rs.getDate("a.date_max");

            String rawOptionCodes = rs.getString("options");
            Attribute attribute = null;

            Properties properties = new Properties(unique, metricFamily, defaultMetricUnit, numberMin, numberMax, dateMin, dateMax);

            if (rawOptionCodes != null) {
                String[] optionCodes = rawOptionCodes.split(",");
                ArrayList<Option> options = new ArrayList<Option>();

                for(String optionCode : optionCodes) {
                    options.add(new Option(optionCode));
                }

                attribute = new Attribute(id, code, type, localizable, scopable, properties, options.toArray(new Option[options.size()]), null);
            } else {
                attribute = new Attribute(id, code, type, localizable, scopable, properties, null, null);
            }
            attributes.put(code, attribute);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
