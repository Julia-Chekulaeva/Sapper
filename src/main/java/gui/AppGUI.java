package gui;

import algorithm.BotGamer;
import game.Cell;
import game.Gamer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

//import javax.swing.*;
import java.util.regex.Pattern;

public class AppGUI extends Application {

    private BotGamer botGamer;

    private boolean fullAutoGame = false;

    private boolean botGame = false;

    public static final ImagePattern FLAG = new ImagePattern(new Image("flag.png"));

    public static final ImagePattern FLAG_NOT_SELECTED = new ImagePattern(new Image("flag_not_selected.png"));

    public static final int DEFAULT_BOARD_SIZE = 8;

    private boolean setFlag = false;

    public static final int DEFAULT_NUM_OF_BOMBS = 10;

    String regex = "\\d+";

    Stage appStage = new Stage();

    private boolean gameIsFinished = false;

    private int boardSize = DEFAULT_BOARD_SIZE;

    public static int tileSize;

    private int numOfBombs = DEFAULT_NUM_OF_BOMBS;

    public Gamer gamer;

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
        root.setPrefSize(boardSize * tileSize, boardSize * tileSize + 40);
        Rectangle flag = new Rectangle();
        flag.setWidth(20);
        flag.setHeight(20);
        flag.relocate(boardSize * tileSize - 30, boardSize * tileSize + 10);
        flag.setOnMouseClicked(event -> {
            setFlag = !setFlag;
            if (setFlag) {
                flag.setFill(FLAG);
            } else {
                flag.setFill(FLAG_NOT_SELECTED);
            }
        });
        flag.setFill(FLAG_NOT_SELECTED);
        Button button = new Button("Solve by robot");
        button.relocate(350, boardSize * tileSize + 10);
        RadioButton autoRobot = new RadioButton("Full automatic bot");
        autoRobot.setSelected(false);
        autoRobot.relocate(10, boardSize * tileSize + 10);
        button.setOnMouseClicked(event -> {
            botGame = true;
            fullAutoGame = autoRobot.isSelected();
            if (botGamer.play(fullAutoGame)) {
                botGame = false;
            }
        });
        root.getChildren().addAll(tileGroup, flag, button, autoRobot);
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
        if (gamer.board.lostTheGame()) {
            selectedTile.makeBoom();
            message = "You lost the game!";
        } else {
            message = "You won the game!";
        }
        Label label1 = new Label(message);
        label1.relocate(10.0, 0.0);
        Label label2 = new Label(String.format("Attempts' count: %d", gamer.getNumOfGuesses()));
        label2.relocate(10.0, 50.0);
        root.getChildren().addAll(label1, label2);
        root.setPrefSize(50.0, 100.0);
        appStage.setScene(new Scene(root));
        appStage.show();
    }

    private void initialise() {
        appStage.getIcons().add(Tile.IMAGE_BOMB);
        Pane root = new Pane();
        TextField textField1 = new TextField("8");
        textField1.relocate(100.0, 0.0);
        textField1.setPrefSize(40.0, 15.0);
        Label label1 = new Label("Size of field:");
        label1.relocate(10, 0);
        TextField textField2 = new TextField("10");
        textField2.relocate(100, 30);
        textField2.setPrefSize(40, 15);
        Label label2 = new Label("Bombs' count:");
        label2.relocate(10, 30);
        Button button = new Button("OK");
        button.relocate(50, 60);
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
        root.getChildren().addAll(textField1, label1, textField2, label2, button);
        root.setPrefSize(200, 100);
        Scene beginScene = new Scene(root);
        appStage.setScene(beginScene);
        appStage.showAndWait();
    }

    private void askAboutRobot() {
        RadioButton button1 = new RadioButton("You");
        RadioButton button2 = new RadioButton("Robot");
        button1.relocate(10, 20);
        button1.setSelected(true);
        button2.relocate(60, 20);
        button1.setOnMouseClicked(e -> {
            button2.setSelected(!button1.isSelected());
        });
        button2.setOnMouseClicked(e -> {
            button1.setSelected(!button2.isSelected());
        });
        Label label3 = new Label("Who is playing:");
        label3.relocate(10, 0);
        Button button = new Button("OK");
        button.relocate(50, 50);
        button.setOnMouseClicked(event -> {
            botGame = button2.isSelected();
            appStage.close();
        });
        Pane root = new Pane();
        root.setPrefSize(200, 100);
        root.getChildren().addAll(button, button1, button2, label3);
        appStage.setScene(new Scene(root));
        appStage.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(Tile.IMAGE_BOMB);
        initialise();
        primaryStage.setTitle("Sapper");
        Scene scene = new Scene(createContent());
        botGamer = new BotGamer(this);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setOnMouseClicked(event -> {
            if (gameIsFinished) {
                return;
            }
            if (botGame) {
                return;
            }
            int x = (int) (event.getSceneX() / tileSize);
            int y = (int) (event.getSceneY() / tileSize);
            if (y >= boardSize) {
                return;
            }
            Tile selectedTile = getTile(x, y);
            if (selectedTile.isOpen()) {
                return;
            }
            if (setFlag) {
                selectedTile.setFlag(!selectedTile.hasFlag());
                return;
            }
            if (selectedTile.hasFlag()) {
                return;
            }
            if (y == boardSize) {
                return;
            }
            guess(x, y);
        });
        /*askAboutRobot();
        if (botGame) {
            botGamer.play();
        }*/
    }

    public void guess(int x, int y) {
        Tile selectedTile = getTile(x, y);
        gamer.guess(x, y);
        gameIsFinished = gamer.board.lostTheGame() || gamer.board.winTheGame();
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
    }

    public Tile getTile(int x, int y) {
        return (Tile) tileGroup.getChildren().get(x * boardSize + y);
    }

    public int getBoardSize() {
        return boardSize;
    }

    public boolean gameIsFinished() {
        return gameIsFinished;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
