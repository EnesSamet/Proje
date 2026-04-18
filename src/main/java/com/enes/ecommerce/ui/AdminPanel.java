package com.enes.ecommerce.ui;

import com.enes.ecommerce.model.Product;
import com.enes.ecommerce.service.AuthService;
import com.enes.ecommerce.service.InventoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private InventoryService inventoryService;
    private DefaultTableModel tableModel;
    private JTable productTable;

    private JTextField txtName, txtCategory, txtPrice, txtStock, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnLogout;

    public AdminPanel(MainFrame parent, AuthService authService) {
        this.inventoryService = new InventoryService();
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblWelcome = new JLabel("Hoşgeldiniz, " + authService.getCurrentUser().getUsername() + " (Admin)");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(lblWelcome, BorderLayout.WEST);

        btnLogout = new JButton("Çıkış Yap");
        btnLogout.addActionListener(e -> parent.logout());
        JPanel pnlLogout = new JPanel();
        pnlLogout.add(btnLogout);
        headerPanel.add(pnlLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Table and Search
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Ürün Ara: "));
        txtSearch = new JTextField(20);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        searchPanel.add(txtSearch);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "İsim", "Kategori", "Fiyat", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
        centerPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Form
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Ürün Yönetimi (CRUD)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx=0; gbc.gridy=0; bottomPanel.add(new JLabel("İsim:"), gbc);
        gbc.gridx=1; txtName = new JTextField(15); bottomPanel.add(txtName, gbc);

        gbc.gridx=2; bottomPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx=3; txtCategory = new JTextField(15); bottomPanel.add(txtCategory, gbc);

        gbc.gridx=0; gbc.gridy=1; bottomPanel.add(new JLabel("Fiyat:"), gbc);
        gbc.gridx=1; txtPrice = new JTextField(15); bottomPanel.add(txtPrice, gbc);

        gbc.gridx=2; bottomPanel.add(new JLabel("Stok:"), gbc);
        gbc.gridx=3; txtStock = new JTextField(15); bottomPanel.add(txtStock, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Ekle");
        btnUpdate = new JButton("Güncelle");
        btnDelete = new JButton("Sil");

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=4;
        bottomPanel.add(btnPanel, gbc);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();
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
            JOptionPane.showMessageDialog(this, "Ürün eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fiyat ve stok alanları sayısal olmalıdır.", "Geçersiz Girdi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek ürünü seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            inventoryService.updateProduct(id, txtName.getText(), txtCategory.getText(), price, stock);
            JOptionPane.showMessageDialog(this, "Ürün güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek ürünü seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bu ürünü silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
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
        productTable.clearSelection();
    }
}
