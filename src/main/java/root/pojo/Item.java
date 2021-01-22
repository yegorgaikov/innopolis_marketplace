package root.pojo;

import java.math.BigDecimal;

public class Item {

    private BigDecimal itemId;
    private String itemName;
    private String description;
    private BigDecimal cost;

    public Item() {
    }

    public Item(BigDecimal itemId, String itemName, String description, BigDecimal cost) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.cost = cost;
    }

    public Item(String itemName, String description, BigDecimal cost) {
        this.itemName = itemName;
        this.description = description;
        this.cost = cost;
    }

    public BigDecimal getItemId() {
        return itemId;
    }

    public void setItemId(BigDecimal itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                '}';
    }
}
