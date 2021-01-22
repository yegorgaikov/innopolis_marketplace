package root.pojo;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    private BigDecimal orderId;
    private int countItems;
    private BigDecimal summa;
    private BigDecimal clientId;
    private List<Item> items;

    public Order() {
    }

    public Order(BigDecimal orderId, int countItems, BigDecimal summa, BigDecimal clientId, List<Item> items) {
        this.orderId = orderId;
        this.countItems = countItems;
        this.summa = summa;
        this.clientId = clientId;
        this.items = items;
    }

    public BigDecimal getOrderId() {
        return orderId;
    }

    public void setOrderId(BigDecimal orderId) {
        this.orderId = orderId;
    }

    public int getCountItems() {
        return countItems;
    }

    public void setCountItems(int countItems) {
        this.countItems = countItems;
    }

    public BigDecimal getSumma() {
        return summa;
    }

    public void setSumma(BigDecimal summa) {
        this.summa = summa;
    }

    public BigDecimal getClientId() {
        return clientId;
    }

    public void setClientId(BigDecimal clientId) {
        this.clientId = clientId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", countItems=" + countItems +
                ", summa=" + summa +
                ", clientId=" + clientId +
                ", items=" + items +
                '}';
    }
}
