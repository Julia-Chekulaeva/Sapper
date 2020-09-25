package game;

import java.util.Scanner;

public class Game {

    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        int size = in.nextInt();
        int numOfBombs = in.nextInt();
        Gamer gamer = new Gamer(size, numOfBombs);
        gamer.start();
        gamer.play();
    }
}
