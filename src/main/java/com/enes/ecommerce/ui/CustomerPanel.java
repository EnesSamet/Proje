package com.enes.ecommerce.ui;

import com.enes.ecommerce.model.Order;
import com.enes.ecommerce.model.Product;
import com.enes.ecommerce.service.AuthService;
import com.enes.ecommerce.service.InventoryService;
import com.enes.ecommerce.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private InventoryService inventoryService;
    private TransactionService transactionService;
    private AuthService authService;

    private DefaultTableModel productTableModel;
    private JTable productTable;
    private DefaultTableModel orderTableModel;
    private JTable orderTable;

    private JTextField txtSearch, txtQuantity;
    private JButton btnBuy, btnLogout;

    public CustomerPanel(MainFrame parent, AuthService authService) {
        this.authService = authService;
        this.inventoryService = new InventoryService();
        this.transactionService = new TransactionService();
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblWelcome = new JLabel("Hoşgeldiniz, " + authService.getCurrentUser().getUsername() + " (Müşteri)");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(lblWelcome, BorderLayout.WEST);

        btnLogout = new JButton("Çıkış Yap");
        btnLogout.addActionListener(e -> parent.logout());
        JPanel pnlLogout = new JPanel();
        pnlLogout.add(btnLogout);
        headerPanel.add(pnlLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Split Pane (Products Top, Orders Bottom)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Top: Products
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Ürün Kataloğu"));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Ürün Ara: "));
        txtSearch = new JTextField(20);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        searchPanel.add(txtSearch);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new String[]{"ID", "İsim", "Kategori", "Fiyat", "Mevcut Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        topPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buyPanel = new JPanel(new FlowLayout());
        buyPanel.add(new JLabel("Adet:"));
        txtQuantity = new JTextField("1", 5);
        buyPanel.add(txtQuantity);
        btnBuy = new JButton("Seçili Ürünü Satın Al");
        btnBuy.addActionListener(e -> purchase());
        buyPanel.add(btnBuy);
        topPanel.add(buyPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(topPanel);

        // Bottom: Orders
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Sipariş Geçmişim"));
        orderTableModel = new DefaultTableModel(new String[]{"Sipariş ID", "Tarih", "Toplam Tutar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        bottomPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        refreshData();
    }

    private void search() {
        String keyword = txtSearch.getText();
        List<Product> list = inventoryService.searchProducts(keyword);
        loadProductTable(list);
    }

    private void refreshData() {
        loadProductTable(inventoryService.getAllProducts());
        loadOrderTable();
    }

    private void loadProductTable(List<Product> list) {
        productTableModel.setRowCount(0);
        for (Product p : list) {
            productTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStock()});
        }
    }

    private void loadOrderTable() {
        orderTableModel.setRowCount(0);
        List<Order> list = transactionService.getOrdersByUserId(authService.getCurrentUser().getId());
        for (Order o : list) {
            orderTableModel.addRow(new Object[]{o.getId(), o.getOrderDate(), o.getTotalAmount()});
        }
    }

    private void purchase() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Satın almak için bir ürün seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(productTableModel.getValueAt(row, 0).toString());
            int quantity = Integer.parseInt(txtQuantity.getText());
            transactionService.purchaseProduct(authService.getCurrentUser().getId(), productId, quantity);
            
            JOptionPane.showMessageDialog(this, "Satın alma işlemi başarıyla tamamlandı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            refreshData(); // Updates stock and puts new order on screen
            txtQuantity.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Adet sayısal bir değer olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}
