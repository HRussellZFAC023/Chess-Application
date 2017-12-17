package Controller;
import model.DatabaseConnection;
import model.game.Games;
import model.game.GamesService;
import model.Tournament.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.stage.WindowEvent;

public class DataController {

    private DatabaseConnection tournamentDatabase;
    private DatabaseConnection gameDatabase;
    private ButtonType option1;

    public DataController () {
        System.out.println("Initialising main controller...");
        tournamentDatabase = new DatabaseConnection("Tournament_Database.db");
        gameDatabase = new DatabaseConnection("Moves_Database.db");
    }

    public void testDb () {
        ArrayList<Enrollments> testList = new ArrayList<>();
        ArrayList<Pairings> testList2 = new ArrayList<>();
        ArrayList<Players> testList3 = new ArrayList<>();
        ArrayList<Tournaments> testList4 = new ArrayList<>();
        ArrayList<Games> testList5 = new ArrayList<>();
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
        Games game = new Games(0,"28/11/99","hello","world");
        GamesService.save(game,gameDatabase);

    }

    public void openSomething() {
        boolean local = true;
        String fileLocation;
        ArrayList<String> fileContents = new ArrayList<>();


        Optional msgResult = dialogueBox("Would you like to open a game from a database or a PGN file",
                "Local Database","PGN File");

        if ( msgResult.isPresent() && msgResult.get() != option1 ) local = false;

        if ( ! local ) {

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

            if ( msgResult2.isPresent() && msgResult2.get() == option1 ) {
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
        option1 = new ButtonType(button1,ButtonBar.ButtonData.YES);
        ButtonType option2 = new ButtonType(button2,ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,displayText,option1,option2);

        return alert.showAndWait();
    }

    public void saveSomething () {

    }

    public void exitPrompt () {
        Optional msgResult = dialogueBox(
                "Are you sure you want to exit?\nMake sure you have saved your game.",
                "Yes","No"
        );


        if ( msgResult.isPresent() && msgResult.get() == option1 ) {
            tournamentDatabase.disconnect ();
            gameDatabase.disconnect ();
            System.exit (0);
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
