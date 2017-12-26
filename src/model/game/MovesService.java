package model.game;

import model.DatabaseConnection;
import model.MoveView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovesService {

    public static void selectAll(List<Moves> destination, DatabaseConnection database) { }
    private static Moves selectById (int id,DatabaseConnection database) {
        return null;
    }
    public static void save(Moves itemToSave, DatabaseConnection database) {
        Moves existingItem = null;
        if (itemToSave.getMoveId() != 0) existingItem = selectById(itemToSave.getMoveId(), database);

        try {
            if (existingItem == null) {
                PreparedStatement statement =
                        database.newStatement( "INSERT INTO move (move_ID, game_ID, Move) VALUES (?,?)" );
                statement.setInt(1, itemToSave.getGameId());
                statement.setString(2, itemToSave.getMove());
                database.executeUpdate(statement);
            }
            else {
                PreparedStatement statement =
                        database.newStatement( "UPDATE game SET game_ID = ?, move = ? WHERE id = ?" );
                statement.setInt(1, itemToSave.getGameId());
                statement.setString(2, itemToSave.getMove());
                statement.setInt( 3 , itemToSave.getGameId() );
                database.executeUpdate(statement);
            }
        } catch (SQLException resultsException) {
            System.out.println("Database saving error: " + resultsException.getMessage());
        }




    }	// insert & update

    public static void selectForTable(List<MoveView> targetList , DatabaseConnection database) {
        ArrayList<String> moves = new ArrayList<>();
        String blackMove = "";
        String whiteMove = "";

        PreparedStatement statement = database.newStatement(
                "SELECT * From Move"
        );

        try {
            if ( statement != null ) {

                ResultSet results = database.executeQuery( statement );

                if ( results != null ) {
                    while ( results.next() ) {
                        moves.add( results.getString( "move" ) );
                    }
                }
                for ( int i = 0;i < moves.size();i++ ) {
                    System.out.println( moves );
                    if ( i % 2 == 0 ) whiteMove = moves.get( i );
                    else {
                        blackMove = moves.get( i );
                        targetList.add( new MoveView( whiteMove , blackMove ) );
                    }
                }
            }
        } catch ( SQLException resultsException ) {
            System.out.println( "Database select all error: " + resultsException.getMessage() );
        }
    }




    public static void deleteById(int id, DatabaseConnection database) { }

}