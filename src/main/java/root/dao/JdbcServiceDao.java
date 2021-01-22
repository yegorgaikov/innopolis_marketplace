package root.dao;

import root.pojo.Client;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pattern Facade
 */

public interface JdbcServiceDao {

    Client addClient(Client newClient, String login, String password);

    boolean deleteClient(BigDecimal clientId);

    Client getClientById(BigDecimal clientId);

    List<Client> getAllClients();
}
