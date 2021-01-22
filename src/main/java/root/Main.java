package root;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import root.dao.*;
import root.pojo.Client;
import root.pojo.Item;
import root.pojo.Order;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) throws SQLException {
        JdbcItemDao jdbcItemDao = new JdbcItemDaoImpl();
        JdbcOrderDao jdbcOrderDao = new JdbcOrderDaoImpl(jdbcItemDao);
        JdbcServiceDao jdbcServiceDao = new JdbcServiceDaoImpl();

        DBUtil.newDatabase();
        serviceMethod(jdbcServiceDao);
        itemsMethod(jdbcItemDao);
        ordersMethod(jdbcOrderDao, jdbcItemDao, jdbcServiceDao);
    }

    public static void serviceMethod(JdbcServiceDao jdbcServiceDao) {
        Client newClient = new Client();
        newClient.setFirstName("Roman");
        newClient.setLastName("Privalov");
        newClient.setAddress("Saint-Petersburg");
        newClient.setPhoneNumber("+79771233215");

        Client client = jdbcServiceDao.addClient(newClient, "login", "password");
        LOGGER.info("Client client =" + client.toString());
        LOGGER.info("=========================================================================" + "\n");

        print(jdbcServiceDao.getAllClients());

        Client clientById = jdbcServiceDao.getClientById(new BigDecimal("1"));
        LOGGER.info("clientByIdTrue: " + clientById.toString());

        LOGGER.info("deleteClient: " + jdbcServiceDao.deleteClient(new BigDecimal("2")));
        LOGGER.info("=========================================================================" + "\n");
    }

    public static void itemsMethod(JdbcItemDao jdbcItemDao) {
        print(jdbcItemDao.getAllItems());

        LOGGER.info("getItemById: " + jdbcItemDao.getItemById(new BigDecimal(String.valueOf(2))));
        LOGGER.info("=========================================================================" + "\n");

        LOGGER.info("addItem: " + jdbcItemDao.addItem(new Item(
                "monitor",
                "ACER, 20 inch",
                new BigDecimal(String.valueOf(19900.00))
        )));
        LOGGER.info("=========================================================================" + "\n");

        LOGGER.info("updateItem: " + jdbcItemDao.updateItem(new Item(
                new BigDecimal(String.valueOf(2)),
                "monitor",
                "ACER, 32 inch",
                new BigDecimal(String.valueOf(29900.00))
        )));
        LOGGER.info("=========================================================================" + "\n");

        LOGGER.info("deleteItemById: " + jdbcItemDao.deleteItemById(new BigDecimal(String.valueOf(2))));
        LOGGER.info("=========================================================================" + "\n");

        print(jdbcItemDao.getAllItems());
    }

    public static void ordersMethod(JdbcOrderDao jdbcOrderDao, JdbcItemDao jdbcItemDao, JdbcServiceDao jdbcServiceDao) {

        Client client = jdbcServiceDao.getClientById(new BigDecimal("3"));

        List<Item> items = jdbcItemDao.getAllItems();

        Order order = new Order();
        order.setClientId(client.getClientId());
        order.setItems(items);

        LOGGER.info("addOrder: " + jdbcOrderDao.addOrder(order));
        LOGGER.info("=========================================================================" + "\n");

        print(jdbcOrderDao.getAllOrders());

        jdbcItemDao.addItem(new Item(
                "ReadBook",
                "Pocketbook 632, 300DPI, IPX7",
                new BigDecimal(String.valueOf(12900.00))
        ));

        jdbcOrderDao.addItemToOrder(jdbcItemDao.getItemById(new BigDecimal("3")), order.getOrderId());

        LOGGER.info("Order before adding new item:" + jdbcOrderDao.getOrderById(order.getOrderId()));
        LOGGER.info("=========================================================================" + "\n");

        boolean result = jdbcOrderDao.deleteItemFromOrder(order.getOrderId(), new BigDecimal("3"));
        LOGGER.info("deleteItemFromOrder = " + result + "    " + jdbcOrderDao.getOrderById(order.getOrderId()));
        LOGGER.info("=========================================================================" + "\n");

        LOGGER.info("Order by client id:" + jdbcOrderDao.getOrderByClientId(new BigDecimal("3")));
        LOGGER.info("=========================================================================" + "\n");

        LOGGER.info("deleteOrder:");
        jdbcOrderDao.deleteOrder(order.getOrderId());
        print(jdbcOrderDao.getAllOrders());
    }

    public static <T> void print(List<T> objects) {
        LOGGER.info("Show List: ");
        for (T t : objects) {
            LOGGER.info(t.toString());
        }
        LOGGER.info("=========================================================================" + "\n");
    }
}
