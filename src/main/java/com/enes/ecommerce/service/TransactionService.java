package com.enes.ecommerce.service;

import com.enes.ecommerce.data.DatabaseConnection;
import com.enes.ecommerce.data.OrderDao;
import com.enes.ecommerce.data.OrderItemDao;
import com.enes.ecommerce.model.Order;
import com.enes.ecommerce.model.OrderItem;
import com.enes.ecommerce.model.Product;

import java.sql.Connection;
import java.util.List;

public class TransactionService {
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;

    public TransactionService() {
        this.orderDao = new OrderDao();
        this.orderItemDao = new OrderItemDao();
    }

    public void purchaseProduct(int userId, int productId, int quantity) throws Exception {
        if (quantity <= 0) throw new Exception("Miktar en az 1 olmalıdır.");

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Başlangıç: Transaction

            try {
                // Ürünü Getir
                Product product = null;
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Products WHERE id = ?")) {
                    pstmt.setInt(1, productId);
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            product = new Product(
                                rs.getInt("id"), rs.getString("name"), 
                                rs.getString("category"), rs.getDouble("price"), 
                                rs.getInt("stock")
                            );
                        }
                    }
                }

                if (product == null) throw new Exception("Ürün bulunamadı.");
                if (product.getStock() < quantity) throw new Exception("Yetersiz stok. Mevcut: " + product.getStock());

                // Stoğu Düşür
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement("UPDATE Products SET stock = stock - ? WHERE id = ?")) {
                    pstmt.setInt(1, quantity);
                    pstmt.setInt(2, productId);
                    pstmt.executeUpdate();
                }

                // Sipariş Ekle
                double amount = product.getPrice() * quantity;
                String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                int orderId = -1;
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Orders (user_id, order_date, total_amount) VALUES (?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, dateStr);
                    pstmt.setDouble(3, amount);
                    pstmt.executeUpdate();
                    try (java.sql.ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) orderId = keys.getInt(1);
                    }
                }

                // Sipariş Öğesini Ekle
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement("INSERT INTO OrderItems (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)")) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, productId);
                    pstmt.setInt(3, quantity);
                    pstmt.setDouble(4, product.getPrice());
                    pstmt.executeUpdate();
                }

                conn.commit(); // Başarılı: Commit
            } catch (Exception e) {
                conn.rollback(); // Hata durumu: Rollback
                throw new Exception("Satın alma başarısız: " + e.getMessage());
            }
        }
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderDao.getByUserId(userId);
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return orderItemDao.getByOrderId(orderId);
    }
}
