package model.game;

import model.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class MovesService {

    public static void selectAll(List<Moves> destination, DatabaseConnection database) { }
    public static Moves selectById(int id, DatabaseConnection database) {
        return null;
    }
    public static void save(Moves itemToSave, DatabaseConnection database) {
        Moves existingItem = null;
        if (itemToSave.getMoveId() != 0) existingItem = selectById(itemToSave.getMoveId(), database);

        try {
            if (existingItem == null) {
                PreparedStatement statement = database.newStatement("INSERT INTO game (game_ID, move, moves_in) VALUES (?,?,?)");
                statement.setInt(1, itemToSave.getGameId());
                statement.setString(2, itemToSave.getMove());
                statement.setInt(3, itemToSave.getMoves_in());
                database.executeUpdate(statement);
            }
            else {
                PreparedStatement statement = database.newStatement("UPDATE game SET game_ID = ?, move = ?, moves_in = ? WHERE id = ?");
                statement.setInt(1, itemToSave.getGameId());
                statement.setString(2, itemToSave.getMove());
                statement.setInt(3, itemToSave.getMoves_in());
                statement.setInt(4, itemToSave.getGameId());
                database.executeUpdate(statement);
            }
        } catch (SQLException resultsException) {
            System.out.println("Database saving error: " + resultsException.getMessage());
        }




    }	// insert & update
    public static void deleteById(int id, DatabaseConnection database) { }

}