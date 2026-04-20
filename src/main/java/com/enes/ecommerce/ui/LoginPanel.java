package com.enes.ecommerce.ui;

import com.enes.ecommerce.service.AuthService;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    public LoginPanel(MainFrame parent, AuthService authService) {
        setLayout(new GridBagLayout());
        setBackground(new Color(249, 250, 251)); // Tailwind gray-50 background

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        // Add subtle shadow using FlatLaf
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16; border: 1,1,1,1,#e5e7eb;");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Title and Subtitle
        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +10; foreground: #111827;");
        JLabel lblSubtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        lblSubtitle.putClientProperty(FlatClientProperties.STYLE, "foreground: #6b7280;");
        
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0); card.add(lblTitle, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 25, 0); card.add(lblSubtitle, gbc);

        // Error message placeholder
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.putClientProperty(FlatClientProperties.STYLE, "foreground: #dc2626; font: bold -1;"); // Tailwind red-600
        lblError.setVisible(false);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 10, 0); card.add(lblError, gbc);

        // Username
        txtUsername = new JTextField(15);
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10;");
        JLabel lblUserTitle = new JLabel("Username");
        lblUserTitle.putClientProperty(FlatClientProperties.STYLE, "font: semibold;");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 5, 0); card.add(lblUserTitle, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 15, 0); card.add(txtUsername, gbc);

        // Password
        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "margin: 5,10,5,10; showRevealButton: true;");
        JLabel lblPassTitle = new JLabel("Password");
        lblPassTitle.putClientProperty(FlatClientProperties.STYLE, "font: semibold;");
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 5, 0); card.add(lblPassTitle, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 25, 0); card.add(txtPassword, gbc);

        // Login Button
        btnLogin = new JButton("Sign In");
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "background: #4f46e5; foreground: #ffffff; font: bold +1; margin: 8,0,8,0; borderWidth: 0; focusWidth: 0;");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogin.addActionListener(e -> {
            lblError.setVisible(false);
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            
            // Input Validation
            if(user.isEmpty() || pass.isEmpty()){
                showError("Username and password cannot be empty!");
                txtUsername.putClientProperty("JComponent.outline", "error");
                txtPassword.putClientProperty("JComponent.outline", "error");
                return;
            } else {
                txtUsername.putClientProperty("JComponent.outline", null);
                txtPassword.putClientProperty("JComponent.outline", null);
            }

            try {
                if (authService.login(user, pass)) {
                    txtUsername.setText("");
                    txtPassword.setText("");
                    parent.loginSuccess(authService.getCurrentUser());
                } else {
                    showError("Invalid credentials. Try admin/admin or customer/customer");
                    txtUsername.putClientProperty("JComponent.outline", "error");
                    txtPassword.putClientProperty("JComponent.outline", "error");
                }
            } catch (Exception ex) {
                showError("System error: " + ex.getMessage());
            }
        });
        gbc.gridy = 7; card.add(btnLogin, gbc);

        add(card);
    }
    
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        revalidate(); // trigger visual update
    }
}
