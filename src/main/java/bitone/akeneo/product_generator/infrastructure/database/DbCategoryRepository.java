package bitone.akeneo.product_generator.infrastructure.database;

import java.util.HashMap;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bitone.akeneo.product_generator.domain.model.CategoryRepository;
import bitone.akeneo.product_generator.domain.model.Category;

public class DbCategoryRepository implements CategoryRepository {

    private HashMap<String, Category> categories;
    private HashSet<Category> children;

    public Category get(String code) {
        return categories.get(code);
    }

    public int count() {
        return categories.size();
    }

    public Category[] all() {
        return (Category[]) categories.values().toArray(new Category[categories.size()]);
    }

    public int countChildren() {
        return children.size();
    }

    public Category[] allChildren() {
        return (Category[]) children.toArray(new Category[children.size()]);
    }

    public void initialize(String dbUrl) throws SQLException {
        Connection conn;
        Statement stmt;
        HashMap<Integer, String> idCodeMapping = new HashMap<Integer, String>();

        categories = new HashMap<String, Category>();
        children = new HashSet<Category>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }

        conn = DriverManager.getConnection(dbUrl);
        stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT id, code, parent_id FROM pim_catalog_category ORDER BY lvl ASC");

        while(rs.next()){
            int id  = rs.getInt("id");
            String code = rs.getString("code");
            Integer parentId = new Integer(rs.getInt("parent_id"));
            if (rs.wasNull()) {
                parentId = null;
            }

            idCodeMapping.put(id, code);

            Category parent = null;
            if (parentId != null) {
                String parentCode = idCodeMapping.get(parentId);
                parent = categories.get(parentCode);
            }
            Category category = new Category(id, code, parent);

            if (parent != null) {
                children.add(category);
            }
            categories.put(code, category);
        }

        rs.close();
        stmt.close();
        conn.close();
    }
}
