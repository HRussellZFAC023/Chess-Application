package controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import model.DatabaseConnection;
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
    private final DatabaseConnection gameDatabase;
    private ButtonType option1;
    private TableView<MoveView> tableView;
    private List<MoveView> allMoves = new ArrayList<>();
    private Games game;

    public DataController(TableView<MoveView> tableView) {

        System.out.println("Initialising main controller...");
        tournamentDatabase = new DatabaseConnection( "src\\Assets\\Tournament_Database.db" );
        gameDatabase = new DatabaseConnection( "src\\Assets\\Moves_Database.db" );
        this.tableView = tableView;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy.MM.dd" );
        LocalDate localDate = LocalDate.now();

        game = new Games( 0 , dtf.format( localDate ) , "Unknown" , "Unknown" , "*" );
        GamesService.save( game , gameDatabase );
        updateTable();
    }

    void updateTable(String move) {
        Moves saveItem = new Moves( getMoveId() + 1 , getCurrentGameId() , move );
        MovesService.save( saveItem , gameDatabase );
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

    private int getMoveId() {
        ArrayList<Moves> list = new ArrayList<>();
        MovesService.selectAll( list , gameDatabase );
        try {
            return list.get( list.size() - 1 ).getMoveId();
        } catch ( Exception empty ) {
            return 0;
        }
    }


    private void updateTable() {
        allMoves.clear();
        MovesService.selectForTable( allMoves , gameDatabase , getCurrentGameId() );
        tableView.setItems( FXCollections.observableList( allMoves ) );
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

    public void openSomething() {
        int local = 2;  //0 - not local event; 1- local event; 2 - event cancelled
        String fileLocation;
        ArrayList<String> fileContents = new ArrayList<>();


        Optional<ButtonType> msgResult = dialogueBox( "Would you like to open a game from a database or a PGN file" ,
                "Local Database","PGN File");

        if (msgResult.isPresent() && msgResult.get() == option1 && msgResult.get() != CLOSE)  local = 1;
        else if (msgResult.isPresent() && msgResult.get() != option1 && msgResult.get() != CLOSE) local = 0;
        System.out.println( msgResult );

        if ( local == 0 ) {

            FileDialog dialog = new FileDialog(( Frame ) null,"Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            fileLocation = dialog.getFile();
            System.out.println(fileLocation + " chosen.");

            try {
                try ( BufferedReader br = new BufferedReader(new FileReader(fileLocation)) ) {
                    String line = br.readLine();
                    while ( line != null ) {
                        fileContents.add(line);
                        line = br.readLine();
                    }
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            System.out.println(fileContents);
            Optional<ButtonType> msgResult2 = dialogueBox( "Would you like to save the file to the database?" ,
                    "yes","no");

            if ( msgResult2.isPresent() && msgResult2.get() == option1 && msgResult2.get() != CLOSE ) {
                boolean foundStart = false;
                String date = "";
                String w = "";
                String b = "";
                String gameResult = "";

                for (String line : fileContents) {
                    if ( line.contains("Date") )
                        date = extract(line,foundStart);
                    else if ( line.contains("White") ) {
                        w = extract(line,foundStart);
                    } else if ( line.contains("Black") ) {
                        b = extract(line,foundStart);
                    } else if ( line.contains("Result") ) {
                        gameResult = extract(line,foundStart);
                    } else if ( line.trim().equals("") )
                        foundStart = true;
                    else if ( foundStart ) {


                        //Moves move = new Moves();
                        System.out.println(line);
                    }

                }
                System.out.println(date);
                System.out.println(w);
                System.out.println(b);
                System.out.println(gameResult);


            }

        } else {/*TODO get user to select game from database */}

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

        Node closeButton = alert.getDialogPane().lookupButton(CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        return alert.showAndWait();

    }



    public void exitPrompt () {
        Optional<ButtonType> msgResult = dialogueBox(
                "Are you sure you want to exit?" ,
                "Yes","No"
        );


        if ( msgResult.isPresent() && msgResult.get() == option1 ) {
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
                MovesService.deleteById( getCurrentGameId() , gameDatabase );
                GamesService.deleteById( getCurrentGameId() , gameDatabase );
                tournamentDatabase.disconnect();
                gameDatabase.disconnect();
                System.exit( 0 );
            }
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
            //TODO save as PGN
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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy.MM.dd" );
        LocalDate localDate = LocalDate.now();


        dialog.setResultConverter( (ButtonType button) -> {
            if ( button == ButtonType.OK ) {
                return game;
            }
            return null;
        } );

        Optional<Object> result = dialog.showAndWait();

        result.ifPresent( save -> {
            game = new Games( getCurrentGameId() , dtf.format( localDate ) , whitePlayer.getText() ,
                    blackPlayer.getText() , choices.getValue() );
            GamesService.save( game , gameDatabase );
        } );


    }

    public boolean newGame() {
        Optional<ButtonType> msgResult = dialogueBox( "Save current game?" ,
                "Yes" , "No" );

        if ( msgResult.isPresent() && msgResult.get() != option1 && msgResult.get() != ButtonType.CLOSE )//button2
        {
            MovesService.deleteById( getCurrentGameId() , gameDatabase );
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
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy.MM.dd" );
            LocalDate localDate = LocalDate.now();

            game = new Games( 0 , dtf.format( localDate ) , "Unknown" , "Unknown" , "*" );
            GamesService.save( game , gameDatabase );
            updateTable();
            return true;
        }
        return false;
    }
}
