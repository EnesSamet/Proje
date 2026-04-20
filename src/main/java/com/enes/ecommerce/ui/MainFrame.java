package com.enes.ecommerce.ui;

import com.enes.ecommerce.data.DatabaseConnection;
import com.enes.ecommerce.model.User;
import com.enes.ecommerce.service.AuthService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private AuthService authService;

    public MainFrame() {
        setTitle("E-Commerce Automation System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Data
        DatabaseConnection.initializeDatabase();
        authService = new AuthService();

        // Layout setup
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Load Panels
        LoginPanel loginPanel = new LoginPanel(this, authService);
        mainPanel.add(loginPanel, "Login");

        add(mainPanel);
        
        // Show default
        showPanel("Login");
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }
    
    public void loginSuccess(User user) {
        if ("ADMIN".equals(user.getRole())) {
            AdminPanel adminPanel = new AdminPanel(this, authService);
            mainPanel.add(adminPanel, "Admin");
            showPanel("Admin");
        } else {
            CustomerPanel customerPanel = new CustomerPanel(this, authService);
            mainPanel.add(customerPanel, "Customer");
            showPanel("Customer");
        }
    }

    public void logout() {
        authService.logout();
        showPanel("Login");
    }

    public static void main(String[] args) {
        try {
            // Global FlatLaf UI settings
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("Table.alternateRowColor", new Color(245, 245, 250));
            // Set modern light look and feel
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
