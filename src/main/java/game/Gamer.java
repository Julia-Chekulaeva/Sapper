package game;

import javafx.util.Pair;

import java.util.Scanner;

public class Gamer {

    static Scanner in = new Scanner(System.in);

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

    public void play() {
        while (!(board.lostTheGame || board.winTheGame)) {
            int x = in.nextInt() - 1;
            int y = in.nextInt() - 1;
            guess(x, y);
        }
        if (board.lostTheGame) {
            System.out.println("Вы проиграли!");
        } else {
            System.out.println("Вы выиграли!");
        }
        System.out.println(String.format("Число попыток: %d", numOfGuesses));
    }

    public void guess(int x, int y) {
        board.guess(x, y);
        board.printAndAnalyseProcess();
        numOfGuesses++;
    }

    public int getSize() {
        return size;
    }

    public int getNumOfGuesses() {
        return numOfGuesses;
    }
}
