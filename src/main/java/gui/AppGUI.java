package gui;

import algorithm.BotGamer;
import game.Cell;
import game.Gamer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class AppGUI extends Application {

    private BotGamer botGamer;

    private static final double MAIN_BOARD_SIZE = 750.0;

    private boolean fullAutoGame = false;

    private boolean botGame = false;

    public static final ImagePattern FLAG = new ImagePattern(new Image("flag.png"));

    public static final ImagePattern FLAG_NOT_SELECTED = new ImagePattern(new Image("flag_not_selected.png"));

    public static final int DEFAULT_BOARD_SIZE = 10;

    private boolean setFlag = false;

    public static final int DEFAULT_NUM_OF_BOMBS = 15;

    String regex = "\\d+";

    Stage appStage = new Stage();

    private boolean gameIsFinished = false;

    private int boardSize = DEFAULT_BOARD_SIZE;

    public static double tileSize;

    private int numOfBombs = DEFAULT_NUM_OF_BOMBS;

    public Gamer gamer;

    private Group tileGroup = new Group();

    private Cell[][] cells;

    private double tileSize() {
        return MAIN_BOARD_SIZE / boardSize;
    }

    private Parent createContent() {
        tileSize = tileSize();
        gamer = new Gamer(boardSize, numOfBombs);
        gamer.start();
        cells = gamer.board.getCells();
        Pane root = new Pane();
        root.setPrefSize(MAIN_BOARD_SIZE, MAIN_BOARD_SIZE + 40);
        Rectangle flag = new Rectangle();
        flag.setWidth(20);
        flag.setHeight(20);
        flag.relocate(MAIN_BOARD_SIZE - 30, MAIN_BOARD_SIZE + 10);
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
        button.relocate(MAIN_BOARD_SIZE / 2.0 - 20, MAIN_BOARD_SIZE + 10);
        RadioButton autoRobot = new RadioButton("Full automatic bot");
        autoRobot.setSelected(true);
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
        TextField textField1 = new TextField(String.format("%d", DEFAULT_BOARD_SIZE));
        textField1.relocate(100.0, 0.0);
        textField1.setPrefSize(40.0, 15.0);
        Label label1 = new Label("Size of field:");
        label1.relocate(10, 0);
        TextField textField2 = new TextField(String.format("%d", DEFAULT_NUM_OF_BOMBS));
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
    }

    public void guess(int x, int y) {
        Tile selectedTile = getTile(x, y);
        if (selectedTile.isOpen()) {
            return;
        }
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

    public boolean gameIsNotFinished() {
        return !gameIsFinished;
    }

    public boolean gameIsFinished() {
        return gameIsFinished;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
