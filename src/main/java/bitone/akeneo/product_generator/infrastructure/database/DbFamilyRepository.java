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
        Statement stmt;

        families = new HashMap<String, Family>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT f.id, f.code, GROUP_CONCAT(a.code) as attrs FROM pim_catalog_family f "
            + "LEFT JOIN pim_catalog_family_attribute fa ON fa.family_id = f.id "
            + "LEFT JOIN pim_catalog_attribute a ON a.id = fa.attribute_id "
            + "GROUP BY f.id, f.code"
        );

        while(rs.next()){
            int id  = rs.getInt("f.id");
            String code = rs.getString("f.code");
            String[] attributeCodes = rs.getString("attrs").split(",");

            ArrayList<Attribute> attributes = new ArrayList<Attribute>();

            for(String attributeCode : attributeCodes) {
                attributes.add(attributeRepository.get(attributeCode));
            }

            Family family = new Family(id, code, attributes.toArray(new Attribute[attributes.size()]), null);
            families.put(code, family);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
