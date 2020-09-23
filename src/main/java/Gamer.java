import javafx.util.Pair;

import java.util.Scanner;

public class Gamer {

    static Scanner in = new Scanner(System.in);

    private int size = 4;

    private int numOfBombs = 1;

    int numOfGuesses;

    public void play() {
        Board board = new Board();
        board.set(size, numOfBombs);
        while (!(board.lostTheGame || board.winTheGame)) {
            Pair<Integer, Integer> pair = guess();
            board.guess(pair.getKey() - 1, pair.getValue() - 1);
            board.printAndAnalyseProcess();
            numOfGuesses++;
        }
        if (board.lostTheGame) {
            System.out.println("Вы проиграли!");
        } else {
            System.out.println("Вы выиграли!");
        }
        System.out.println(String.format("Число попыток: %d", numOfGuesses));
    }

    public Pair<Integer, Integer> guess() {
        Integer x = in.nextInt();
        Integer y = in.nextInt();
        return new Pair(x, y);
    }
}
