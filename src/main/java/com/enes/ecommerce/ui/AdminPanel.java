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

public class AdminPanel extends JPanel {
    private InventoryService inventoryService;
    private TransactionService transactionService;
    private DefaultTableModel tableModel;
    private JTable productTable;

    private JTextField txtName, txtCategory, txtPrice, txtStock, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnLogout;
    
    // Report components
    private JLabel lblTotalProducts;
    private JLabel lblTotalRevenue;
    private JLabel lblLowStock;

    public AdminPanel(MainFrame parent, AuthService authService) {
        this.inventoryService = new InventoryService();
        this.transactionService = new TransactionService();
        setLayout(new BorderLayout());
        setBackground(new Color(249, 250, 251));

        // Modern Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,1,0,#e5e7eb;");

        JLabel lblWelcome = new JLabel("Admin Dashboard");
        lblWelcome.putClientProperty(FlatClientProperties.STYLE, "font: bold +8; foreground: #111827;");
        JLabel lblSub = new JLabel("Manage your product inventory (" + authService.getCurrentUser().getUsername() + ")");
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

        // Tabbed Pane for Operations and Reports
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "showTabSeparators: true; tabHeight: 40; font: bold;");
        
        JPanel inventoryTab = createInventoryTab();
        tabbedPane.addTab("Inventory Management", inventoryTab);
        
        JPanel reportsTab = createReportsTab();
        tabbedPane.addTab("Reports & Statistics", reportsTab);
        
        // Tab transition listener to refresh reports
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                refreshReports();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel createInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(249, 250, 251));

        // Center Table and Search Map
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 16; border: 1,1,1,1,#e5e7eb;");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        txtSearch = new JTextField(25);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search products...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "showClearButton: true; margin: 5,10,5,10; arc: 15;");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        searchPanel.add(txtSearch);
        centerContainer.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(35);
        productTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false; selectionBackground: #e0e7ff; selectionForeground: #3730a3;");
        productTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold; background: #f9fafb;");
        
        productTable.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerContainer, BorderLayout.CENTER);

        // Bottom Form for CRUD
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 16; border: 1,1,1,1,#e5e7eb;");
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Add / Update Product", 0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(17, 24, 39)),
            BorderFactory.createEmptyBorder(10, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; formContainer.add(new JLabel("Product Name:"), gbc);
        gbc.gridx=1; txtName = new JTextField(15); 
        txtName.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10;");
        formContainer.add(txtName, gbc);

        gbc.gridx=2; formContainer.add(new JLabel("Category:"), gbc);
        gbc.gridx=3; txtCategory = new JTextField(15); 
        txtCategory.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10;");
        formContainer.add(txtCategory, gbc);

        gbc.gridx=0; gbc.gridy=1; formContainer.add(new JLabel("Price ($):"), gbc);
        gbc.gridx=1; txtPrice = new JTextField(15); 
        txtPrice.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10;");
        formContainer.add(txtPrice, gbc);

        gbc.gridx=2; formContainer.add(new JLabel("Stock:"), gbc);
        gbc.gridx=3; txtStock = new JTextField(15); 
        txtStock.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10;");
        formContainer.add(txtStock, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        btnAdd = new JButton("Add Product");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "background: #10b981; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,15,5,15;");
        
        btnUpdate = new JButton("Update");
        btnUpdate.putClientProperty(FlatClientProperties.STYLE, "background: #3b82f6; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,15,5,15;");
        
        btnDelete = new JButton("Delete");
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "background: #ef4444; foreground: #ffffff; font: bold; borderWidth: 0; padding: 5,15,5,15;");

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=4; gbc.insets = new Insets(15, 10, 0, 10);
        formContainer.add(btnPanel, gbc);

        panel.add(formContainer, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(new Color(249, 250, 251));
        
        // Report Cards
        panel.add(createStatCard("Total Products in Catalog", lblTotalProducts = new JLabel("0")));
        panel.add(createStatCard("Total Revenue (Sales)", lblTotalRevenue = new JLabel("$0.00")));
        panel.add(createStatCard("Low Stock Alerts (<10)", lblLowStock = new JLabel("0")));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16; border: 1,1,1,1,#e5e7eb;");
        card.setPreferredSize(new Dimension(250, 100));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "foreground: #6b7280; font: bold;");
        valueLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +12; foreground: #4f46e5;");
        
        card.add(lblTitle);
        card.add(valueLabel);
        return card;
    }

    private void search() {
        String keyword = txtSearch.getText();
        List<Product> list = inventoryService.searchProducts(keyword);
        loadTable(list);
    }

    private void refreshTable() {
        loadTable(inventoryService.getAllProducts());
    }

    private void loadTable(List<Product> list) {
        tableModel.setRowCount(0);
        for (Product p : list) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStock()});
        }
    }
    
    private void refreshReports() {
        List<Product> products = inventoryService.getAllProducts();
        List<Order> orders = transactionService.getAllOrders();
        
        lblTotalProducts.setText(String.valueOf(products.size()));
        
        double revenue = 0;
        for (Order o : orders) revenue += o.getTotalAmount();
        lblTotalRevenue.setText(String.format("$%.2f", revenue));
        
        long lowStock = products.stream().filter(p -> p.getStock() < 10).count();
        lblLowStock.setText(String.valueOf(lowStock));
        if(lowStock > 0) lblLowStock.putClientProperty(FlatClientProperties.STYLE, "font: bold +12; foreground: #dc2626;");
        else lblLowStock.putClientProperty(FlatClientProperties.STYLE, "font: bold +12; foreground: #10b981;");
    }

    private void fillFormFromTable() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            txtCategory.setText(tableModel.getValueAt(row, 2).toString());
            txtPrice.setText(tableModel.getValueAt(row, 3).toString());
            txtStock.setText(tableModel.getValueAt(row, 4).toString());
        }
    }

    private void addProduct() {
        try {
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            inventoryService.addProduct(txtName.getText(), txtCategory.getText(), price, stock);
            JOptionPane.showMessageDialog(this, "Product added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (NumberFormatException e) {
            showErrorValidation("Both Price and Stock must be valid numbers.");
        } catch (Exception e) {
            showErrorValidation(e.getMessage());
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            inventoryService.updateProduct(id, txtName.getText(), txtCategory.getText(), price, stock);
            JOptionPane.showMessageDialog(this, "Product updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (NumberFormatException e) {
            showErrorValidation("Both Price and Stock must be valid numbers.");
        } catch (Exception e) {
            showErrorValidation(e.getMessage());
        }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            inventoryService.deleteProduct(id);
            refreshTable();
            clearForm();
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtPrice.putClientProperty("JComponent.outline", null);
        txtStock.putClientProperty("JComponent.outline", null);
        productTable.clearSelection();
    }
    
    private void showErrorValidation(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
        txtPrice.putClientProperty("JComponent.outline", "error");
        txtStock.putClientProperty("JComponent.outline", "error");
    }
}
