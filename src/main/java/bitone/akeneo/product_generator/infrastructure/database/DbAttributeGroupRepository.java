package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.AttributeGroupRepository;
import bitone.akeneo.product_generator.domain.model.AttributeGroup;

public class DbAttributeGroupRepository implements AttributeGroupRepository {

    private HashMap<String, AttributeGroup> attributeGroups;

    public AttributeGroup get(String code) {
        return attributeGroups.get(code);
    }

    public int count() {
        return attributeGroups.size();
    }

    public AttributeGroup[] all() {
        return (AttributeGroup[]) attributeGroups.values().toArray();
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Statement stmt;

        attributeGroups = new HashMap<String, AttributeGroup>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();


        ResultSet rs = stmt.executeQuery("SELECT id, code FROM pim_catalog_attribute_group");

        while(rs.next()){
            int id  = rs.getInt("id");
            String code = rs.getString("code");

            AttributeGroup attributeGroup = new AttributeGroup(id, code);
            attributeGroups.put(code, attributeGroup);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
