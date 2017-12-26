package bitone.akeneo.product_generator.infrastructure.database;

import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.AttributeRepository;
import bitone.akeneo.product_generator.domain.model.Family;
import bitone.akeneo.product_generator.domain.model.FamilyRepository;
import bitone.akeneo.product_generator.domain.model.FamilyVariant;
import bitone.akeneo.product_generator.domain.model.FamilyVariantRepository;
import bitone.akeneo.product_generator.domain.model.family_variant.AttributeSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DbFamilyVariantRepository implements FamilyVariantRepository {

    private HashMap<String, Family> familyVariants;
    private AttributeRepository attributeRepository;

    public DbFamilyVariantRepository(FamilRepository familyRepository, AttributeRepository attributeRepository) {
        this.familyRepository = familyRepository;
        this.attributeRepository = attributeRepository;
    }

    public Family get(String code) {
        return familyVariant.get(code);
    }

    public int count() {
        return familyVariant.size();
    }

    public FamilyVariant[] all() {
        return (FamilyVariant[]) familyVariant.values().toArray(new FamilyVariant[familyVariants.size()]);
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Connection attributesConn;
        Connection attributeSetsConn;
        Statement stmt;
        Statement attributesStmt;
        Statement attributesSetsConn;

        families = new HashMap<String, Family>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(dbUrl);
        attributeSetsConn = DriverManager.getConnection(dbUrl);
        attributesConn = DriverManager.getConnection(dbUrl);
        labelsConn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        attributeSetsStmt = attributeSetsConn.createStatement();
        attributesStmt = attributesConn.createStatement();
        labelsStmt = labelsConn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT fv.id, fv.code, f.code FROM pim_catalog_family_variant fv "
            + " JOIN pim_catalog_family f ON f.id = fv.family_id "
            + "GROUP BY fv.id, fv.code, f.code"
        );

        while (rs.next()){
            int id  = rs.getInt("fv.id");
            String code = rs.getString("fv.code");
            String familyCode = rs.getString("f.code");

            ArrayList<AttributeSet> attributeSets = new ArrayList<AttributeSet>();

            ResultSet rsAttributeSets = attributeSetsStmt.executeQuery(
                "SELECT as.id, as.level FROM pim_catalog_family_variant_attribute_set as "
                + "JOIN pim_catalog_family_variant_has_variant_attribute_sets fas ON fas.variant_attribute_sets_id = fas.id AND fas.family_variant_id = " + id + " "
                + "GROUP BY as.id"
            );

            while (rsAttributeSets.next()) {
                ArrayList<Attribute> axes = new ArrayList<Attribute>();
                ArrayList<Attribute> attributes = new ArrayList<Attribute>();

                int attributeSetId = rsAttributeSets.getInt("as.id");
                int attributeSetLevel = rsAttributeSets.getInt("as.level");

                ResultSet rsAttributes = attributesStmt.executeQuery(
                    "SELECT a.code FROM pim_catalog_attribute a "
                    + "JOIN pim_catalog_variant_attribute_set_has_axes ax ON ax.axes_id = a.id and ax.variant_attribute_set_id = " + attributeSetId
                );

                while (rsAttributes.next()) {
                    axes.add(attributeRepository.get(rsAttributes.getString("a.code")));
                }

                ResultSet rsAttributes = attributesStmt.executeQuery(
                    "SELECT a.code FROM pim_catalog_attribute a "
                    + "JOIN pim_catalog_variant_attribute_set_has_attributes at ON at.attributes_id = a.id and ax.variant_attribute_set_id = " + attributeSetId
                );

                while (rsAttributes.next()) {
                    attributes.add(attributeRepository.get(rsAttributes.getString("a.code")));
                }

                attributeSets.add(new AttributeSet(attributeSetId, attributeSetLevel, axes.toArray(new Attribute[axes.size()]), attributes.toArray(new Attribute[attributes.size()])));
            }

            FamilyVariant = new FamilyVariant(id, code, labels, attributeAsLabel, attributes.toArray(new Attribute[attributes.size()]));
            familyVariants.put(code, familyVariant);
        }

        rs.close();
        stmt.close();
        attributeSetsStmt.close();
        attributesStmt.close();
        conn.close();
        attributeSetsConn.close();
        attributesConn.close();
    }
}
