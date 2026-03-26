package models;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseOperations {
    boolean add() throws SQLException;
    boolean update() throws SQLException;
    boolean delete() throws SQLException;
    void search(String keyword) throws SQLException;
    List<Student> getAllStudents() throws SQLException;
}