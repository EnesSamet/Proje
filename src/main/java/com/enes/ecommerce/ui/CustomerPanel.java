package com.enes.ecommerce.ui;

import com.enes.ecommerce.model.Order;
import com.enes.ecommerce.model.Product;
import com.enes.ecommerce.service.AuthService;
import com.enes.ecommerce.service.InventoryService;
import com.enes.ecommerce.service.TransactionService;
import com.formdev.flatlaf.FlatClientProperties;

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
        setBackground(new Color(249, 250, 251));

        // Modern Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,1,0,#e5e7eb;");

        JLabel lblWelcome = new JLabel("ShopHub Store");
        lblWelcome.putClientProperty(FlatClientProperties.STYLE, "font: bold +8; foreground: #111827;");
        JLabel lblSub = new JLabel("Welcome, " + authService.getCurrentUser().getUsername() + " (Customer)");
        lblSub.putClientProperty(FlatClientProperties.STYLE, "foreground: #6b7280;");
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblWelcome);
        titlePanel.add(lblSub);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "background: #ef4444; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,15,5,15;");
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> parent.logout());
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Split Pane (Products Top, Orders Bottom)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.putClientProperty(FlatClientProperties.STYLE, "dividerSize: 10; dividerFocusColor: #e5e7eb;");

        // Top: Products
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 16; border: 1,1,1,1,#e5e7eb;");
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Product Catalog", 0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(17, 24, 39)),
            BorderFactory.createEmptyBorder(5, 10, 10, 10)
        ));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(25);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search products...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "showClearButton: true; margin: 5,10,5,10; arc: 15;");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        searchPanel.add(txtSearch);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Available Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(35);
        productTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false; selectionBackground: #e0e7ff; selectionForeground: #3730a3;");
        productTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold; background: #f9fafb;");
        
        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createEmptyBorder());
        topPanel.add(productScroll, BorderLayout.CENTER);

        JPanel buyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buyPanel.setBackground(Color.WHITE);
        buyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        buyPanel.add(new JLabel("Qty:"));
        txtQuantity = new JTextField("1", 5);
        txtQuantity.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10; textAlignment: center; font: bold;");
        buyPanel.add(txtQuantity);
        
        btnBuy = new JButton("Buy Selected Product");
        btnBuy.putClientProperty(FlatClientProperties.STYLE, "background: #4f46e5; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,20,5,20;");
        btnBuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuy.addActionListener(e -> purchase());
        buyPanel.add(btnBuy);
        
        topPanel.add(buyPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(topPanel);

        // Bottom: Orders
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 16; border: 1,1,1,1,#e5e7eb;");
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "My Order History", 0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(17, 24, 39)),
            BorderFactory.createEmptyBorder(5, 10, 10, 10)
        ));
        
        orderTableModel = new DefaultTableModel(new String[]{"Order ID", "Date", "Total Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(35);
        orderTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false; selectionBackground: #e0e7ff; selectionForeground: #3730a3;");
        orderTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold; background: #f9fafb;");
        
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createEmptyBorder());
        bottomPanel.add(orderScroll, BorderLayout.CENTER);

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
            orderTableModel.addRow(new Object[]{o.getId(), o.getOrderDate(), String.format("$%.2f", o.getTotalAmount())});
        }
    }

    private void purchase() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product from the catalog to buy.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(productTableModel.getValueAt(row, 0).toString());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            
            if (quantity <= 0) {
                showErrorValidation("Quantity must be greater than 0.");
                return;
            }
            
            transactionService.purchaseProduct(authService.getCurrentUser().getId(), productId, quantity);
            
            JOptionPane.showMessageDialog(this, "Purchase successful! Thank you.", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtQuantity.putClientProperty("JComponent.outline", null);
            refreshData(); 
            txtQuantity.setText("1");
        } catch (NumberFormatException e) {
            showErrorValidation("Quantity must be a valid number.");
        } catch (Exception e) {
            showErrorValidation(e.getMessage());
        }
    }
    
    private void showErrorValidation(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
        txtQuantity.putClientProperty("JComponent.outline", "error");
    }
}
