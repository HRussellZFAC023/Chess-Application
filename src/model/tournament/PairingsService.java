package model.tournament;

import model.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PairingsService {
    public static void selectAll(List<Pairings> targetList, DatabaseConnection database) {
        PreparedStatement statement = database.newStatement("SELECT Round_no, Player_1_number, Player_2_number, Round_ID, Tournament_ID FROM Pairing");

        try {
            if (statement != null) {

                ResultSet results = database.executeQuery(statement);

                if (results != null) {
                    while (results.next()) {
                        targetList.add(new Pairings(results.getInt("Round_no"), results.getInt("Player_1_number"), results.getInt("Player_2_number"), results.getInt("Round_ID"), results.getInt("Tournament_ID") ));
                    }
                }
            }
        } catch (SQLException resultsException) {
            System.out.println("Database select all error: " + resultsException.getMessage());
        }

    }

    public static Pairings selectById(int id, DatabaseConnection database) {
        return null;
    }
    public static void save(Pairings console, DatabaseConnection database) { }	// insert & update
    public static void deleteById(int id, DatabaseConnection database) { }

}
