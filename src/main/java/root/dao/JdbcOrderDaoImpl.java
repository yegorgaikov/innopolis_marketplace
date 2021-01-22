package root.dao;

import root.connectionManager.ConnectionManagerJdbcImpl;
import root.pojo.Item;
import root.pojo.Order;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcOrderDaoImpl implements JdbcOrderDao {

    private final JdbcItemDao jdbcItemDao;

    public JdbcOrderDaoImpl(JdbcItemDao jdbcItemDao) {
        this.jdbcItemDao = jdbcItemDao;
    }

    /**
     * Применение батчинга и ручного управления транзакциями
     */
    @Override
    public Order addOrder(Order order) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement psOrders = connection.prepareStatement(
                     "INSERT INTO orders VALUES (DEFAULT, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psOrderItems = connection.prepareStatement(
                     "INSERT INTO orderItems VALUES(DEFAULT, ?, ?)")) {

            connection.setAutoCommit(false);
            Savepoint addOrder = connection.setSavepoint("addOrder");

            psOrders.setInt(1, order.getItems().size());
            psOrders.setBigDecimal(2, summa(order.getItems()));
            psOrders.setBigDecimal(3, order.getClientId());

            psOrders.executeUpdate();
            try (ResultSet generatedKeys = psOrders.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getBigDecimal(1));
                }
            }

            try {
                for (Item item : order.getItems()) {
                    psOrderItems.setBigDecimal(1, order.getOrderId());
                    psOrderItems.setBigDecimal(2, item.getItemId());
                    psOrderItems.addBatch();
                }

                psOrderItems.executeBatch();
                connection.commit();

                return getOrderById(order.getOrderId());
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback(addOrder);
                connection.setAutoCommit(true);
            }

            connection.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Order();
    }

    private BigDecimal summa(List<Item> items) {
        BigDecimal sum = new BigDecimal("0");
        for (Item item : items) {
            sum = sum.add(item.getCost());
        }
        return sum;
    }

    @Override
    public boolean addItemToOrder(Item item, BigDecimal orderId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement psOrderItems = connection.prepareStatement(
                     "INSERT INTO orderItems VALUES(DEFAULT, ?, ?)")) {

            connection.setAutoCommit(false);

            psOrderItems.setBigDecimal(1, orderId);
            psOrderItems.setBigDecimal(2, item.getItemId());

            psOrderItems.execute();

            if (Objects.equals(jdbcItemDao.updateItem(item), null)) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new SQLException("The operation was cancelled");
            }

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Order updateOrder(Order order) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE orders SET countItems = (?), summa = (?), clientId = (?) where orderId = (?)")) {
            preparedStatement.setInt(1, order.getItems().size());
            preparedStatement.setBigDecimal(2, summa(order.getItems()));
            preparedStatement.setBigDecimal(3, order.getClientId());
            preparedStatement.setBigDecimal(4, order.getOrderId());
            preparedStatement.executeUpdate();

            return getOrderById(order.getOrderId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public boolean deleteOrder(BigDecimal orderId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement psOrders = connection.prepareStatement(
                     "DELETE FROM orders where orderId = (?)");
             PreparedStatement psOrderItems = connection.prepareStatement(
                     "DELETE FROM orderItems where orderId = (?)")) {

            connection.setAutoCommit(false);
            Savepoint delete = connection.setSavepoint("delete");

            try {
                psOrders.setBigDecimal(1, orderId);
                psOrders.executeUpdate();

                psOrderItems.setBigDecimal(1, orderId);
                psOrderItems.executeUpdate();
            } catch (SQLException e) {
                connection.rollback(delete);
                connection.setAutoCommit(true);
                throw new SQLException(e);
            }

            connection.commit();
            connection.setAutoCommit(true);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Order getOrderById(BigDecimal orderId) {
        Order order = new Order();
        try (CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
             Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM orders WHERE orderId = (?)")) {

            return initOrder(orderId, order, cachedRowSet, preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }


    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        try (CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
             Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection()) {
            cachedRowSet.setCommand("SELECT * FROM orders");
            cachedRowSet.execute(connection);

            return initOrderList(orders, cachedRowSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> getOrderByClientId(BigDecimal clientId) {
        List<Order> orders = new ArrayList<>();

        try (CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
             Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM orders WHERE clientId = (?)")) {

            preparedStatement.setBigDecimal(1, clientId);
            cachedRowSet.populate(preparedStatement.executeQuery());

            return initOrderList(orders, cachedRowSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean deleteItemFromOrder(BigDecimal orderId, BigDecimal itemId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM orderItems where orderId = (?) and itemId = (?)")) {

            preparedStatement.setBigDecimal(1, orderId);
            preparedStatement.setBigDecimal(2, itemId);
            preparedStatement.executeUpdate();

            Item changedItem = jdbcItemDao.getItemById(itemId);
            jdbcItemDao.updateItem(changedItem);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Order initOrder(BigDecimal clientId, Order order, CachedRowSet cachedRowSet,
                            PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setBigDecimal(1, clientId);
        cachedRowSet.populate(preparedStatement.executeQuery());

        if (cachedRowSet.next()) {
            order.setOrderId(cachedRowSet.getBigDecimal("orderId"));
            order.setCountItems(cachedRowSet.getInt("countItems"));
            order.setSumma(cachedRowSet.getBigDecimal("summa"));
            order.setClientId(cachedRowSet.getBigDecimal("clientId"));
            order.setItems(getItemsFromOrder(order.getOrderId()));
        }
        return order;
    }

    private List<Item> getItemsFromOrder(BigDecimal orderId) {

        List<Item> items = new ArrayList<>();
        Item item;

        try (CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
             Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM orderItems WHERE orderId = (?)")) {

            preparedStatement.setBigDecimal(1, orderId);
            cachedRowSet.populate(preparedStatement.executeQuery());

            while (cachedRowSet.next()) {
                item = jdbcItemDao.getItemById(cachedRowSet.getBigDecimal("itemId"));
                items.add(item);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return items;
    }

    private List<Order> initOrderList(List<Order> orders, CachedRowSet cachedRowSet) throws SQLException {
        Order order = new Order();
        while (cachedRowSet.next()) {
            order.setOrderId(cachedRowSet.getBigDecimal("orderId"));
            order.setCountItems(cachedRowSet.getInt("countItems"));
            order.setSumma(cachedRowSet.getBigDecimal("summa"));
            BigDecimal clientId = cachedRowSet.getBigDecimal("clientId") == null ?
                    new BigDecimal("0") : cachedRowSet.getBigDecimal("clientId");
            order.setClientId(clientId);
            order.setItems(getItemsFromOrder(order.getOrderId()));

            orders.add(order);
        }
        return orders;
    }
}
