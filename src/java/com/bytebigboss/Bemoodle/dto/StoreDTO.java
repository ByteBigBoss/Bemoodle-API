package com.bytebigboss.Bemoodle.dto;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class StoreDTO {
    
    private String name;
    private String description;
    private int categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
