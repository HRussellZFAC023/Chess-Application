package model.game;

import model.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GamesService {

    public static void selectAll(List<Games> targetList, DatabaseConnection database) {
        PreparedStatement statement = database.newStatement("SELECT game_ID, date_Played, white_player, black_player FROM game ORDER BY game_ID");

        try {
            if (statement != null) {

                ResultSet results = database.executeQuery(statement);

                if (results != null) {
                    while (results.next()) {
                        targetList.add(new Games(results.getInt("game_ID"), results.getString("date_Played"), results.getString("white_player"), results.getString("black_player")));
                    }
                }
            }
        } catch (SQLException resultsException) {
            System.out.println("Database select all error: " + resultsException.getMessage());
        }

    }
    private static Games selectById (int id,DatabaseConnection database) {
        return null;
    }
    public static void save(Games itemToSave, DatabaseConnection database) {

        Games existingItem = null;
        if (itemToSave.getGameId() != 0) existingItem = selectById(itemToSave.getGameId(), database);

        try {
            if (existingItem == null) {
                PreparedStatement statement = database.newStatement("INSERT INTO game (date_Played, white_player, black_player) VALUES (?,?,?)");
                statement.setString(1, itemToSave.getGameDate());
                statement.setString(2, itemToSave.getWhite());
                statement.setString(3, itemToSave.getBlack());
                database.executeUpdate(statement);
            }
            else {
                PreparedStatement statement = database.newStatement("UPDATE game SET date_Played = ?, white_player = ?, black_player = ? WHERE game_ID = ?");
                statement.setString(1, itemToSave.getBlack());
                statement.setString(2, itemToSave.getWhite());
                statement.setString(3, itemToSave.getGameDate());
                statement.setInt(4, itemToSave.getGameId());
                database.executeUpdate(statement);
            }
        } catch (SQLException resultsException) {
            System.out.println("Database saving error: " + resultsException.getMessage());
        }


    }	// insert & update
    public static void deleteById(int id, DatabaseConnection database) { }

}