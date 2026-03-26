package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import database.DatabaseConnection;
import java.sql.*;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private JLabel messageLabel;
    private JProgressBar progressBar;
    private JButton loginButton, resetButton;
    
    // Custom colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color BUTTON_HOVER = new Color(46, 204, 113);
    
    public LoginGUI() {
        setTitle("Student Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 73, 94), 
                                                     getWidth(), getHeight(), new Color(44, 62, 80));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create title panel with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        // Create custom title label with shadow
        JLabel titleLabel = new JLabel("Student Management System") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.setFont(getFont());
                g2d.drawString(getText(), 3, 33);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), 0, 30);
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Create form panel with semi-transparent background
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field with icon
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("👤 Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.WHITE);
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        usernameField.setBackground(new Color(255, 255, 255, 220));
        formPanel.add(usernameField, gbc);
        
        // Password field with icon
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setBackground(new Color(255, 255, 255, 220));
        formPanel.add(passwordField, gbc);
        
        // Remember Me checkbox
        gbc.gridx = 1;
        gbc.gridy = 2;
        rememberMeCheckBox = new JCheckBox("Remember Me");
        rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheckBox.setForeground(Color.WHITE);
        rememberMeCheckBox.setOpaque(false);
        formPanel.add(rememberMeCheckBox, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        loginButton = createStyledButton("Login", PRIMARY_COLOR, BUTTON_HOVER);
        resetButton = createStyledButton("Reset", new Color(231, 76, 60), new Color(241, 86, 70));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        
        // Message label
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setOpaque(false);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setForeground(BUTTON_HOVER);
        progressBar.setBackground(new Color(255, 255, 255, 100));
        
        // Add components to main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load saved credentials
        loadSavedCredentials();
        
        // Add action listeners
        loginButton.addActionListener(e -> performLogin());
        resetButton.addActionListener(e -> resetFields());
        
        // Enter key to login
        getRootPane().setDefaultButton(loginButton);
        
        // Add hover effects
        addButtonHoverEffect(loginButton, PRIMARY_COLOR, BUTTON_HOVER);
        addButtonHoverEffect(resetButton, new Color(231, 76, 60), new Color(241, 86, 70));
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        button.setText(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
    
    private void addButtonHoverEffect(JButton button, Color normal, Color hover) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
                button.repaint();
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("❌ Please enter username and password!");
            return;
        }
        
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        messageLabel.setText("🔄 Authenticating...");
        
        Timer timer = new Timer(1500, e -> {
            if (username.equals("admin") && password.equals("admin")) {
                messageLabel.setText("✅ Login successful! Redirecting...");
                
                if (rememberMeCheckBox.isSelected()) {
                    saveCredentials(username, password);
                } else {
                    clearSavedCredentials();
                }
                
                new MainGUI().setVisible(true);
                dispose();
            } else {
                messageLabel.setText("❌ Invalid username or password!");
                progressBar.setVisible(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        rememberMeCheckBox.setSelected(false);
        messageLabel.setText(" ");
        usernameField.requestFocus();
    }
    
    private void saveCredentials(String username, String password) {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginGUI.class);
        prefs.put("username", username);
        prefs.put("password", password);
        prefs.putBoolean("remember", true);
    }
    
    private void loadSavedCredentials() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginGUI.class);
        boolean remember = prefs.getBoolean("remember", false);
        if (remember) {
            String username = prefs.get("username", "");
            String password = prefs.get("password", "");
            usernameField.setText(username);
            passwordField.setText(password);
            rememberMeCheckBox.setSelected(true);
        }
    }
    
    private void clearSavedCredentials() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginGUI.class);
        prefs.remove("username");
        prefs.remove("password");
        prefs.putBoolean("remember", false);
    }
}