package utils;

public class StringUtils {
    
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String[] words = input.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    public static boolean isValidStudentId(String studentId) {
        if (studentId == null) return false;
        return studentId.matches("^S\\d{3}$");
    }
    
    public static boolean isValidMarks(int marks) {
        return marks >= 0 && marks <= 100;
    }
    
    public static String getGrade(int marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 75) return "B+";
        if (marks >= 70) return "B";
        if (marks >= 65) return "C+";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }
}