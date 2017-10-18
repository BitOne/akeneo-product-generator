package bitone.akeneo.product_generator.domain.model;

import java.util.ArrayList;

public class Category {

    private int id;
    private String code;
    private Category parent;
    private ArrayList<Category> children;

    public Category(int id, String code, Category parent) {
        this.id = id;
        this.code = code;
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public boolean isRoot() {
        return null == parent;
    }

    public void addChild(Category category) {
        children.add(category);
    }

    public Category[] getChildren() {
        return (Category[]) children.toArray();
    }

    public Category getParent() {
        return parent;
    }
}
