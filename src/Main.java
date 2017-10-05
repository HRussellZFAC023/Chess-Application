import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application {

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
        Button bottomButton1 = new Button("I am bottom.");
        bottomPane.getChildren().add(bottomButton1);
        Button bottomButton2 = new Button("I am bottom again.");
        bottomPane.getChildren().add(bottomButton2);
        borderPane.setBottom(bottomPane);
        bottomPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(bottomPane, Pos.BOTTOM_CENTER);

        GridPane centerPane = new GridPane();


        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Button chessSpace = new Button(Integer.toString(x) + ", " + Integer.toString(y));
                //chessSpace.setPrefSize(100, 100);
                centerPane.add(chessSpace, x, y);
                chessSpace.getStyleClass().add("chess-space");
                //if()
                chessSpace.getStylesheets().add("chess-space-light");
            }
        }

        borderPane.setCenter(centerPane);








        stage.setScene(scene);
        stage.show();

    }

    private Object doSomething(ActionEvent ae) {
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}