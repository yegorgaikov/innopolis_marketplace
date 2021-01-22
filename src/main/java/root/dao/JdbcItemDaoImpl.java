package root.dao;

import root.connectionManager.ConnectionManagerJdbcImpl;
import root.pojo.Item;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcItemDaoImpl implements JdbcItemDao {
    @Override
    public Item addItem(Item item) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO items values (DEFAULT, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, item.getItemName());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setBigDecimal(3, item.getCost());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return getItemById(generatedKeys.getBigDecimal(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Item();
    }

    @Override
    public Item updateItem(Item item) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE items SET itemName = (?), description = (?), itemCost = (?) where itemId = (?)")) {
            preparedStatement.setString(1, item.getItemName());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setBigDecimal(3, item.getCost());
            preparedStatement.setBigDecimal(4, item.getItemId());
            preparedStatement.executeUpdate();

            return getItemById(item.getItemId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Item();
    }

    @Override
    public boolean deleteItemById(BigDecimal itemId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM items where itemId = (?)")) {
            preparedStatement.setBigDecimal(1, itemId);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Item getItemById(BigDecimal itemId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM items where itemId = (?)")) {
            preparedStatement.setLong(1, itemId.longValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return initItem(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Item();
    }

    @Override
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        Item item;

        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM items")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    item = initItem(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private Item initItem(ResultSet resultSet) throws SQLException {
        Item item = new Item();

        item.setItemId(resultSet.getBigDecimal("itemId"));
        item.setItemName(resultSet.getString("itemName"));
        item.setDescription(resultSet.getString("description"));
        item.setCost(resultSet.getBigDecimal("itemCost"));

        return item;
    }
}
