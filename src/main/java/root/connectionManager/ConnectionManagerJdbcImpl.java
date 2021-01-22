package root.connectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Pattern Singleton
 */

public class ConnectionManagerJdbcImpl implements ConnectionManager {
    public static final ConnectionManager INSTANCE = new ConnectionManagerJdbcImpl();

    public ConnectionManagerJdbcImpl() {
    }

    public static ConnectionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/marketplaceDB",
                    "postgres",
                    "qwerty");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
