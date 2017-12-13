import Model.DatabaseConnection;
import Model.Game.*;
import Model.MoveView;
import Model.Tournament.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;


/*
TODO write PGN contents to database
TODO output the game to GUI
*/
public class Main extends Application {

    private static ButtonType option1;
    private Space[][] spaces = new Space[8][8];
    private int moveId;

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseConnection tournamentDatabase = new DatabaseConnection("Tournament_Database.db");
        DatabaseConnection gameDatabase = new DatabaseConnection("Moves_Database.db");

        stage.getIcons().add(new Image("chess-33-xxl.png"));    //adds icon

        VBox root = new VBox();
        Scene scene = new Scene(root, 1024, 768);       //creates scene
        scene.getStylesheets().add("stylesheet.css");
        stage.setTitle("Chess Application");        //setting the title


        /*Adding the main menu*/
        MenuBar myMenu = new MenuBar();

        Menu gameMenu = new Menu("Game");
        MenuItem newGameButton = new MenuItem("New Game");
        MenuItem saveButton = new MenuItem("Save");
        MenuItem openButton = new MenuItem("Open"); //when this is pressed offer user to load a pgn or load from database
        MenuItem setupButton = new MenuItem("Setup Position");
        MenuItem quitButton = new MenuItem("Quit");
        gameMenu.getItems().addAll(newGameButton, saveButton, openButton, setupButton, quitButton);

         /*TODO Add functionality to menu clicks*/
        newGameButton.setOnAction(Main::doSomething);
        saveButton.setOnAction(Main::saveSomething);

