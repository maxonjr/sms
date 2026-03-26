package models;

import database.DatabaseConnection;
import utils.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person implements DatabaseOperations {
    private String studentId;
    private String course;
    private int marks;
    private int facultyId;
    private int departmentId;
    private int yearOfStudy;
    private int semester;
    private String facultyName;
    private String departmentName;
    
    // Constructors
    public Student(String studentId, String name, String email, String course, int marks) {
        super(name, email);
        this.studentId = studentId;
        this.course = course;
        this.marks = marks;
    }
    
    public Student(String name, String email, String course, int marks) {
        super(name, email);
        this.course = course;
        this.marks = marks;
    }
    
    public Student(String studentId, String name, String email, String course, int marks, 
                   int facultyId, int departmentId, int yearOfStudy, int semester) {
        super(name, email);
        this.studentId = studentId;
        this.course = course;
        this.marks = marks;
        this.facultyId = facultyId;
        this.departmentId = departmentId;
        this.yearOfStudy = yearOfStudy;
        this.semester = semester;
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
    
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    
    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    
    @Override
    public void displayInfo() {
        System.out.printf("ID: %s | Name: %s | Email: %s | Course: %s | Marks: %d | Faculty: %s | Department: %s%n",
            studentId, name, email, course, marks, facultyName, departmentName);
    }
    
    @Override
    public boolean add() throws SQLException {
        if (studentId == null || studentId.isEmpty()) {
            studentId = generateStudentId();
        }
        
        String sql = "INSERT INTO students (student_id, name, email, course, marks, faculty_id, department_id, year_of_study, semester) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.setString(2, StringUtils.toTitleCase(name));
            pstmt.setString(3, email.toLowerCase());
            pstmt.setString(4, course);
            pstmt.setInt(5, marks);
            pstmt.setInt(6, facultyId);
            pstmt.setInt(7, departmentId);
            pstmt.setInt(8, yearOfStudy);
            pstmt.setInt(9, semester);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    @Override
    public boolean update() throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, course = ?, marks = ?, faculty_id = ?, department_id = ?, year_of_study = ?, semester = ? WHERE student_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, StringUtils.toTitleCase(name));
            pstmt.setString(2, email.toLowerCase());
            pstmt.setString(3, course);
            pstmt.setInt(4, marks);
            pstmt.setInt(5, facultyId);
            pstmt.setInt(6, departmentId);
            pstmt.setInt(7, yearOfStudy);
            pstmt.setInt(8, semester);
            pstmt.setString(9, studentId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    @Override
    public boolean delete() throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    @Override
    public void search(String keyword) throws SQLException {
        String sql = "SELECT s.*, f.faculty_name, d.dept_name FROM students s " +
                     "LEFT JOIN faculties f ON s.faculty_id = f.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "WHERE s.name LIKE ? OR s.student_id LIKE ? OR s.email LIKE ?";
        
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String searchPattern = "%" + keyword + "%";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            rs = pstmt.executeQuery();
            
            System.out.println("\n=== Search Results ===");
            while (rs.next()) {
                System.out.printf("%-10s %-20s %-25s %-20s %-5d %-20s %-20s%n",
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getInt("marks"),
                    rs.getString("faculty_name"),
                    rs.getString("dept_name"));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    @Override
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, f.faculty_name, d.dept_name FROM students s " +
                     "LEFT JOIN faculties f ON s.faculty_id = f.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "ORDER BY s.student_id";
        
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getInt("marks"),
                    rs.getInt("faculty_id"),
                    rs.getInt("department_id"),
                    rs.getInt("year_of_study"),
                    rs.getInt("semester")
                );
                student.setFacultyName(rs.getString("faculty_name"));
                student.setDepartmentName(rs.getString("dept_name"));
                students.add(student);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return students;
    }
    
    private String generateStudentId() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(student_id, 2) AS UNSIGNED)) as max_id FROM students";
        
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            int nextId = 1;
            if (rs.next() && rs.getInt("max_id") > 0) {
                nextId = rs.getInt("max_id") + 1;
            }
            return "S" + String.format("%03d", nextId);
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }
    
    public static List<Object[]> getStatistics() throws SQLException {
        List<Object[]> statistics = new ArrayList<>();
        
        // Statistics by faculty
        String sql = "SELECT f.faculty_name, COUNT(s.id) as student_count, " +
                     "AVG(s.marks) as avg_marks, " +
                     "SUM(CASE WHEN s.marks >= 80 THEN 1 ELSE 0 END) as distinction_count " +
                     "FROM faculties f LEFT JOIN students s ON f.id = s.faculty_id " +
                     "GROUP BY f.id, f.faculty_name";
        
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] stats = new Object[5];
                stats[0] = rs.getString("faculty_name");
                stats[1] = rs.getInt("student_count");
                stats[2] = Math.round(rs.getDouble("avg_marks") * 100) / 100.0;
                stats[3] = rs.getInt("distinction_count");
                stats[4] = rs.getInt("distinction_count") > 0 ? 
                           Math.round((rs.getDouble("distinction_count") / rs.getDouble("student_count")) * 100) : 0;
                statistics.add(stats);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
        return statistics;
    }
}