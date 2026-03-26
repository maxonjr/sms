import java.sql.*;

public class Test {
    public static void main(String[] args) {
        System.out.println("Testing JDBC Connection...");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver loaded!");
            
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", 
                "root", 
                ""
            );
            System.out.println("✓ Connected to MySQL!");
            
            conn.close();
            System.out.println("✓ All tests passed!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}