        openButton.setOnAction((ActionEvent ae) -> {
            try {
                openSomething(ae);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        setupButton.setOnAction(Main::doSomething);
        quitButton.setOnAction((ActionEvent ae) -> exitPrompt(null));

        Menu tournamentMenu = new Menu("Tournaments");
        MenuItem tournamentItem1 = new MenuItem("Generate Tournaments");
        MenuItem tournamentItem2 = new MenuItem("Load Tournaments");
        tournamentMenu.getItems().addAll(tournamentItem1, tournamentItem2);

        tournamentItem1.setOnAction(Main::doSomething);
        tournamentItem2.setOnAction(Main::doSomething);


        Menu trainingMenu = new Menu("Training");
        MenuItem trainingItem1 = new MenuItem("New puzzle");
        MenuItem trainingItem2 = new MenuItem("Create puzzle");
        trainingMenu.getItems().addAll(trainingItem1, trainingItem2);

        trainingItem1.setOnAction(Main::doSomething);
        trainingItem2.setOnAction(Main::doSomething);

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem1 = new MenuItem("Rules of chess");
        MenuItem aboutItem2 = new MenuItem("Credits");
        aboutMenu.getItems().addAll(aboutItem1, aboutItem2);

        aboutItem1.setOnAction(Main::doSomething);
        aboutItem2.setOnAction(Main::doSomething);

        myMenu.getMenus().addAll(gameMenu, trainingMenu, aboutMenu);
        root.getChildren().add(myMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(borderPane);
        borderPane.setPadding(new Insets(40));


        GridPane centerPane = new GridPane();

        boolean light;
        boolean selected = false;
        int ascii = 65;

        /*drawing the chessboard*/
        for (int x = 1; x <= 8; x++) {
            for (int y = 0; y < 8; y++) {

                light = ((x + y) % 2 == 0);
                spaces[x - 1][y] = new Space(light, x, y);
                final int Xval = (x - 1);
                final int Yval = y;
                spaces[x - 1][(y)].setOnAction(e -> onSpaceClick(Xval, Yval));
                {

                }
                centerPane.add(spaces[(x - 1)][y], x, y);
            }
            //sets row labels
            Label xchar = new Label(Character.toString((char) (ascii++)));
            xchar.getStyleClass().add("label-fill");
            centerPane.add(xchar, x, 8);
            //sets column labels
            Label ynum = new Label((Integer.toString(x)));
            ynum.getStyleClass().add("label-fill");
            int columns = 8;
            centerPane.add(ynum, 0, (columns - x));
        }
        centerPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(centerPane);

        ObservableList<MoveView> moveViews = FXCollections.observableArrayList(
                new MoveView("e4", "e5"),
                new MoveView("♘Nf3", "d4")
        );
        TableView table = new TableView<>();
        //table.setPrefSize(200, 750);            //done in stylesheet
        table.setItems(moveViews);


        TableColumn whiteMoves = new TableColumn<>("White");
        whiteMoves.setCellValueFactory(new PropertyValueFactory<>("White"));
        table.getColumns().add(whiteMoves);
        whiteMoves.setSortable(false);

        TableColumn blackMoves = new TableColumn<>("Black");
        blackMoves.setCellValueFactory(new PropertyValueFactory<>("Black"));
        table.getColumns().add(blackMoves);
        blackMoves.setSortable(false);

        table.getStyleClass().add("table-view");
        table.setSelectionModel(null);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox rightPane = new VBox();
        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setSpacing(10);
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");
        buttonPane.getChildren().addAll(undoButton, redoButton);
        rightPane.getChildren().addAll(table, buttonPane);
        rightPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(rightPane, Pos.CENTER_RIGHT);


        borderPane.setRight(rightPane);

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(this::exitPrompt);


//test code

        ArrayList<Enrollments> testList = new ArrayList<>();
        ArrayList<Pairings> testList2 = new ArrayList<>();
        ArrayList<Players> testList3 = new ArrayList<>();
        ArrayList<Tournaments> testList4 = new ArrayList<>();
        ArrayList<Games> testList5 = new ArrayList<>();



        EnrollmentsService.selectAll(testList, tournamentDatabase);
        PairingsService.selectAll(testList2, tournamentDatabase);
        PlayersService.selectAll(testList3, tournamentDatabase);
        TournamentsService.selectAll(testList4, tournamentDatabase);
        GamesService.selectAll(testList5, gameDatabase);

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
        for (Games c : testList5){
            System.out.println(c);
        }
        //how to save temp
       Games game = new Games( 0,"28/11/99", "hello", "world");
       GamesService.save(game, gameDatabase);
    }

    private static void saveSomething(ActionEvent ae) {

    }


    private static void openSomething(ActionEvent ae) throws IOException {
        boolean local = true;
        String fileLocation;
        ArrayList<String> fileContents = new ArrayList<>();


        Optional msgResult = dialogueBox("Would you like to open a game from a database or a PGN file",
                "Local Database","PGN File");

        if (msgResult.isPresent() && msgResult.get() != option1) local = false;

        if (!local) {

            FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            fileLocation = dialog.getFile();
            System.out.println(fileLocation + " chosen.");

            try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
                String line = br.readLine();
                while (line != null) {
                    fileContents.add(line);
                    line = br.readLine();
                }
            }

            System.out.println(fileContents);
            Optional msgResult2 = dialogueBox("Would you like to save the file to the database?" ,
                    "yes","no");

            if (msgResult2.isPresent() && msgResult2.get() == option1) {
                boolean foundStart = false;
                String date = "";
                String w = "";
                String b = "";
                String gameResult = "";

                for (String line : fileContents) {
                    if(line.contains("Date"))
                        date = extract(line, foundStart);
                    else if(line.contains("White")){
                        w = extract(line, foundStart);
                    }
                    else if(line.contains("Black")){
                        b = extract(line, foundStart);
                    }
                    else if(line.contains("Result")){
                        gameResult = extract(line, foundStart);
                    }
                    else if (line.trim().equals(""))
                        foundStart = true;
                    else if (foundStart) {


                        //Moves move = new Moves();
                        System.out.println(line);
                        saveSomething(null);
                    }

                }
                System.out.println(date);
                System.out.println(w);
                System.out.println(b);
                System.out.println(gameResult);


            }

        }else {/*TODO get user to select game from database */}

    }
    private static String extract(String line, boolean foundStart) {
        StringBuilder sb = new StringBuilder();
        for (char c: line.toCharArray()) {
            if (c == '"') foundStart = !foundStart;
            if (foundStart && c != '"')
                sb.append(c);
        }
        return String.valueOf(sb);
    }



    private static Optional dialogueBox (String displayText, String button1, String button2){
        option1 = new ButtonType(button1, ButtonBar.ButtonData.YES);
        ButtonType option2 = new ButtonType(button2, ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, displayText, option1, option2);

        return alert.showAndWait();
    }

    //write game opened to database

    private void exitPrompt(WindowEvent we) {
         Optional msgResult = dialogueBox(
                "Are you sure you want to exit?\nMake sure you have saved your game.",
        "Yes","No"
        );

        if (msgResult.isPresent() && msgResult.get() == option1) System.exit(0);
        we.consume();
    }


    private static void doSomething(ActionEvent ae) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText("This feature has not yet been implemented");
        alert.showAndWait();
    }

    private void onSpaceClick(int x, int y){
        System.out.println("the x value is" + x);
        System.out.println("the y value is" + y);

    }

    public static void main(String[] args) {
        launch(args);

    }
}