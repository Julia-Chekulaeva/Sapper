package gui;

import com.sun.deploy.uitoolkit.impl.fx.ui.ErrorPane;
import game.Cell;
import game.Gamer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class AppGUI extends Application {

    public static final int DEFAULT_BOARD_SIZE = 8;

    public static final int DEFAULT_NUM_OF_BOMBS = 10;

    String regex = "\\d+";

    Stage appStage = new Stage();

    private boolean gameIsFinished = false;

    private int boardSize = DEFAULT_BOARD_SIZE;

    public static int tileSize;

    private int numOfBombs = DEFAULT_NUM_OF_BOMBS;

    private Gamer gamer;

    private Group tileGroup = new Group();

    private Cell[][] cells;

    private int tileSize() {
        return 800 / boardSize;
    }

    private Parent createContent() {
        tileSize = tileSize();
        gamer = new Gamer(boardSize, numOfBombs);
        gamer.start();
        cells = gamer.board.getCells();
        Pane root = new Pane();
        root.setPrefSize(boardSize * tileSize, boardSize * tileSize);
        root.getChildren().add(tileGroup);
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Tile tile = new Tile(cells[i][j].hasBomb(), cells[i][j].getNeighbourBombs(), i, j);
                tileGroup.getChildren().add(tile);
            }
        }
        return root;
    }

    private void end(Tile selectedTile) {
        Pane root = new Pane();
        String message;
        if (gamer.board.lostTheGame) {
            selectedTile.makeBoom();
            message = "Вы проиграли!";
        } else {
            message = "Вы выиграли!";
        }
        System.out.println(message);
        Label label1 = new Label(message);
        label1.relocate(10.0, 0.0);
        Label label2 = new Label(String.format("Число попыток: %d", gamer.getNumOfGuesses()));
        label2.relocate(10.0, 50.0);
        root.getChildren().addAll(label1, label2);
        root.setPrefSize(50.0, 100.0);
        System.out.println(String.format("Число попыток: %d", gamer.getNumOfGuesses()));
        appStage.setScene(new Scene(root));
        appStage.show();
    }

    private void initialise() {
        Pane root = new Pane();
        TextField textField1 = new TextField("8");
        textField1.relocate(100.0, 0.0);
        textField1.setPrefSize(40.0, 15.0);
        Label label1 = new Label("Size of field:");
        label1.relocate(10.0, 0.0);
        TextField textField2 = new TextField("10");
        textField2.relocate(100.0, 50.0);
        textField2.setPrefSize(40.0, 15.0);
        Label label2 = new Label("Bombs' count:");
        label2.relocate(10.0, 50.0);
        Button button = new Button("Start game");
        button.relocate(50.0, 100.0);
        button.setOnMouseClicked(event -> {
            if (Pattern.matches(regex, textField1.getText()) && Pattern.matches(regex, textField2.getText())) {
                boardSize = Integer.parseInt(textField1.getText());
                numOfBombs = Integer.parseInt(textField2.getText());
                if (boardSize * boardSize <= numOfBombs) {
                    boardSize = DEFAULT_BOARD_SIZE;
                    numOfBombs = DEFAULT_NUM_OF_BOMBS;
                } else {
                    appStage.close();
                }
            }
        });
        root.getChildren().add(textField1);
        root.getChildren().add(label1);
        root.getChildren().add(textField2);
        root.getChildren().add(label2);
        root.getChildren().add(button);
        root.setPrefSize(200.0, 140.0);
        Scene beginScene = new Scene(root);
        appStage.setScene(beginScene);
        appStage.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        initialise();
        primaryStage.setTitle("Sapper");
        Scene scene = new Scene(createContent());
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setOnMouseClicked(event -> {
            if (gameIsFinished) {
                return;
            }
            int x = (int) (event.getSceneX() / tileSize);
            int y = (int) (event.getSceneY() / tileSize);
            Tile selectedTile = (Tile) tileGroup.getChildren().get(x * boardSize + y);
            if (selectedTile.isOpen()) {
                return;
            }
            gamer.guess(x, y);
            gameIsFinished = gamer.board.lostTheGame || gamer.board.winTheGame;
            for (Node node : tileGroup.getChildren()) {
                Tile tile = (Tile) node;
                int i = tile.x;
                int j = tile.y;
                if ((cells[i][j].isOpen() || gameIsFinished) && !tile.isOpen()) {
                    tile.setOpen(true);
                    tile.openImage();
                }
            }
            if (gameIsFinished) {
                end(selectedTile);
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
