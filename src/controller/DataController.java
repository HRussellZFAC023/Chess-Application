package controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import model.DatabaseConnection;
import model.GameView;
import model.MoveView;
import model.game.Games;
import model.game.GamesService;
import model.game.Moves;
import model.game.MovesService;
import model.tournament.*;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.CLOSE;

public class DataController {

    private final DatabaseConnection tournamentDatabase;
    final DatabaseConnection gameDatabase;
    private ButtonType option1;
    private TableView<MoveView> tableView;
    private Games game;
    List<MoveView> allMoves = new ArrayList<>();

    public DataController(TableView<MoveView> tableView) {

        System.out.println("Initialising main controller...");
        tournamentDatabase = new DatabaseConnection( "src\\assets\\Tournament_Database.db" );
        gameDatabase = new DatabaseConnection( "src\\assets\\Moves_Database.db" );
        this.tableView = tableView;

        resetGame();
        updateTable();
    }

    public void resetGame() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy.MM.dd" );
        LocalDate localDate = LocalDate.now();

        game = new Games( getCurrentGameId() + 1 , dtf.format( localDate ) , "Unknown" , "Unknown" , "*" );
        GamesService.save( game , gameDatabase );
    }


    void updateTable(String move) {
        Moves saveItem = new Moves( getMoveId() + 1 , getCurrentGameId() , move );
        MovesService.save( saveItem , gameDatabase );
        allMoves.clear();
        MovesService.selectForTable( allMoves , gameDatabase , getCurrentGameId() );
        tableView.setItems( FXCollections.observableList( allMoves ) );
        //tableView.getSelectionModel().select(allMoves.size()-1);
    }

    public void updateTable() {
        allMoves.clear();
        MovesService.selectForTable( allMoves , gameDatabase , getCurrentGameId() );
        tableView.setItems( FXCollections.observableList( allMoves ) );
    }

    private int getCurrentGameId() {
        ArrayList<Games> list = new ArrayList<>();
        GamesService.selectAll( list , gameDatabase );
        try {
            return list.get( list.size() - 1 ).getGameId();
        } catch ( Exception empty ) {
            return 0;
        }
    }

    int getMoveId() {
        ArrayList<Moves> list = new ArrayList<>();
        MovesService.selectAll( list , gameDatabase );
        try {
            return list.get( list.size() - 1 ).getMoveId();
        } catch ( Exception empty ) {
            return 0;
        }
    }

    public void testDb () {
        ArrayList<Enrollments> testList = new ArrayList<>();
        ArrayList<Pairings> testList2 = new ArrayList<>();
        ArrayList<Players> testList3 = new ArrayList<>();
        ArrayList<Tournaments> testList4 = new ArrayList<>();
        ArrayList<Games> testList5 = new ArrayList<>();
        ArrayList<Moves> testList6 = new ArrayList<>();

        EnrollmentsService.selectAll(testList,tournamentDatabase);
        PairingsService.selectAll(testList2,tournamentDatabase);
        PlayersService.selectAll(testList3,tournamentDatabase);
        TournamentsService.selectAll(testList4,tournamentDatabase);
        GamesService.selectAll(testList5,gameDatabase);

        for (Enrollments c : testList) {
            System.out.println(c);
        }
        for (Pairings c : testList2) {
            System.out.println(c);
        }
        for (Players c : testList3) {
            System.out.println(c);
        }
        for (Tournaments c : testList4) {
            System.out.println(c);
        }
        for (Games c : testList5) {
            System.out.println(c);
        }
        //how to save temp

        Moves move = new Moves( 5 , 0 , "e4" );
        MovesService.save( move , gameDatabase );

    }

    public void openPgn() {
        String fileLocation;
        ArrayList<String> fileContents = new ArrayList<>();

            FileDialog dialog = new FileDialog( ( Frame ) null , "Select File to Open" );
            dialog.setMode( FileDialog.LOAD );
            dialog.setVisible( true );
            String filePath = dialog.getDirectory();
            fileLocation = dialog.getFile();
            System.out.println( "[" + filePath + "] " + fileLocation + " chosen." );

            try {
                try ( BufferedReader br = new BufferedReader( new FileReader( filePath + fileLocation ) ) ) {
                    String line = br.readLine();
                    while ( line != null ) {
                        fileContents.add( line );
                        line = br.readLine();
                    }
                }
            } catch ( IOException e ) {
                System.out.println("could not read file");
            }

            System.out.println( fileContents );

            boolean foundStart = false;
            String date;
            String w;
            String b;
            String gameResult;

            for ( String line : fileContents ) {
                if ( line.contains( "Date" ) ) {
                    date = extract( line , foundStart );
                    game.setGameDate( date );
                } else if ( line.contains( "White" ) ) {
                    w = extract( line , foundStart );
                    game.setWhite( w );
                } else if ( line.contains( "Black" ) ) {
                    b = extract( line , foundStart );
                    game.setBlack( b );
                } else if ( line.contains( "Result" ) ) {
                    gameResult = extract( line , foundStart );
                    game.setResult( gameResult );
                } else if ( line.trim().equals( "" ) ) {
                    foundStart = true;
                    GamesService.save( game , gameDatabase );
                } else if ( foundStart ) {

                    //System.out.println(line);
                    //Comments are inserted by either a ; (a comment that continues to the end of the line) or a { (which continues until a matching }). Comments do not nest.
                    //I must remove comments before spiting. Also should remove numbers
                    //also must remove result at the end
                    line = line.replaceAll( "\\{.*}|;.*|\\d{0,9}\\.|1/2-1/2|1-0|0-1|\\*|\\+|!" , "" );
                    String splitLine[] = line.split( " " );

                    for ( String s : splitLine ) {
                        if ( ! s.equals( " " ) && ! s.equals( "" ) ) {
                            Moves move = new Moves( getMoveId() + 1 , getCurrentGameId() , s );
                            MovesService.save( move , gameDatabase );
                            updateTable();
                            System.out.println(s);
                        }
                    }
                }
            }
    }

    public void openFromDb(){
        game = databaseDialogue().get();
        game.setGameId( getCurrentGameId() + 1 ); //makes copy
        List<Moves> selectAll = new ArrayList<>();
        MovesService.selectAll( selectAll , gameDatabase);
        game = new Games(0, game.getGameDate(), game.getWhite(),game.getBlack(),game.getResult());
        GamesService.save( game , gameDatabase );
        for ( Moves m : selectAll ) {
            if ( m.getGameId() == game.getGameId() ) {
                updateTable( m.getMove() ); //saves move and displays in table
                System.out.println(m.getMove());
            }

        }
    }


    private Optional<Games> databaseDialogue() {

        Dialog<Games> dialog = new Dialog<>();
        dialog.setTitle( "Select game" );
        dialog.setHeaderText( "Select game" );
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add("assets/stylesheet.css");
        dialogPane.setMinSize( 800 , 500 );


        VBox vbox = new VBox( 10 );
        TableView<GameView> gamesTable = new TableView<>();
        gamesTable.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        //ID column (HIDDEN)
        TableColumn<GameView, String> IdColumn = new TableColumn<>();
        IdColumn.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        gamesTable.getColumns().add( IdColumn );
        IdColumn.setVisible( false );

        //Date Played column
        TableColumn<GameView, String> dateColumn = new TableColumn<>( "Date" );
        dateColumn.setCellValueFactory( new PropertyValueFactory<>( "date" ) );
        gamesTable.getColumns().add( dateColumn );

        //White player
        TableColumn<GameView, String> whitePlayerColumn = new TableColumn<>( "White" );
        whitePlayerColumn.setCellValueFactory( new PropertyValueFactory<>( "white" ) );
        gamesTable.getColumns().add( whitePlayerColumn );

        //Black player
        TableColumn<GameView, String> blackPlayerColumn = new TableColumn<>( "Black" );
        blackPlayerColumn.setCellValueFactory( new PropertyValueFactory<>( "black" ) );
        gamesTable.getColumns().add( blackPlayerColumn );

        //result column
        TableColumn<GameView, String> result = new TableColumn<>( "Result" );
        result.setCellValueFactory( new PropertyValueFactory<>( "result" ) );
        gamesTable.getColumns().add( result );

        ArrayList<GameView> list = new ArrayList<>();
        GamesService.selectForTable( list , gameDatabase );
        gamesTable.setItems( FXCollections.observableList( list ) );

        Button delete = new Button( "Delete game" );
        delete.setOnAction( e -> {
            if ( gamesTable.getSelectionModel().getSelectedItem() != null ) {
                MovesService.deleteByGameId( gamesTable.getSelectionModel().getSelectedItem().getId() , gameDatabase );
                GamesService.deleteById( gamesTable.getSelectionModel().getSelectedItem().getId() , gameDatabase );
                list.clear();
                GamesService.selectForTable( list , gameDatabase );
                gamesTable.setItems( FXCollections.observableList( list ) );
            }
        } );


        vbox.getChildren().addAll( gamesTable , delete );
        vbox.setAlignment( Pos.TOP_RIGHT );
        dialog.getDialogPane().setContent( vbox );
        dialog.getDialogPane().getButtonTypes().add( ButtonType.APPLY );

        dialog.setResultConverter( dialogButton -> {
            if ( dialogButton == ButtonType.APPLY &&
                    gamesTable.getSelectionModel().getSelectedItem() != null ) {
                return new Games(
                        gamesTable.getSelectionModel().getSelectedItem().getId() ,
                        gamesTable.getSelectionModel().getSelectedItem().getDate() ,
                        gamesTable.getSelectionModel().getSelectedItem().getWhite() ,
                        gamesTable.getSelectionModel().getSelectedItem().getBlack() ,
                        gamesTable.getSelectionModel().getSelectedItem().getResult() );
            }
            return game; //reverts back to current game
        } );


        return dialog.showAndWait();
    }


    private String extract (String line,boolean foundStart) {
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if ( c == '"' ) foundStart = ! foundStart;
            if ( foundStart && c != '"' )
                sb.append(c);
        }
        return String.valueOf(sb);
    }

    private Optional<ButtonType> dialogueBox(String displayText , String button1 , String button2) {
        option1 = new ButtonType(button1,ButtonBar.ButtonData.NO);
        ButtonType option2 = new ButtonType(button2,ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,displayText,option1,option2,CLOSE);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("assets/stylesheet.css");
        Node closeButton = alert.getDialogPane().lookupButton(CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        return alert.showAndWait();

    }



    public void exitPrompt () {
            Optional<ButtonType> msg2Result = dialogueBox(
                    "Save current game?" ,
                    "Yes" , "No"
            );
            if ( msg2Result.isPresent() && msg2Result.get() == option1 ) {
                tournamentDatabase.disconnect();
                gameDatabase.disconnect();
                System.exit( 0 );
            } else {
                //delete current game id
                MovesService.deleteByGameId( getCurrentGameId() , gameDatabase );
                GamesService.deleteById( getCurrentGameId() , gameDatabase );
                tournamentDatabase.disconnect();
                gameDatabase.disconnect();
                System.exit( 0 );
            }
    }

    public void exitPrompt(WindowEvent event)  {
        exitPrompt ();
        event.consume();
    }

    public void doSomething () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("This feature has not yet been implemented");
        alert.showAndWait();
    }

    public void saveAsPGN() {
        Optional<ButtonType> msgResult = dialogueBox( "Export game as PGN" ,
                "Yes" , "No" );
        if ( msgResult.isPresent() && msgResult.get() == option1 ) {
            FileDialog fileDialog = new FileDialog( new Frame() , "Save" , FileDialog.SAVE );
            fileDialog.setFilenameFilter( (dir , name) -> name.endsWith( ".pgn" ) );
            fileDialog.setFile( "Untitled.pgn" );
            fileDialog.setVisible( true );

            ArrayList<Moves> list = new ArrayList<>();
            MovesService.selectAll( list , gameDatabase );
            int moveCounter = 1;
            int positionCounter = 0;
            StringBuilder string = new StringBuilder();
            string.append( "[Date \"" ).append( game.getGameDate() ).append( "\"]" ).append( "\r\n" );
            string.append( "[White \"" ).append( game.getWhite() ).append( "\"]" ).append( "\r\n" );
            string.append( "[Black \"" ).append( game.getBlack() ).append( "\"]" ).append( "\r\n" );
            string.append( "[Result \"" ).append( game.getResult() ).append( "\"]" ).append( "\r\n" );
            string.append( "\r\n" );
            for ( Moves c : list ) {
                if ( getCurrentGameId() == c.getGameId() ) {
                    if ( positionCounter % 2 == 0 ) string.append( moveCounter++ ).append( ". " ).append( c.getMove() );
                    else string.append( " " ).append( c.getMove() ).append( " " );
                    positionCounter++;
                }
            }
            string.append( game.getResult() );

            try ( Writer writer = new BufferedWriter( new OutputStreamWriter(
                    new FileOutputStream( fileDialog.getDirectory() + fileDialog.getFile() ) ) ) ) {
                writer.write( String.valueOf( string ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        }

    }

    public void inputNameAndResult() {

        // Create the custom dialog.
        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle( "Results Dialog" );
        dialog.setHeaderText( "Enter match results:" );
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add("assets/stylesheet.css");

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll( ButtonType.OK , ButtonType.CANCEL );

        GridPane grid = new GridPane();
        grid.setHgap( 10 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20 , 150 , 10 , 10 ) );

        TextField whitePlayer = new TextField();
        whitePlayer.setPromptText( "player1" );
        TextField blackPlayer = new TextField();
        blackPlayer.setPromptText( "player2" );
        ComboBox<String> choices = new ComboBox<>();
        choices.getItems().addAll( "1-0" , "0-1" , "1/2-1/2" , "*" );
        choices.setValue( "*" ); //* means ongoing so it is the default value

        grid.add( new Label( "White Player" ) , 0 , 0 );
        grid.add( whitePlayer , 1 , 0 );
        grid.add( new Label( "Black player" ) , 0 , 1 );
        grid.add( blackPlayer , 1 , 1 );
        grid.add( new Label( "Result" ) , 0 , 2 );
        grid.add( choices , 1 , 2 );

        dialog.getDialogPane().setContent( grid );

        dialog.setResultConverter( (ButtonType button) -> {
            if ( button == ButtonType.OK ) {
                return game;
            }
            return null;
        } );

        Optional<Object> result = dialog.showAndWait();

        result.ifPresent( save -> {
            game.setGameId( getCurrentGameId() );
            game.setWhite( whitePlayer.getText() );
            game.setBlack( blackPlayer.getText() );
            game.setResult( choices.getValue() );
            GamesService.save( game , gameDatabase );
        } );


    }

    public boolean newGame() {
        Optional<ButtonType> msgResult = dialogueBox( "Save current game?" ,
                "Yes" , "No" );

        if ( msgResult.isPresent() && msgResult.get() != option1 && msgResult.get() != ButtonType.CLOSE )//button2F
        {
            MovesService.deleteByGameId( getCurrentGameId() , gameDatabase );
            GamesService.deleteById( getCurrentGameId() , gameDatabase );
        }
        if ( msgResult.isPresent() && msgResult.get() == option1 ) {
            Optional<ButtonType> msg2Result = dialogueBox( "Update result?" ,
                    "Yes" , "No" );
            if ( msg2Result.isPresent() && msg2Result.get() == option1 ) {
                inputNameAndResult();
            }
        }
        if ( msgResult.isPresent() && msgResult.get() != ButtonType.CLOSE ) {
            resetGame();
            updateTable();
            return true;
        }
        return false;
    }

    public void aboutMessage() {
        Alert alert = new Alert( Alert.AlertType.INFORMATION ,
                "Created by Henry Russell 2017 " +
                "\nIcons from Wikimedia Commons" +
                "\nStylesheet \"Dark theme\" http://code.makery.ch/library/javafx-8-tutorial/part4/" +
                "\nExemplar 1 https://github.com/SteveBirtles/PizzaProject" +
                "\nExemplar 2 https://github.com/Stevoisiak/JavaFX-Online-Chess" , ButtonType.CLOSE );
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("assets/stylesheet.css");
        alert.show();
    }


    public DatabaseConnection getGameDatabase() {
        return gameDatabase;
    }

    public List<MoveView> getAllMoves() {
        return allMoves;
    }
}
