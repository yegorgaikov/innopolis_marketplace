package root.dao;

import root.pojo.Item;
import root.pojo.Order;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pattern Facade
 */

public interface JdbcOrderDao {

    Order addOrder(Order order);

    Order updateOrder(Order order);

    boolean deleteOrder(BigDecimal orderId);

    Order getOrderById(BigDecimal orderId);

    List<Order> getAllOrders();

    List<Order> getOrderByClientId(BigDecimal clientId);

    boolean addItemToOrder(Item item, BigDecimal orderId);

    boolean deleteItemFromOrder(BigDecimal orderId, BigDecimal itemId);
}
