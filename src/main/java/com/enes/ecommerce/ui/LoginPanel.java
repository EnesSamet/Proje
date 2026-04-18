package com.enes.ecommerce.ui;

import com.enes.ecommerce.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginPanel(MainFrame parent, AuthService authService) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel box = new JPanel(new GridLayout(3, 2, 10, 10));
        box.setBorder(BorderFactory.createTitledBorder("Kullanıcı Girişi"));

        box.add(new JLabel("Kullanıcı Adı:"));
        txtUsername = new JTextField(15);
        box.add(txtUsername);

        box.add(new JLabel("Şifre:"));
        txtPassword = new JPasswordField(15);
        box.add(txtPassword);

        box.add(new JLabel("")); // empty cell
        btnLogin = new JButton("Giriş Yap");
        btnLogin.addActionListener((ActionEvent e) -> {
            try {
                if (authService.login(txtUsername.getText(), new String(txtPassword.getPassword()))) {
                    JOptionPane.showMessageDialog(this, "Giriş Başarılı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    // Clear inputs for next time
                    txtUsername.setText("");
                    txtPassword.setText("");
                    parent.loginSuccess(authService.getCurrentUser());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
        box.add(btnLogin);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(box, gbc);
    }
}
