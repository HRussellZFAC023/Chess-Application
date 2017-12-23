package view;

import controller.ChessBoard;
import controller.DataController;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import model.MoveView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;


/*
TODO write PGN contents to database
TODO output the game to GUI
*/
public class Main extends Application {

    private static DataController controller;

    public static void main(String[] args) {

        controller = new DataController();
        launch(args);

    }


    @Override
    public void start(Stage stage) throws Exception {

        VBox root = new VBox();
        Scene scene = new Scene(root,1024,768);       //creates scene
        scene.getStylesheets().add("Assets/stylesheet.css");
        stage.setTitle("Chess Application");        //setting the title
        stage.getIcons().add(new Image("Assets/chess-33-xxl.png"));    //adds icon
        stage.setScene(scene);
        stage.setMinWidth(750);
        stage.setMinHeight(550);
        stage.show();
        stage.setOnCloseRequest(event -> controller.exitPrompt(event));

        /*Adding the main menu*/
        MenuBar myMenu = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem newGameButton = new MenuItem("New game");
        MenuItem saveButton = new MenuItem("Save");
        MenuItem openButton = new MenuItem("Open"); //when this is pressed offer user to load a pgn or load from database
        MenuItem setupButton = new MenuItem("Setup Position");
        MenuItem quitButton = new MenuItem("Quit");
        gameMenu.getItems().addAll(newGameButton,saveButton,openButton,setupButton,quitButton);

        Menu tournamentMenu = new Menu("Tournaments");
        MenuItem tournamentItem1 = new MenuItem("Generate Tournaments");
        MenuItem tournamentItem2 = new MenuItem("Load Tournaments");
        tournamentMenu.getItems().addAll(tournamentItem1,tournamentItem2);

        Menu trainingMenu = new Menu("Training");
        MenuItem trainingItem1 = new MenuItem("New puzzle");
        MenuItem trainingItem2 = new MenuItem("Create puzzle");
        trainingMenu.getItems().addAll(trainingItem1,trainingItem2);

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem1 = new MenuItem("Rules of chess");
        MenuItem aboutItem2 = new MenuItem("Credits");
        aboutMenu.getItems().addAll(aboutItem1,aboutItem2);

         /*TODO Add functionality to menu clicks*/
        newGameButton.setOnAction(e -> controller.doSomething());
        saveButton.setOnAction(e -> controller.saveSomething());
        openButton.setOnAction(e -> controller.openSomething());
        setupButton.setOnAction(e -> controller.doSomething());
        tournamentItem1.setOnAction(e -> controller.doSomething());
        tournamentItem2.setOnAction(e -> controller.doSomething());
        trainingItem1.setOnAction(e -> controller.doSomething());
        trainingItem2.setOnAction(e -> controller.doSomething());
        aboutItem1.setOnAction(e -> controller.doSomething());
        aboutItem2.setOnAction(e -> controller.doSomething());
        quitButton.setOnAction(e -> controller.exitPrompt());
        quitButton.setAccelerator(new KeyCodeCombination(KeyCode.Q,KeyCombination.CONTROL_DOWN));

        myMenu.getMenus().addAll(gameMenu,trainingMenu,aboutMenu);
        root.getChildren().add(myMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(borderPane);
        borderPane.setPadding(new Insets(40));

        ChessBoard chessBoard = new ChessBoard(true);
        chessBoard.setAlignment(Pos.CENTER);
        chessBoard.setSize(stage.getHeight() -200);
        stage.heightProperty().addListener((obs, oldVal, newVal) -> chessBoard.setSize(stage.getHeight()-200));     //dynamically re-sises chessboard
        borderPane.setCenter(chessBoard);

        VBox rightPane = new VBox();
        ObservableList<MoveView> moveViews = FXCollections.observableArrayList(
                new MoveView("e4","e5"),
                new MoveView("♘Nf3","d4")
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
        table.setSelectionModel(null);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setSpacing(10);
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");
        undoButton.getStyleClass().add("buttonx");
        redoButton.getStyleClass().add("buttonx");
        buttonPane.getChildren().addAll(undoButton,redoButton);
        rightPane.getChildren().addAll(table,buttonPane);
        borderPane.setRight(rightPane);
        BorderPane.setAlignment(rightPane,Pos.CENTER);

    }



}