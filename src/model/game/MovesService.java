package model.game;

import model.DatabaseConnection;
import model.MoveView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovesService {

    public static void selectAll(List<Moves> targetList , DatabaseConnection database) {
        PreparedStatement statement = database.newStatement( "SELECT * FROM Move ORDER BY Move_ID" );

        try {
            if ( statement != null ) {

                ResultSet results = database.executeQuery( statement );

                if ( results != null ) {
                    while ( results.next() ) {
                        targetList.add( new Moves( results.getInt( "Move_ID" ) , results.getInt( "Game_ID" ) ,
                                results.getString( "move" ) ) );
                    }
                }
            }
        } catch ( SQLException resultsException ) {
            System.out.println( "Database select all error: " + resultsException.getMessage() );
        }

    }
    private static Moves selectById (int id,DatabaseConnection database) {
        return null;
    }
    public static void save(Moves itemToSave, DatabaseConnection database) {
        Moves existingItem = null;
        if (itemToSave.getMoveId() != 0) existingItem = selectById(itemToSave.getMoveId(), database);

        try {
            if (existingItem == null) {
                PreparedStatement statement =
                        database.newStatement( "INSERT INTO move (move_ID, game_ID, Move) VALUES (?,?,?)" );
                statement.setInt( 1 , itemToSave.getMoveId() );
                statement.setInt( 2 , itemToSave.getGameId() );
                statement.setString( 3 , itemToSave.getMove() );
                database.executeUpdate(statement);
            }
            else {
                PreparedStatement statement =
                        database.newStatement( "UPDATE game SET game_ID = ?, move = ? WHERE id = ?" );
                statement.setInt( 1 , itemToSave.getMoveId() );
                statement.setInt( 2 , itemToSave.getGameId() );
                statement.setString( 3 , itemToSave.getMove() );
                database.executeUpdate(statement);
            }
        } catch (SQLException resultsException) {
            System.out.println("Database saving error: " + resultsException.getMessage());
        }




    }	// insert & update

    public static void selectForTable(List<MoveView> targetList , DatabaseConnection database , int gameId) {
        ArrayList<String> moves = new ArrayList<>();

        PreparedStatement statement = database.newStatement( "SELECT * From Move ORDER BY move_ID" );

        try {
            if ( statement != null ) {

                ResultSet results = database.executeQuery( statement );

                if ( results != null ) {
                    while ( results.next() ) {

                        if ( results.getInt( "game_ID" ) == gameId )
                            moves.add( results.getString( "move" ) );

                    }
                }
                for ( int i = 0;i < moves.size();i = i + 2 ) {
                    try {
                        targetList.add( new MoveView( moves.get( i ) , moves.get( i + 1 ) ) );
                    } catch ( Exception e ) {
                        targetList.add( new MoveView( moves.get( i ) , "" ) );
                    }

                }
            }
        } catch ( SQLException resultsException ) {
            System.out.println( "Database select all error: " + resultsException.getMessage() );
        }
    }


    public static void deleteById(int id , DatabaseConnection database) {

        PreparedStatement statement = database.newStatement( "DELETE FROM Move WHERE game_ID = ?" );

        try {
            statement.setInt( 1 , id );
            database.executeUpdate( statement );
        } catch ( SQLException resultsException ) {
            System.out.println( "Database deletion error: " + resultsException.getMessage() );
        }

    }

}