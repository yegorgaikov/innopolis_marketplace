package root.dao;

import root.connectionManager.ConnectionManagerJdbcImpl;
import root.pojo.Client;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcServiceDaoImpl implements JdbcServiceDao {

    @Override
    public Client addClient(Client newClient, String login, String password) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO clients values (DEFAULT, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);
            Savepoint addClient = connection.setSavepoint("add client");

            preparedStatement.setString(1, newClient.getFirstName());
            preparedStatement.setString(2, newClient.getLastName());
            preparedStatement.setString(3, newClient.getPhoneNumber());
            preparedStatement.setString(4, newClient.getAddress());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newClient.setClientId(generatedKeys.getBigDecimal(1));
                    if (registrationUser(login, password, newClient.getClientId())) {
                        connection.commit();
                        connection.setAutoCommit(true);
                        return newClient;
                    } else {
                        connection.rollback(addClient);
                        connection.setAutoCommit(true);
                        return new Client();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Client();
    }

    private boolean registrationUser(String login, String password, BigDecimal clientId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO users values (DEFAULT, ?, ?, ?)")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setBigDecimal(3, clientId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteClient(BigDecimal clientId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM clients where clientId = (?)")) {

            preparedStatement.setBigDecimal(1, clientId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Client getClientById(BigDecimal clientId) {
        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM clients where clientId = (?)")) {
            preparedStatement.setLong(1, clientId.longValue());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return initClient(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Client();
    }

    private Client initClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setClientId(resultSet.getBigDecimal("clientId"));
        client.setFirstName(resultSet.getString("firstName"));
        client.setLastName(resultSet.getString("lastName"));
        client.setAddress(resultSet.getString("address"));
        client.setPhoneNumber(resultSet.getString("phoneNumber"));

        return client;
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        Client client;

        try (Connection connection = ConnectionManagerJdbcImpl.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM clients")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    client = initClient(resultSet);
                    clients.add(client);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
