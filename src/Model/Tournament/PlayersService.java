package Model.Tournament;

import Model.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayersService {
    public static void selectAll(List<Players> targetList, DatabaseConnection database) {
        PreparedStatement statement = database.newStatement("SELECT Student_number, First_name, Last_name, First_year FROM Player");

        try {
            if (statement != null) {

                ResultSet results = database.executeQuery(statement);

                if (results != null) {
                    while (results.next()) {
                        targetList.add(new Players(results.getInt("Student_number"), results.getString("First_name"), results.getString("Last_name"), results.getBoolean("First_year") ));
                    }
                }
            }
        } catch (SQLException resultsException) {
            System.out.println("Database select all error: " + resultsException.getMessage());
        }

    }
    public static Players selectById(int id, DatabaseConnection database) {
        return null;
    }
    public static void save(Players console, DatabaseConnection database) { }	// insert & update
    public static void deleteById(int id, DatabaseConnection database) { }

}