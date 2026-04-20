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
import java.util.Map;
import java.util.LinkedHashMap;

public class CustomerPanel extends JPanel {
    private InventoryService inventoryService;
    private TransactionService transactionService;
    private AuthService authService;

    private DefaultTableModel productTableModel;
    private JTable productTable;
    private DefaultTableModel orderTableModel;
    private JTable orderTable;
    
    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    private JTextField txtSearch, txtQuantity;
    private JLabel lblCartTotal;
    
    private class CartItem {
        Product product;
        int quantity;
        public CartItem(Product p, int q) { this.product = p; this.quantity = q; }
    }
    private Map<Integer, CartItem> cart = new LinkedHashMap<>();

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

        JButton btnLogout = new JButton("Logout");
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "background: #ef4444; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,15,5,15;");
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> parent.logout());
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "showTabSeparators: true; tabHeight: 40; font: bold;");
        
        tabbedPane.addTab("Product Catalog", createCatalogTab());
        tabbedPane.addTab("My Cart", createCartTab());
        tabbedPane.addTab("Order History", createOrderTab());
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) refreshCartView();
            else if (tabbedPane.getSelectedIndex() == 2) loadOrderTable();
        });

        add(tabbedPane, BorderLayout.CENTER);

        refreshData();
    }
    
    private JPanel createCatalogTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
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
        panel.add(searchPanel, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Available Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(35);
        productTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false; selectionBackground: #e0e7ff; selectionForeground: #3730a3;");
        productTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold; background: #f9fafb;");
        
        JScrollPane scroll = new JScrollPane(productTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        
        actionPanel.add(new JLabel("Qty:"));
        txtQuantity = new JTextField("1", 5);
        txtQuantity.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10; textAlignment: center; font: bold;");
        actionPanel.add(txtQuantity);
        
        JButton btnAddCart = new JButton("Add to Cart");
        btnAddCart.putClientProperty(FlatClientProperties.STYLE, "background: #4f46e5; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,20,5,20;");
        btnAddCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddCart.addActionListener(e -> addToCart());
        actionPanel.add(btnAddCart);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createCartTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        cartTableModel = new DefaultTableModel(new String[]{"ID", "Product", "Unit Price", "Quantity", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(35);
        cartTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false; selectionBackground: #ffe4e6; selectionForeground: #9f1239;");
        cartTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold; background: #f9fafb;");
        
        JScrollPane scroll = new JScrollPane(cartTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton btnRemove = new JButton("Remove Selected");
        btnRemove.putClientProperty(FlatClientProperties.STYLE, "background: #ef4444; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,15,5,15;");
        btnRemove.addActionListener(e -> removeSelectedCartItem());
        bottomPanel.add(btnRemove, BorderLayout.WEST);
        
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        checkoutPanel.setBackground(Color.WHITE);
        
        lblCartTotal = new JLabel("Total: $0.00");
        lblCartTotal.putClientProperty(FlatClientProperties.STYLE, "font: bold +4; foreground: #111827;");
        checkoutPanel.add(lblCartTotal);
        
        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.putClientProperty(FlatClientProperties.STYLE, "background: #10b981; foreground: #ffffff; font: bold +2; borderWidth: 0; padding: 10,25,10,25;");
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.addActionListener(e -> checkout());
        checkoutPanel.add(btnCheckout);
        
        bottomPanel.add(checkoutPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOrderTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        orderTableModel = new DefaultTableModel(new String[]{"Order ID", "Date", "Total Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(35);
        orderTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false; selectionBackground: #e0e7ff;");
        orderTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold; background: #f9fafb;");
        
        JScrollPane scroll = new JScrollPane(orderTable);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void search() {
        String keyword = txtSearch.getText();
        List<Product> list = inventoryService.searchProducts(keyword);
        loadProductTable(list);
    }

    private void refreshData() {
        loadProductTable(inventoryService.getAllProducts());
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
    
    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product from the catalog.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(productTableModel.getValueAt(row, 0).toString());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            
            if (quantity <= 0) {
                showErrorValidation("Quantity must be greater than 0.");
                return;
            }
            
            int availableStock = Integer.parseInt(productTableModel.getValueAt(row, 4).toString());
            String name = productTableModel.getValueAt(row, 1).toString();
            String cat = productTableModel.getValueAt(row, 2).toString();
            double price = Double.parseDouble(productTableModel.getValueAt(row, 3).toString());
            
            int currentCartQty = cart.containsKey(productId) ? cart.get(productId).quantity : 0;
            if (currentCartQty + quantity > availableStock) {
                showErrorValidation("Insufficient stock! You can only add up to " + (availableStock - currentCartQty) + " more of this item.");
                return;
            }
            
            cart.put(productId, new CartItem(new Product(productId, name, cat, price, availableStock), currentCartQty + quantity));
            
            JOptionPane.showMessageDialog(this, "Added to cart successfully!", "Cart updated", JOptionPane.INFORMATION_MESSAGE);
            txtQuantity.putClientProperty("JComponent.outline", null);
            txtQuantity.setText("1");
        } catch (NumberFormatException e) {
            showErrorValidation("Quantity must be a valid number.");
        }
    }
    
    private void refreshCartView() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
            CartItem ci = entry.getValue();
            double subtotal = ci.product.getPrice() * ci.quantity;
            total += subtotal;
            cartTableModel.addRow(new Object[]{ci.product.getId(), ci.product.getName(), String.format("$%.2f", ci.product.getPrice()), ci.quantity, String.format("$%.2f", subtotal)});
        }
        lblCartTotal.setText(String.format("Total: $%.2f", total));
    }
    
    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(cartTableModel.getValueAt(row, 0).toString());
        cart.remove(id);
        refreshCartView();
    }
    
    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Map<Integer, Integer> tempCart = new LinkedHashMap<>();
            cart.forEach((id, item) -> tempCart.put(id, item.quantity));
            
            transactionService.checkoutCart(authService.getCurrentUser().getId(), tempCart);
            JOptionPane.showMessageDialog(this, "Checkout successful! Thank you for your purchase.", "Order Placed", JOptionPane.INFORMATION_MESSAGE);
            
            cart.clear();
            refreshCartView();
            refreshData(); // updates catalog stock
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Checkout Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            refreshData(); // resync catalog in case of stock errors
        }
    }

    private void showErrorValidation(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
        txtQuantity.putClientProperty("JComponent.outline", "error");
    }
}
