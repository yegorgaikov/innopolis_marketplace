package root;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

    public DBUtil() {
    }

    public static void newDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/marketplaceDB",
                "postgres",
                "qwerty");
             Statement statement = connection.createStatement()
        ) {
            statement.execute("-- Database: marketplaceDB\n"
                    + "DROP TABLE IF EXISTS clients;"
                    + "CREATE TABLE clients (\n"
                    + "clientId bigserial not null constraint clients_pk primary key,"
                    + "firstName varchar(100)  not null,"
                    + "lastName varchar(100) not null,"
                    + "phoneNumber varchar(12)  not null,"
                    + "address varchar(500));"
                    + "\n"
                    + "INSERT INTO clients (firstName, lastName, phoneNumber, address)\n"
                    + "VALUES ('Ivan', 'Ivanov', '+79991234567', 'Moscow'),\n"
                    + "   ('Sergei', 'Petrov', '+79997654321', 'Moscow');"
                    + "\n"
                    + "DROP TABLE IF EXISTS items;"
                    + "CREATE TABLE items (\n"
                    + "itemId bigserial not null constraint items_pk primary key,\n"
                    + "itemName varchar(100) not null,\n"
                    + "description varchar(2000)\n,"
                    + "itemCost numeric(15, 2) not null);"
                    + "\n"
                    + "INSERT INTO items (itemName, description, itemCost)\n"
                    + "VALUES\n"
                    + "   ('iPhone', 'iPhone 12, 64 Gb, white', 79900.00),\n"
                    + "   ('iPad', 'iPad2020, 32 Gb, grey, Wi-Fi', 29900.00),\n"
                    + "   ('Notebook', 'ASUS M570, 8 Gb, SSD 512 Gb ', 59900.00),\n"
                    + "   ('TV', 'Xioami MI TV 4S 43 inch', 30990.00);"
                    + "\n"
                    + "DROP TABLE IF EXISTS orders;"
                    + "CREATE TABLE orders (\n"
                    + "orderId bigserial not null constraint orders_pk primary key,\n"
                    + "countItems integer,\n"
                    + "summa numeric(15, 2) default 0.00 not null,\n"
                    + "clientId bigint not null);"
                    + "\n"
                    + "DROP TABLE IF EXISTS users;"
                    + "CREATE TABLE users (\n"
                    + "userId bigserial not null constraint users_pk primary key,\n"
                    + "login varchar(150) not null,\n"
                    + "password varchar(150) not null,\n"
                    + "clientId integer not null);"
                    + "\n"
                    + "DROP TABLE IF EXISTS orderitems;"
                    + "CREATE TABLE orderitems (\n"
                    + "id bigserial not null constraint orderitems_pk primary key,\n"
                    + "orderId bigint not null,\n"
                    + "itemId bigint not null);"
                    + "\n");

        }
    }
}
