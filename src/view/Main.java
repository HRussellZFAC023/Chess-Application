package view;

import Controller.ChessBoard;
import Controller.DataController;
import model.MoveView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/*
TODO write PGN contents to database
TODO output the game to GUI
*/
public class Main extends Application {

    private static DataController controller;




    public static void main (String[] args) {

        controller = new DataController();
        launch(args);

    }


    @Override
    public void start (Stage stage) throws Exception {


        VBox root = new VBox();
        Scene scene = new Scene(root, 1024, 768);       //creates scene
        scene.getStylesheets().add("stylesheet.css");
        stage.setTitle("Chess Application");        //setting the title
        stage.getIcons().add(new Image("chess-33-xxl.png"));    //adds icon
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event-> controller.exitPrompt(event));

        /*Adding the main menu*/
        MenuBar myMenu = new MenuBar();
        Menu gameMenu = new Menu("game");
        MenuItem newGameButton = new MenuItem("New game");
        MenuItem saveButton = new MenuItem("Save");
        MenuItem openButton = new MenuItem("Open"); //when this is pressed offer user to load a pgn or load from database
        MenuItem setupButton = new MenuItem("Setup Position");
        MenuItem quitButton = new MenuItem("Quit (CTRL Q)");
        gameMenu.getItems().addAll(newGameButton, saveButton, openButton, setupButton, quitButton);

        Menu tournamentMenu = new Menu("Tournaments");
        MenuItem tournamentItem1 = new MenuItem("Generate Tournaments");
        MenuItem tournamentItem2 = new MenuItem("Load Tournaments");
        tournamentMenu.getItems().addAll(tournamentItem1, tournamentItem2);

        Menu trainingMenu = new Menu("Training");
        MenuItem trainingItem1 = new MenuItem("New puzzle");
        MenuItem trainingItem2 = new MenuItem("Create puzzle");
        trainingMenu.getItems().addAll(trainingItem1, trainingItem2);

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem1 = new MenuItem("Rules of chess");
        MenuItem aboutItem2 = new MenuItem("Credits");
        aboutMenu.getItems().addAll(aboutItem1, aboutItem2);

         /*TODO Add functionality to menu clicks*/
        newGameButton.setOnAction  (e -> controller.doSomething());
        saveButton.setOnAction   (e -> controller.saveSomething());
        openButton.setOnAction   (e -> controller.openSomething());
        setupButton.setOnAction    (e -> controller.doSomething());
        tournamentItem1.setOnAction(e -> controller.doSomething());
        tournamentItem2.setOnAction(e -> controller.doSomething());
        trainingItem1.setOnAction  (e -> controller.doSomething());
        trainingItem2.setOnAction  (e -> controller.doSomething());
        aboutItem1.setOnAction     (e -> controller.doSomething());
        aboutItem2.setOnAction     (e -> controller.doSomething());
        quitButton.setOnAction      (e -> controller.exitPrompt());
        quitButton.setAccelerator( new KeyCodeCombination (KeyCode.Q, KeyCombination.CONTROL_DOWN) );

        myMenu.getMenus().addAll(gameMenu, trainingMenu, aboutMenu);
        root.getChildren().add(myMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(borderPane);
        borderPane.setPadding(new Insets(40));

        GridPane centerPane = new ChessBoard();
        centerPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(centerPane);

        VBox rightPane = new VBox();
        ObservableList<MoveView> moveViews = FXCollections.observableArrayList(
                new MoveView("e4", "e5"),
                new MoveView("♘Nf3", "d4")
        );
        TableView table = new TableView<>();
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

    }
}