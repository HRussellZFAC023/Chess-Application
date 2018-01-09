package view;

import controller.ChessBoard;
import controller.DataController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.MoveView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.awt.Desktop.getDesktop;
import static java.awt.Desktop.isDesktopSupported;


/*
TODO write PGN contents to database
TODO output the game to GUI
*/
public class Main extends Application {

    private static DataController controller;
    private static TableView<MoveView> tableView = new TableView<>();
    private static ChessBoard chessBoard;


    public static void main(String[] args) {

        controller = new DataController( tableView/*,chessBoard*/ );
        launch(args);

    }


    @Override
    public void start(Stage stage) throws Exception {

        VBox root = new VBox();
        Scene scene = new Scene(root,1024,768);       //creates scene
        scene.getStylesheets().add("assets/stylesheet.css");
        stage.setTitle("Chess Application");        //setting the title
        stage.getIcons().add(new Image("assets/chess-33-xxl.png"));    //adds icon
        stage.setScene(scene);
        stage.setMinWidth(750);
        stage.setMinHeight(550);
        stage.show();
        stage.setOnCloseRequest(event -> controller.exitPrompt(event));

        /*Adding the main menu*/
        MenuBar myMenu = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem newGameButton = new MenuItem("New game");
        MenuItem saveButton = new MenuItem( "Save As" );
        MenuItem importButton = new MenuItem("Import");
        MenuItem openButton = new MenuItem("Open");
        MenuItem quitButton = new MenuItem("Quit");
        gameMenu.getItems().addAll( newGameButton , saveButton , openButton, importButton , quitButton );

        Menu tournamentMenu = new Menu("Tournaments");
        MenuItem tournamentItem1 = new MenuItem( "Generate Tournament" );
        MenuItem tournamentItem2 = new MenuItem("Load Tournaments");
        tournamentMenu.getItems().addAll(tournamentItem1,tournamentItem2);

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem1 = new MenuItem("Rules of chess");
        MenuItem aboutItem2 = new MenuItem("Credits");
        aboutMenu.getItems().addAll(aboutItem1,aboutItem2);

        myMenu.getMenus().addAll( gameMenu , tournamentMenu , aboutMenu );
        root.getChildren().add(myMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(borderPane);
        borderPane.setPadding(new Insets(40));

        chessBoard = new ChessBoard( true , controller );
        chessBoard.setAlignment(Pos.CENTER);
        chessBoard.setSize(stage.getHeight() -200);
        stage.heightProperty().addListener((obs, oldVal, newVal) -> chessBoard.setSize(stage.getHeight()-200));     //dynamically re-sises chessboard
        borderPane.setCenter(chessBoard);

        VBox rightPane = new VBox();
        tableView.setPrefHeight( 1000 );
        TableColumn<MoveView, String> whiteMoves = new TableColumn<>( "White" );
        whiteMoves.setCellValueFactory( new PropertyValueFactory<>( "white" ) );
        whiteMoves.setSortable(false);
        tableView.getColumns().add( whiteMoves );

        TableColumn<MoveView, String> blackMoves = new TableColumn<>( "Black" );
        blackMoves.setCellValueFactory( new PropertyValueFactory<>( "black" ) );
        blackMoves.setSortable(false);
        tableView.getColumns().add( blackMoves );
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tableView.setSelectionModel( null );

        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setSpacing(10);
        Button undoButton = new Button("Undo");

        undoButton.getStyleClass().add("buttonx");
        Button result = new Button( "Result" );
        result.getStyleClass().add( "buttonx" );
        buttonPane.getChildren().addAll( undoButton , result );
        rightPane.getChildren().addAll( tableView , buttonPane );
        borderPane.setRight(rightPane);
        BorderPane.setAlignment(rightPane,Pos.CENTER);

        //action events
        newGameButton.setOnAction( e -> {
            if ( controller.newGame() ) {//Resets Chessboard after asking if user would like to save
                controller.resetGame();
                controller.updateTable();
                chessBoard.removeAllPieces();
                chessBoard.defineStartPositions();
            }
        } );
        newGameButton.setAccelerator( new KeyCodeCombination( KeyCode.N , KeyCombination.CONTROL_DOWN ) );
        saveButton.setOnAction( e -> controller.saveAsPGN() );
        saveButton.setAccelerator( new KeyCodeCombination( KeyCode.S , KeyCombination.CONTROL_DOWN ) );
        importButton.setOnAction( e -> {
            if ( controller.newGame() ) {//Resets Chessboard after asking if user would like to save
                chessBoard.removeAllPieces();
                chessBoard.defineStartPositions();
                controller.resetGame();
                controller.openPgn();
                controller.updateTable();
                chessBoard.load();}
        } );
        importButton.setAccelerator( new KeyCodeCombination( KeyCode.I , KeyCombination.CONTROL_DOWN ) );
        openButton.setOnAction(e ->{
        if ( controller.newGame() ) {//Resets Chessboard after asking if user would like to save
            chessBoard.removeAllPieces();
            chessBoard.defineStartPositions();
            controller.resetGame();
            controller.openFromDb();
            controller.updateTable();
            chessBoard.load();}
        });
        openButton.setAccelerator( new KeyCodeCombination( KeyCode.O , KeyCombination.CONTROL_DOWN ) );


        quitButton.setOnAction( e -> controller.exitPrompt() );
        quitButton.setAccelerator( new KeyCodeCombination( KeyCode.Q , KeyCombination.CONTROL_DOWN ) );
        tournamentItem1.setOnAction( e -> controller.doSomething() );
        tournamentItem2.setOnAction( e -> controller.doSomething() );
        aboutItem1.setOnAction( e -> {
            if ( isDesktopSupported() ) {
                try {
                    getDesktop().browse( new URI( "http://www.chessvariants.com/d.chess/chess.html" ) );
                } catch ( IOException | URISyntaxException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        aboutItem2.setOnAction( e -> controller.aboutMessage() );
        undoButton.setOnAction( e -> chessBoard.undo() );
        result.setOnAction( e -> controller.inputNameAndResult() );

    }


}