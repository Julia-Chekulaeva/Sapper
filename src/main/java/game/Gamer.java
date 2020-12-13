package game;

import javafx.scene.control.Label;

public class Gamer {

    private final int size;

    private final int numOfBombs;

    int numOfGuesses;

    public Board board;

    public Gamer(int size, int numOfBombs) {
        this.size = size;
        this.numOfBombs = numOfBombs;
    }

    public void start() {
        board = new Board();
        board.set(size, numOfBombs);
    }

    public void guess(int x, int y) {
        System.out.println(String.format("Guess: %d %d", x, y));
        board.guess(x, y);
        board.analyseProcess();
        board.printProcess();
        numOfGuesses++;
        if (board.lostTheGame()) {
            System.out.println("You lost the game!");
            System.out.println();
        }
        if (board.wonTheGame()) {
            System.out.println("You won the game!");
            System.out.println();
        }
    }

    public int getNumOfGuesses() {
        return numOfGuesses;
    }

    public int getNumOfBombs() {
        return numOfBombs;
    }

    public int getSize() {
        return size;
    }
}
