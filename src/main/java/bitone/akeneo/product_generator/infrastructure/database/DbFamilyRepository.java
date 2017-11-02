package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.FamilyRepository;
import bitone.akeneo.product_generator.domain.model.AttributeRepository;
import bitone.akeneo.product_generator.domain.model.ChannelRepository;
import bitone.akeneo.product_generator.domain.model.Family;
import bitone.akeneo.product_generator.domain.model.Attribute;

public class DbFamilyRepository implements FamilyRepository {

    private HashMap<String, Family> families;
    private AttributeRepository attributeRepository;
    private ChannelRepository channelRepository;

    public DbFamilyRepository(AttributeRepository attributeRepository, ChannelRepository channelRepository) {
        this.attributeRepository = attributeRepository;
        this.channelRepository = channelRepository;
    }

    public Family get(String code) {
        return families.get(code);
    }

    public int count() {
        return families.size();
    }

    public Family[] all() {
        return (Family[]) families.values().toArray(new Family[families.size()]);
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Connection attributesConn;
        Connection labelsConn;
        Statement stmt;
        Statement attributesStmt;
        Statement labelsStmt;

        families = new HashMap<String, Family>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        attributesConn = DriverManager.getConnection(dbUrl);
        labelsConn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        attributesStmt = attributesConn.createStatement();
        labelsStmt = labelsConn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT f.id, f.code FROM pim_catalog_family f "
            + "GROUP BY f.id, f.code"
        );

        while (rs.next()){
            int id  = rs.getInt("f.id");
            String code = rs.getString("f.code");
            ArrayList<Attribute> attributes = new ArrayList<Attribute>();
            HashMap<String, String> labels = new HashMap<String, String>();

            ResultSet rsAttributes = attributesStmt.executeQuery(
                "SELECT a.code FROM pim_catalog_attribute a "
                + "JOIN pim_catalog_family_attribute fa ON fa.attribute_id = a.id AND fa.family_id = " + id + " "
                + " GROUP BY a.code"
            );

            while (rsAttributes.next()) {
                attributes.add(attributeRepository.get(rsAttributes.getString("a.code")));
            }

            ResultSet rsLabels = labelsStmt.executeQuery(
                "SELECT t.locale, t.label FROM pim_catalog_family_translation t WHERE t.foreign_key =  " + id
            );

            while (rsLabels.next()) {
                labels.put(rsLabels.getString("t.locale"), rsLabels.getString("t.label"));
            }

            Family family = new Family(id, code, labels, attributes.toArray(new Attribute[attributes.size()]), null);
            families.put(code, family);
        }

        rs.close();
        stmt.close();
        attributesStmt.close();
        labelsStmt.close();
        conn.close();
        attributesConn.close();
        labelsConn.close();
    }
}
