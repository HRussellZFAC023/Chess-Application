package model.tournament;

import model.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EnrollmentsService {
    public static void selectAll(List<Enrollments> targetList, DatabaseConnection database) {
        PreparedStatement statement = database.newStatement("SELECT Tournament_ID, Student_Number, Has_paid FROM Enrollment ORDER BY Tournament_ID");

        try {
            if (statement != null) {

                ResultSet results = database.executeQuery(statement);

                if (results != null) {
                    while (results.next()) {
                       targetList.add(new Enrollments(results.getInt("Tournament_ID"), results.getInt("Student_Number"), results.getBoolean("Has_paid")));
                    }
                }
            }
        } catch (SQLException resultsException) {
            System.out.println("Database select all error: " + resultsException.getMessage());
        }

    }
    public static Enrollments selectById(int id, DatabaseConnection database) {
        return null;
    }
    public static void save(Enrollments console, DatabaseConnection database) { }	// insert & update
    public static void deleteById(int id, DatabaseConnection database) { }

}

