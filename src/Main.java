import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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


        VBox root = new VBox();
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add("stylesheet.css");


        MenuBar myMenu = new MenuBar();

        Menu numbersMenu = new Menu("Numbers");
        MenuItem numbersItem1 = new MenuItem("One");
        MenuItem numbersItem2 = new MenuItem("Two");
        MenuItem numbersItem3 = new MenuItem("Three");
        numbersMenu.getItems().addAll(numbersItem1, numbersItem2, numbersItem3);

        Menu coloursMenu = new Menu("Colours");
        MenuItem coloursItem1 = new MenuItem("Red");
        MenuItem coloursItem2 = new MenuItem("Green");
        MenuItem coloursItem3 = new MenuItem("Blue");
        coloursMenu.getItems().addAll(coloursItem1, coloursItem2, coloursItem3);

        Menu shapesMenu = new Menu("Shapes");
        MenuItem shapesItem1 = new MenuItem("Triangle");
        MenuItem shapesItem2 = new MenuItem("Square");
        MenuItem shapesItem3 = new MenuItem("Circle");
        shapesMenu.getItems().addAll(shapesItem1, shapesItem2, shapesItem3);

        myMenu.getMenus().addAll(numbersMenu, coloursMenu, shapesMenu);
        root.getChildren().add(myMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(borderPane);
        borderPane.setPadding(new Insets(40, 40, 40, 40));

        stage.setTitle("Chess Application");
        Button myButton = new Button("Click me!");

        myButton.setOnAction((ActionEvent ae) -> doSomething(ae));

        borderPane.getChildren().add(myButton);

        GridPane leftPane = new GridPane();
        Button leftButton1 = new Button("I am left.");
        leftPane.getChildren().add(leftButton1);
        Button leftButton2 = new Button("I am left again.");
        leftPane.getChildren().add(leftButton2);
        borderPane.setLeft(leftPane);
        leftPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(leftPane, Pos.CENTER_LEFT);

        GridPane rightPane = new GridPane();
        Button rightButton1 = new Button("I am right.");
        rightPane.getChildren().add(rightButton1);
        Button rightButton2 = new Button("I am right again.");
        rightPane.getChildren().add(rightButton2);
        borderPane.setRight(rightPane);
        rightPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(rightPane, Pos.CENTER_RIGHT);

        GridPane topPane = new GridPane();
        Button topButton1 = new Button("I am top.");
        topPane.getChildren().add(topButton1);
        Button topButton2 = new Button("I am top again.");
        topPane.getChildren().add(topButton2);
        borderPane.setTop(topPane);
        topPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(topPane, Pos.TOP_CENTER);

        GridPane bottomPane = new GridPane();


        borderPane.setBottom(bottomPane);
        bottomPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(bottomPane, Pos.BOTTOM_CENTER);


        GridPane centerPane = new GridPane();

        boolean light;
        int ascii = 65;


        for (int x = 1; x <= 8; x++) {
            for (int y = 0; y < 8; y++) {
                Button chessSpace = new Button(/*Integer.toString(x) + ", " + Integer.toString(y)*/);
                chessSpace.getStyleClass().add("chess-space");
                chessSpace.setOnAction((ActionEvent ae) -> doSomething(ae));
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
        table.setPrefSize(150, 250);
        table.setItems(moves);

        TableColumn whiteMoves = new TableColumn<>("White");
        whiteMoves.setCellValueFactory(new PropertyValueFactory<>("White"));
        table.getColumns().add(whiteMoves);

        TableColumn blackMoves = new TableColumn<>("Black");
        blackMoves.setCellValueFactory(new PropertyValueFactory<>("Black"));
        table.getColumns().add(blackMoves);


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


    private Object doSomething(ActionEvent ae) {
        //chessSpace
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}