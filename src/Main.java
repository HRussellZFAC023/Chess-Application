import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;


public class Main extends Application {

    private final int rows = 8;
    private final int columns = 8;

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("chess-33-xxl.png"));

        VBox root = new VBox();
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add("stylesheet.css");


        MenuBar myMenu = new MenuBar();

        Menu gameMenu = new Menu("Game");
        MenuItem gameItem1 = new MenuItem("New Game");
        MenuItem gameItem2 = new MenuItem("Save");
        MenuItem gameItem3 = new MenuItem("Open");
        MenuItem gameItem4 = new MenuItem("Setup Position");
        MenuItem gameItem5 = new MenuItem("Quit");
        gameMenu.getItems().addAll(gameItem1, gameItem2, gameItem3, gameItem4, gameItem5);

        Menu tournamentMenu = new Menu("Tournament");
        MenuItem tournamentItem1 = new MenuItem("Generate Tournament");
        MenuItem tournamentItem2 = new MenuItem("Load Tournament");
        tournamentMenu.getItems().addAll(tournamentItem1, tournamentItem2);

        Menu trainingMenu = new Menu("Training");
        MenuItem trainingItem1 = new MenuItem("New puzzle");
        MenuItem trainingItem2 = new MenuItem("Create puzzle");
        trainingMenu.getItems().addAll(trainingItem1, trainingItem2 );

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem1 = new MenuItem("Rules of chess");
        MenuItem aboutItem2 = new MenuItem("Credits");
        aboutMenu.getItems().addAll(aboutItem1, aboutItem2 );

        myMenu.getMenus().addAll(gameMenu, trainingMenu, aboutMenu);
        root.getChildren().add(myMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(borderPane);
        borderPane.setPadding(new Insets(40));

        stage.setTitle("Chess Application");





       VBox rightPane = new VBox();
        Button rightButton1 = new Button("I am right.");
        rightPane.getChildren().add(rightButton1);
        Button rightButton2 = new Button("I am right again.");
        rightPane.getChildren().add(rightButton2);
        borderPane.setRight(rightPane);
        rightPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(rightPane, Pos.CENTER_RIGHT);

        GridPane centerPane = new GridPane();

        boolean light;
        boolean selected = false;
        int ascii = 65;


        for (int x = 1; x <= 8; x++) {
            for (int y = 0; y < 8; y++) {
                Button chessSpace = new Button(/*Integer.toString(x) + ", " + Integer.toString(y)*/);
                chessSpace.getStyleClass().add("chess-space");
                chessSpace.setOnAction((ActionEvent ae) -> doSomething(ae,selected));
                light = ((x + y) % 2 == 0 );
                if(light) {
                    chessSpace.getStyleClass().add("chess-space-light");
                }else{
                    chessSpace.getStyleClass().add("chess-space-dark");
            }
                centerPane.add(chessSpace, x, y);
            }
            //sets row labels
            Label xchar = new Label(Character.toString ((char) (ascii++)));
            centerPane.add(xchar,x,8);
            //sets column labels
            Label ynum = new Label((Integer.toString(x)));
            centerPane.add(ynum,0,(columns-x));
        }
        borderPane.setCenter(centerPane);

        ObservableList<Moves> moves = FXCollections.observableArrayList(
                new Moves("e4", "e5"),
                new Moves("d4", "e5xd4")
        );
        TableView table = new TableView<>();
        table.setPrefSize(200, 200);
        table.setItems(moves);


        TableColumn whiteMoves = new TableColumn<>("White");
        whiteMoves.setCellValueFactory(new PropertyValueFactory<>("White"));
        table.getColumns().add(whiteMoves);
        whiteMoves.setSortable(false);

        TableColumn blackMoves = new TableColumn<>("Black");
        blackMoves.setCellValueFactory(new PropertyValueFactory<>("Black"));
        table.getColumns().add(blackMoves);
        blackMoves.setSortable(false);

        whiteMoves.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        blackMoves.prefWidthProperty().bind(table.widthProperty().multiply(0.5));

        whiteMoves.setResizable(false);
        blackMoves.setResizable(false);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);




        borderPane.setRight(table);



        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((WindowEvent we) -> exitPrompt(we));


    }


    private void exitPrompt(WindowEvent we) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to exit?\nMake sure you have saved your game.");

        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.exit(0);
        } else {
            we.consume();
        }
    }


    private Object doSomething(ActionEvent ae, boolean selected) {
        return(!selected);
    }

    public static void main(String[] args) {
        launch(args);
    }
}