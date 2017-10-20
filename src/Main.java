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

    private final int rows = 8;    // not used
    private final int columns = 8;

    public Space[][] spaces = new Space[8][8];
    public Space activeSpace = null;

    @Override
    public void start(Stage stage) throws Exception {

        stage.getIcons().add(new Image("chess-33-xxl.png"));    //adds icon

        VBox root = new VBox();
        Scene scene = new Scene(root, 1024, 768);       //creates scene
        scene.getStylesheets().add("stylesheet.css");
        stage.setTitle("Chess Application");        //setting the title

        /*Adding the main menu*/
        MenuBar myMenu = new MenuBar();

        Menu gameMenu = new Menu("Game");
        MenuItem gameItem1 = new MenuItem("New Game");
        MenuItem gameItem2 = new MenuItem("Save");
        MenuItem gameItem3 = new MenuItem("Open");
        MenuItem gameItem4 = new MenuItem("Setup Position");
        MenuItem gameItem5 = new MenuItem("Quit");
        gameMenu.getItems().addAll(gameItem1, gameItem2, gameItem3, gameItem4, gameItem5);
        gameItem1.setOnAction((ActionEvent ae) -> doSomething(ae));

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



       GridPane centerPane = new GridPane();

        boolean light;
        boolean selected = false;
        int ascii = 65;

        /*drawing the chessboard*/
        for (int x = 1; x <= 8; x++) {
            for (int y = 0; y < 8; y++) {

                light = ((x + y) % 2 == 0 );
                spaces[x-1][y] = new Space(light,x,y);
                final int Xval = (x-1);
                final int Yval = y;
                spaces[x-1][(y)].setOnAction( e -> onSpaceClick(Xval, Yval) );
                {

            }
                centerPane.add(spaces[(x-1)][y], x, y);
            }
            //sets row labels
            Label xchar = new Label(Character.toString ((char) (ascii++)));
            xchar.getStyleClass().add("label-fill");
            centerPane.add(xchar,x,8);
            //sets column labels
            Label ynum = new Label((Integer.toString(x)));
            ynum.getStyleClass().add("label-fill");
            centerPane.add(ynum,0,(columns-x));
        }
        centerPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(centerPane);

        ObservableList<Moves> moves = FXCollections.observableArrayList(
                new Moves("e4", "e5"),
                new Moves("♘Nf3", "d4")
        );
        TableView table = new TableView<>();
        //table.setPrefSize(200, 750);            //done in stylesheet
        table.setItems(moves);


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

        VBox leftPane = new VBox();
        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.BASELINE_CENTER);
        buttonPane.setPadding(new Insets(10));
        buttonPane.setSpacing(10);
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");
        buttonPane.getChildren().addAll(undoButton, redoButton);
        leftPane.getChildren().addAll(table,buttonPane);
        borderPane.setRight(leftPane);

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


    private static void doSomething(ActionEvent ae) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("This feature has not yet been implemented");
        alert.showAndWait();
    }

    public void onSpaceClick(int x, int y){
        System.out.println("the x value is" + x);
        System.out.println("the y value is" + y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}