package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import models.Student;

public class StatisticsGUI extends JFrame {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalStudentsLabel, avgMarksLabel, distinctionLabel, passRateLabel;
    private JProgressBar passRateBar;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    
    public StatisticsGUI() {
        setTitle("Statistics Dashboard - Student Management System");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Summary Cards Panel
        JPanel summaryPanel = createSummaryPanel();
        mainPanel.add(summaryPanel, BorderLayout.CENTER);
        
        // Statistics Table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load statistics
        loadStatistics();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Statistics Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setForeground(PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadStatistics());
        panel.add(refreshButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Total Students Card
        JPanel totalCard = createStatCard("Total Students", "0", SUCCESS_COLOR);
        totalStudentsLabel = (JLabel) ((JPanel) totalCard.getComponent(1)).getComponent(0);
        panel.add(totalCard);
        
        // Average Marks Card
        JPanel avgCard = createStatCard("Average Marks", "0%", PRIMARY_COLOR);
        avgMarksLabel = (JLabel) ((JPanel) avgCard.getComponent(1)).getComponent(0);
        panel.add(avgCard);
        
        // Distinction Students Card
        JPanel distinctionCard = createStatCard("Distinction (80+)", "0", WARNING_COLOR);
        distinctionLabel = (JLabel) ((JPanel) distinctionCard.getComponent(1)).getComponent(0);
        panel.add(distinctionCard);
        
        // Pass Rate Card
        JPanel passCard = createStatCard("Pass Rate", "0%", DANGER_COLOR);
        passRateLabel = (JLabel) ((JPanel) passCard.getComponent(1)).getComponent(0);
        panel.add(passCard);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setBackground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
        
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "Statistics by Faculty",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        
        String[] columns = {"Faculty", "Students", "Average Marks", "Distinction", "Pass Rate"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsTable.setRowHeight(30);
        statsTable.setSelectionBackground(new Color(52, 152, 219, 50));
        
        // Set column widths
        statsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        statsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        statsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        statsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        // Custom renderer for numeric columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        statsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        statsTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        statsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        statsTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setPreferredSize(new Dimension(850, 250));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadStatistics() {
        try {
            List<Object[]> stats = Student.getStatistics();
            
            // Clear table
            tableModel.setRowCount(0);
            
            int totalStudents = 0;
            double totalMarks = 0;
            int totalDistinction = 0;
            int totalPass = 0;
            
            // Add rows to table
            for (Object[] stat : stats) {
                tableModel.addRow(stat);
                
                totalStudents += (int) stat[1];
                totalMarks += (double) stat[2] * (int) stat[1];
                totalDistinction += (int) stat[3];
                totalPass += (int) stat[4];
            }
            
            // Update summary cards
            totalStudentsLabel.setText(String.valueOf(totalStudents));
            
            double avgMarks = totalStudents > 0 ? totalMarks / totalStudents : 0;
            avgMarksLabel.setText(String.format("%.1f%%", avgMarks));
            
            distinctionLabel.setText(String.valueOf(totalDistinction));
            
            int passRate = totalStudents > 0 ? (totalPass * 100) / totalStudents : 0;
            passRateLabel.setText(passRate + "%");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading statistics: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}