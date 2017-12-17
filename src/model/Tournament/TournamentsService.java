package model.Tournament;

import model.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TournamentsService {
    public static void selectAll(List<Tournaments> targetList, DatabaseConnection database) {
        PreparedStatement statement = database.newStatement("SELECT Tournament_ID, Date, Location FROM Tournament");

        try {
            if (statement != null) {

                ResultSet results = database.executeQuery(statement);

                if (results != null) {
                    while (results.next()) {
                        targetList.add(new Tournaments(results.getInt("Tournament_ID"), results.getString("Date"), results.getString("Location")));
                    }
                }
            }
        } catch (SQLException resultsException) {
            System.out.println("Database select all error: " + resultsException.getMessage());
        }
    }
    public static Tournaments selectById(int id, DatabaseConnection database) {
        return  null;
    }
    public static void save(Tournaments console, DatabaseConnection database) { }	// insert & update
    public static void deleteById(int id, DatabaseConnection database) { }
}
