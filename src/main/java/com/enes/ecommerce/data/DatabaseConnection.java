package com.enes.ecommerce.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:ecommerce.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String usersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL" +
                ");";

        String productsTable = "CREATE TABLE IF NOT EXISTS Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "stock INTEGER NOT NULL" +
                ");";

        String ordersTable = "CREATE TABLE IF NOT EXISTS Orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "order_date TEXT NOT NULL," +
                "total_amount REAL NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES Users(id)" +
                ");";
                
        String orderItemsTable = "CREATE TABLE IF NOT EXISTS OrderItems (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER NOT NULL," +
                "product_id INTEGER NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "price REAL NOT NULL," +
                "FOREIGN KEY(order_id) REFERENCES Orders(id)," +
                "FOREIGN KEY(product_id) REFERENCES Products(id)" +
                ");";

        String defaultAdmin = "INSERT OR IGNORE INTO Users (username, password, role) VALUES ('admin', 'admin', 'ADMIN');";
        String defaultCustomer = "INSERT OR IGNORE INTO Users (username, password, role) VALUES ('customer', 'customer', 'CUSTOMER');";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(productsTable);
            stmt.execute(ordersTable);
            stmt.execute(orderItemsTable);
            
            // Initial data injection logic
            // Add default admin if not exist
            stmt.executeUpdate(defaultAdmin);
            stmt.executeUpdate(defaultCustomer);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}
