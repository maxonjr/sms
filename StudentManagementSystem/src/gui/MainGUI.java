package gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;
import models.Student;
import utils.StringUtils;
import database.DatabaseConnection;

public class MainGUI extends JFrame {
    private JTextField nameField, emailField, marksField;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> facultyComboBox;
    private JComboBox<String> departmentComboBox;
    private JComboBox<Integer> yearComboBox;
    private JComboBox<Integer> semesterComboBox;
    private JButton addButton, updateButton, deleteButton, searchButton, showAllButton, clearButton, statisticsButton;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel, titleLabel, countLabel;
    private String currentStudentId;
    private JScrollPane mainScrollPane;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color INFO_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color PANEL_COLOR = Color.WHITE;

    private String[] courses = {
            "Computer Science", "Information Technology", "Software Engineering",
            "Data Science", "Cyber Security", "Artificial Intelligence", "Cloud Computing",
            "Business Administration", "Economics", "Finance", "Accounting",
            "Psychology", "Sociology", "English Literature", "History",
            "Medicine", "Nursing", "Public Health"
    };

    public MainGUI() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND_COLOR);
        createMenuBar();

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(BACKGROUND_COLOR);

        mainContentPanel.add(createHeaderPanel());
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainContentPanel.add(createInputPanel());
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainContentPanel.add(createTablePanel());

        mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setBackground(BACKGROUND_COLOR);

        add(mainScrollPane);

        loadAllStudents();
        loadFaculties();
        updateStatus("Ready", INFO_COLOR);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        titleLabel = new JLabel("Student Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        panel.add(titlePanel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        countLabel = new JLabel("Total Students: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        countLabel.setForeground(Color.WHITE);
        statsPanel.add(countLabel);

        panel.add(statsPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 480));

        JLabel formTitle = new JLabel("Student Information Form");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(PRIMARY_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // First row - Name and Email
        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 0));
        row1.setBackground(PANEL_COLOR);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel namePanel = new JPanel(new BorderLayout(8, 0));
        namePanel.setBackground(PANEL_COLOR);
        JLabel nameIcon = new JLabel("Name:");
        nameIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameIcon.setForeground(new Color(80, 80, 80));
        namePanel.add(nameIcon, BorderLayout.WEST);

        nameField = createStyledTextField();
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateInputs();
            }

            public void removeUpdate(DocumentEvent e) {
                validateInputs();
            }

            public void insertUpdate(DocumentEvent e) {
                validateInputs();
            }
        });
        namePanel.add(nameField, BorderLayout.CENTER);
        row1.add(namePanel);

        JPanel emailPanel = new JPanel(new BorderLayout(8, 0));
        emailPanel.setBackground(PANEL_COLOR);
        JLabel emailIcon = new JLabel("Email:");
        emailIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailIcon.setForeground(new Color(80, 80, 80));
        emailPanel.add(emailIcon, BorderLayout.WEST);

        emailField = createStyledTextField();
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateInputs();
            }

            public void removeUpdate(DocumentEvent e) {
                validateInputs();
            }

            public void insertUpdate(DocumentEvent e) {
                validateInputs();
            }
        });
        emailPanel.add(emailField, BorderLayout.CENTER);
        row1.add(emailPanel);

        panel.add(row1);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Second row - Course and Marks
        JPanel row2 = new JPanel(new GridLayout(1, 2, 15, 0));
        row2.setBackground(PANEL_COLOR);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel coursePanel = new JPanel(new BorderLayout(8, 0));
        coursePanel.setBackground(PANEL_COLOR);
        JLabel courseIcon = new JLabel("Course:");
        courseIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        courseIcon.setForeground(new Color(80, 80, 80));
        coursePanel.add(courseIcon, BorderLayout.WEST);

        courseComboBox = new JComboBox<>(courses);
        courseComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseComboBox.setBackground(Color.WHITE);
        courseComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        coursePanel.add(courseComboBox, BorderLayout.CENTER);
        row2.add(coursePanel);

        JPanel marksPanel = new JPanel(new BorderLayout(8, 0));
        marksPanel.setBackground(PANEL_COLOR);
        JLabel marksIcon = new JLabel("Marks (0-100):");
        marksIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        marksIcon.setForeground(new Color(80, 80, 80));
        marksPanel.add(marksIcon, BorderLayout.WEST);

        marksField = createStyledTextField();
        marksField.setPreferredSize(new Dimension(100, 38));
        marksField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateInputs();
            }

            public void removeUpdate(DocumentEvent e) {
                validateInputs();
            }

            public void insertUpdate(DocumentEvent e) {
                validateInputs();
            }
        });
        marksPanel.add(marksField, BorderLayout.CENTER);
        row2.add(marksPanel);

        panel.add(row2);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Third row - Faculty and Department
        JPanel row3 = new JPanel(new GridLayout(1, 2, 15, 0));
        row3.setBackground(PANEL_COLOR);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel facultyPanel = new JPanel(new BorderLayout(8, 0));
        facultyPanel.setBackground(PANEL_COLOR);
        JLabel facultyIcon = new JLabel("Faculty:");
        facultyIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        facultyIcon.setForeground(new Color(80, 80, 80));
        facultyPanel.add(facultyIcon, BorderLayout.WEST);

        facultyComboBox = new JComboBox<>();
        facultyComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        facultyComboBox.setBackground(Color.WHITE);
        facultyComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        facultyComboBox.addActionListener(e -> {
            String selected = (String) facultyComboBox.getSelectedItem();
            if (selected != null && !selected.equals("Select Faculty")) {
                loadDepartments(selected);
            }
        });
        facultyPanel.add(facultyComboBox, BorderLayout.CENTER);
        row3.add(facultyPanel);

        JPanel deptPanel = new JPanel(new BorderLayout(8, 0));
        deptPanel.setBackground(PANEL_COLOR);
        JLabel deptIcon = new JLabel("Department:");
        deptIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deptIcon.setForeground(new Color(80, 80, 80));
        deptPanel.add(deptIcon, BorderLayout.WEST);

        departmentComboBox = new JComboBox<>();
        departmentComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        departmentComboBox.setBackground(Color.WHITE);
        departmentComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        deptPanel.add(departmentComboBox, BorderLayout.CENTER);
        row3.add(deptPanel);

        panel.add(row3);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Fourth row - Year and Semester
        JPanel row4 = new JPanel(new GridLayout(1, 2, 15, 0));
        row4.setBackground(PANEL_COLOR);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel yearPanel = new JPanel(new BorderLayout(8, 0));
        yearPanel.setBackground(PANEL_COLOR);
        JLabel yearIcon = new JLabel("Year of Study:");
        yearIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        yearIcon.setForeground(new Color(80, 80, 80));
        yearPanel.add(yearIcon, BorderLayout.WEST);

        yearComboBox = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
        yearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearComboBox.setBackground(Color.WHITE);
        yearComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        yearPanel.add(yearComboBox, BorderLayout.CENTER);
        row4.add(yearPanel);

        JPanel semesterPanel = new JPanel(new BorderLayout(8, 0));
        semesterPanel.setBackground(PANEL_COLOR);
        JLabel semesterIcon = new JLabel("Semester:");
        semesterIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        semesterIcon.setForeground(new Color(80, 80, 80));
        semesterPanel.add(semesterIcon, BorderLayout.WEST);

        semesterComboBox = new JComboBox<>(new Integer[] { 1, 2 });
        semesterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semesterComboBox.setBackground(Color.WHITE);
        semesterComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        semesterPanel.add(semesterComboBox, BorderLayout.CENTER);
        row4.add(semesterPanel);

        panel.add(row4);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(PANEL_COLOR);

        addButton = createStyledButton("Add Student", SUCCESS_COLOR);
        updateButton = createStyledButton("Update", WARNING_COLOR);
        deleteButton = createStyledButton("Delete", DANGER_COLOR);
        searchButton = createStyledButton("Search", INFO_COLOR);
        showAllButton = createStyledButton("Show All", PRIMARY_COLOR);
        clearButton = createStyledButton("Clear", new Color(149, 165, 166));
        statisticsButton = createStyledButton("Statistics", new Color(155, 89, 182));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(showAllButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(statisticsButton);

        panel.add(buttonPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);

        addButton.setEnabled(false);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(125, 40));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        button.addActionListener(e -> {
            if (button == addButton)
                addStudent();
            else if (button == updateButton)
                updateStudent();
            else if (button == deleteButton)
                deleteStudent();
            else if (button == searchButton)
                searchStudents();
            else if (button == showAllButton)
                loadAllStudents();
            else if (button == clearButton)
                clearInputFields();
            else if (button == statisticsButton) {
                new StatisticsGUI().setVisible(true);
            }
        });

        return button;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_COLOR);

        JLabel tableTitle = new JLabel("Student Records");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY_COLOR);
        titlePanel.add(tableTitle, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(PANEL_COLOR);
        JLabel filterLabel = new JLabel("Quick Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(filterLabel);

        JComboBox<String> gradeFilter = new JComboBox<>(
                new String[] { "All Grades", "A (90-100)", "B (80-89)", "C (70-79)", "D (60-69)", "F (0-59)" });
        gradeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradeFilter.addActionListener(e -> filterByGrade((String) gradeFilter.getSelectedItem()));
        filterPanel.add(gradeFilter);

        titlePanel.add(filterPanel, BorderLayout.EAST);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columns = { "#", "Student ID", "Name", "Email", "Course", "Marks", "Grade", "Faculty", "Department",
                "Year", "Semester" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.setRowHeight(35);
        studentTable.setSelectionBackground(new Color(52, 152, 219, 50));
        studentTable.setSelectionForeground(Color.BLACK);
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(230, 230, 230));
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tableRowSelected();
            }
        });

        // Set column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(8).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(9).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(10).setPreferredWidth(60);

        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(250, 250, 250));
                    } else {
                        c.setBackground(Color.WHITE);
                    }

                    if (column == 6 && value != null) {
                        String grade = value.toString();
                        if (grade.startsWith("A")) {
                            c.setBackground(new Color(46, 204, 113, 40));
                        } else if (grade.startsWith("B")) {
                            c.setBackground(new Color(52, 152, 219, 40));
                        } else if (grade.startsWith("C")) {
                            c.setBackground(new Color(241, 196, 15, 40));
                        } else if (grade.startsWith("D")) {
                            c.setBackground(new Color(230, 126, 34, 40));
                        } else {
                            c.setBackground(new Color(231, 76, 60, 40));
                        }
                    }
                }

                if (column == 0 || column == 5 || column == 9 || column == 10) {
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                return c;
            }
        });

        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setPreferredSize(new Dimension(1200, 400));

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PANEL_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel footerLabel = new JLabel(
                "Tip: Click on any row to edit or delete | Statistics button shows detailed analytics");
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void filterByGrade(String gradeFilter) {
        try {
            Student tempStudent = new Student("", "", "", 0);
            List<Student> students = tempStudent.getAllStudents();
            tableModel.setRowCount(0);
            int id = 1;

            for (Student s : students) {
                String grade = StringUtils.getGrade(s.getMarks());
                boolean include = false;

                switch (gradeFilter) {
                    case "All Grades":
                        include = true;
                        break;
                    case "A (90-100)":
                        include = grade.equals("A+") || grade.equals("A");
                        break;
                    case "B (80-89)":
                        include = grade.equals("B+") || grade.equals("B");
                        break;
                    case "C (70-79)":
                        include = grade.equals("C+") || grade.equals("C");
                        break;
                    case "D (60-69)":
                        include = grade.equals("D");
                        break;
                    case "F (0-59)":
                        include = grade.equals("F");
                        break;
                }

                if (include) {
                    addStudentToTable(s, id++);
                }
            }
            updateStatus("Filtered: Showing " + (id - 1) + " students with " + gradeFilter, INFO_COLOR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(52, 73, 94));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JMenu fileMenu = createStyledMenu("File");
        JMenuItem exitItem = createStyledMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu studentsMenu = createStyledMenu("Students");
        JMenuItem addItem = createStyledMenuItem("Add Student");
        JMenuItem updateItem = createStyledMenuItem("Update Student");
        JMenuItem deleteItem = createStyledMenuItem("Delete Student");

        addItem.addActionListener(e -> clearInputFields());
        updateItem.addActionListener(e -> updateStudent());
        deleteItem.addActionListener(e -> deleteStudent());

        studentsMenu.add(addItem);
        studentsMenu.add(updateItem);
        studentsMenu.add(deleteItem);

        JMenu viewMenu = createStyledMenu("View");
        JMenuItem refreshItem = createStyledMenuItem("Refresh");
        refreshItem.addActionListener(e -> loadAllStudents());
        viewMenu.add(refreshItem);

        JMenuItem statsItem = createStyledMenuItem("Statistics Dashboard");
        statsItem.addActionListener(e -> new StatisticsGUI().setVisible(true));
        viewMenu.add(statsItem);

        JMenu helpMenu = createStyledMenu("Help");
        JMenuItem aboutItem = createStyledMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(studentsMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String title) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return item;
    }

    private void loadFaculties() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT faculty_name FROM faculties ORDER BY faculty_name");

            facultyComboBox.removeAllItems();
            facultyComboBox.addItem("Select Faculty");
            while (rs.next()) {
                facultyComboBox.addItem(rs.getString("faculty_name"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDepartments(String facultyName) {
        try {
            String sql = "SELECT d.dept_name FROM departments d " +
                    "JOIN faculties f ON d.faculty_id = f.id " +
                    "WHERE f.faculty_name = ? ORDER BY d.dept_name";

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, facultyName);
            ResultSet rs = pstmt.executeQuery();

            departmentComboBox.removeAllItems();
            departmentComboBox.addItem("Select Department");
            while (rs.next()) {
                departmentComboBox.addItem(rs.getString("dept_name"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getFacultyId(String facultyName) throws SQLException {
        String sql = "SELECT id FROM faculties WHERE faculty_name = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, facultyName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }
        return 0;
    }

    private int getDepartmentId(String deptName) throws SQLException {
        String sql = "SELECT id FROM departments WHERE dept_name = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, deptName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }
        return 0;
    }

    private void validateInputs() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String marksText = marksField.getText().trim();

        boolean isValid = true;
        String errorMsg = "";

        if (name.isEmpty()) {
            isValid = false;
            errorMsg = "Name is required";
        } else if (email.isEmpty() || !StringUtils.isValidEmail(email)) {
            isValid = false;
            errorMsg = "Valid email is required";
        } else if (marksText.isEmpty()) {
            isValid = false;
            errorMsg = "Marks are required";
        } else {
            try {
                int marks = Integer.parseInt(marksText);
                if (!StringUtils.isValidMarks(marks)) {
                    isValid = false;
                    errorMsg = "Marks must be between 0 and 100";
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errorMsg = "Marks must be a number";
            }
        }

        addButton.setEnabled(isValid);
        if (!isValid) {
            updateStatus(errorMsg, DANGER_COLOR);
        } else {
            updateStatus("All inputs valid - Ready to add", SUCCESS_COLOR);
        }
    }

    private void addStudent() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String course = (String) courseComboBox.getSelectedItem();
            int marks = Integer.parseInt(marksField.getText().trim());

            String facultyName = (String) facultyComboBox.getSelectedItem();
            String deptName = (String) departmentComboBox.getSelectedItem();
            int year = (int) yearComboBox.getSelectedItem();
            int semester = (int) semesterComboBox.getSelectedItem();

            if (facultyName == null || facultyName.equals("Select Faculty")) {
                JOptionPane.showMessageDialog(this, "Please select a faculty!",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (deptName == null || deptName.equals("Select Department")) {
                JOptionPane.showMessageDialog(this, "Please select a department!",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int facultyId = getFacultyId(facultyName);
            int departmentId = getDepartmentId(deptName);

            Student student = new Student(name, email, course, marks);
            student.setFacultyId(facultyId);
            student.setDepartmentId(departmentId);
            student.setYearOfStudy(year);
            student.setSemester(semester);

            if (student.add()) {
                JOptionPane.showMessageDialog(this, "Student added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllStudents();
                clearInputFields();
                updateStatus("Student added successfully", SUCCESS_COLOR);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                JOptionPane.showMessageDialog(this, "Student with this email already exists!",
                        "Duplicate Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateStudent() {
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to update!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String course = (String) courseComboBox.getSelectedItem();
            int marks = Integer.parseInt(marksField.getText().trim());

            String facultyName = (String) facultyComboBox.getSelectedItem();
            String deptName = (String) departmentComboBox.getSelectedItem();
            int year = (int) yearComboBox.getSelectedItem();
            int semester = (int) semesterComboBox.getSelectedItem();

            int facultyId = getFacultyId(facultyName);
            int departmentId = getDepartmentId(deptName);

            Student student = new Student(currentStudentId, name, email, course, marks);
            student.setFacultyId(facultyId);
            student.setDepartmentId(departmentId);
            student.setYearOfStudy(year);
            student.setSemester(semester);

            if (student.update()) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllStudents();
                clearInputFields();
                updateStatus("Student updated successfully", SUCCESS_COLOR);
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteStudent() {
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Student student = new Student(currentStudentId, "", "", "", 0);
                if (student.delete()) {
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllStudents();
                    clearInputFields();
                    updateStatus("Student deleted successfully", SUCCESS_COLOR);
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void searchStudents() {
        String keyword = JOptionPane.showInputDialog(this, "Enter name, ID, or email to search:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                Student tempStudent = new Student("", "", "", 0);
                tableModel.setRowCount(0);
                List<Student> students = tempStudent.getAllStudents();
                int found = 0;
                int id = 1;
                for (Student s : students) {
                    if (s.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                            s.getStudentId().toLowerCase().contains(keyword.toLowerCase()) ||
                            s.getEmail().toLowerCase().contains(keyword.toLowerCase())) {
                        addStudentToTable(s, id++);
                        found++;
                    }
                }
                updateStatus("Found " + found + " results for '" + keyword + "'", INFO_COLOR);
                if (found == 0) {
                    JOptionPane.showMessageDialog(this, "No students found matching '" + keyword + "'",
                            "Search Results", JOptionPane.INFORMATION_MESSAGE);
                    loadAllStudents();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAllStudents() {
        try {
            Student tempStudent = new Student("", "", "", 0);
            List<Student> students = tempStudent.getAllStudents();

            tableModel.setRowCount(0);
            int id = 1;
            for (Student s : students) {
                addStudentToTable(s, id++);
            }
            countLabel.setText("Total Students: " + students.size());
            updateStatus("Loaded " + students.size() + " students", INFO_COLOR);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addStudentToTable(Student student) {
        addStudentToTable(student, tableModel.getRowCount() + 1);
    }

    private void addStudentToTable(Student student, int id) {
        Object[] row = {
                id,
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getMarks(),
                StringUtils.getGrade(student.getMarks()),
                student.getFacultyName() != null ? student.getFacultyName() : "N/A",
                student.getDepartmentName() != null ? student.getDepartmentName() : "N/A",
                student.getYearOfStudy(),
                student.getSemester()
        };
        tableModel.addRow(row);
    }

    private void tableRowSelected() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            currentStudentId = (String) tableModel.getValueAt(selectedRow, 1);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 2));
            emailField.setText((String) tableModel.getValueAt(selectedRow, 3));
            courseComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
            marksField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));

            String faculty = (String) tableModel.getValueAt(selectedRow, 7);
            if (faculty != null && !faculty.equals("N/A")) {
                facultyComboBox.setSelectedItem(faculty);
                loadDepartments(faculty);
                departmentComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 8));
            }

            yearComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 9));
            semesterComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 10));

            updateStatus("Selected student: " + currentStudentId, INFO_COLOR);
        }
    }

    private void clearInputFields() {
        currentStudentId = null;
        nameField.setText("");
        emailField.setText("");
        marksField.setText("");
        courseComboBox.setSelectedIndex(0);
        facultyComboBox.setSelectedIndex(0);
        departmentComboBox.removeAllItems();
        departmentComboBox.addItem("Select Department");
        yearComboBox.setSelectedIndex(0);
        semesterComboBox.setSelectedIndex(0);
        studentTable.clearSelection();
        nameField.requestFocus();
        updateStatus("Ready to add new student", INFO_COLOR);
        addButton.setEnabled(false);
    }

    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        Timer timer = new Timer(3000, e -> {
            if (statusLabel.getText().equals(message)) {
                statusLabel.setText(" ");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showAboutDialog() {
        String message = "Student Management System\nVersion 3.0\n\n" +
                "Developed for CSE Year 2\n" +
                "Technologies: Java Swing, MySQL, JDBC\n\n" +
                "Features:\n" +
                "  - Faculty and Department Classification\n" +
                "  - Year of Study and Semester Tracking\n" +
                "  - Statistics Dashboard with Analytics\n" +
                "  - Add, Update, Delete Students\n" +
                "  - Search Functionality\n" +
                "  - Grade Filter\n" +
                "  - Real-time Validation\n" +
                "  - Grade Calculation\n" +
                "  - Title Case Conversion\n" +
                "  - Modern UI with Scroll Support\n\n" +
                "(c) 2024 Student Management System";
        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }
}