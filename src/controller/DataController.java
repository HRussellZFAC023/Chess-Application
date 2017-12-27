package controller;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.WindowEvent;
import model.DatabaseConnection;
import model.MoveView;
import model.game.Games;
import model.game.GamesService;
import model.game.Moves;
import model.game.MovesService;
import model.tournament.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public DataController(TableView<MoveView> tableView) {

        System.out.println("Initialising main controller...");
        tournamentDatabase = new DatabaseConnection( "src\\Assets\\Tournament_Database.db" );
        gameDatabase = new DatabaseConnection( "src\\Assets\\Moves_Database.db" );
        this.tableView = tableView;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy/MM/dd" );
        LocalDate localDate = LocalDate.now();

        Games game = new Games( 0 , dtf.format( localDate ) , "Unknown" , "Unknown" , "N/A" );
        GamesService.save( game , gameDatabase );
        updateTable();
    }

    void updateTable(String move) {
        Moves saveItem = new Moves( getMoveId() + 1 , getGameId() , move );
        MovesService.save( saveItem , gameDatabase );
        allMoves.clear();
        MovesService.selectForTable( allMoves , gameDatabase , getGameId() );
        tableView.setItems( FXCollections.observableList( allMoves ) );
    }

    private int getGameId() {
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
        MovesService.selectForTable( allMoves , gameDatabase , getGameId() );
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


        Optional msgResult = dialogueBox("Would you like to open a game from a database or a PGN file",
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
            Optional msgResult2 = dialogueBox("Would you like to save the file to the database?",
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
                        saveSomething();
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

    private Optional dialogueBox (String displayText,String button1,String button2) {
        option1 = new ButtonType(button1,ButtonBar.ButtonData.NO);
        ButtonType option2 = new ButtonType(button2,ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,displayText,option1,option2,CLOSE);

        Node closeButton = alert.getDialogPane().lookupButton(CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        return alert.showAndWait();

    }

    public void saveSomething () {

    }

    public void exitPrompt () {
        Optional msgResult = dialogueBox(
                "Are you sure you want to exit?" ,
                "Yes","No"
        );


        if ( msgResult.isPresent() && msgResult.get() == option1 ) {
            Optional msg2Result = dialogueBox(
                    "Save current game?" ,
                    "Yes" , "No"
            );
            if ( msg2Result.isPresent() && msg2Result.get() == option1 ) {
                tournamentDatabase.disconnect();
                gameDatabase.disconnect();
                System.exit( 0 );
            } else {
                //delete current game id
                MovesService.deleteById( getGameId() , gameDatabase );
                GamesService.deleteById( getGameId() , gameDatabase );
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
        alert.setTitle("Information Dialog");
        alert.setContentText("This feature has not yet been implemented");
        alert.showAndWait();
    }

}
