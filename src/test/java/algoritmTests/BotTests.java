package algoritmTests;

import algorithm.BotGamer;
import game.Gamer;
import javafx.util.Pair;
import org.junit.Test;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class BotTests {

    @Test
    public void play() {
        int size = 15; int bombs = 17;
        int n = 1000;
        double percentOfLost = 0.2;
        int lostGames = 0;
        Random random = new Random();
        loop:
        for (int i = 0; i < n; i++) {
            Gamer gamer = new Gamer(size, bombs);
            gamer.start();
            BotGamer botGamer = new BotGamer(gamer);
            while (!botGamer.gameIsNotFinished()) {
                List<Pair<Integer, Integer>> coordinates = botGamer.getCoordinates();
                int index = random.nextInt(coordinates.size());
                int x = coordinates.get(index).getKey();
                int y = coordinates.get(index).getValue();
                gamer.guess(x, y);
                if (gamer.board.lostTheGame()) {
                    continue loop;
                }
                botGamer.play(false);
                assert (!gamer.board.lostTheGame());
            }
            botGamer.play(false);
            assert (!gamer.board.lostTheGame());
        }
        for (int i = 0; i < n; i++) {
            Gamer gamer = new Gamer(size, bombs);
            gamer.start();
            BotGamer botGamer = new BotGamer(gamer);
            botGamer.play(true);
            if (gamer.board.lostTheGame()) {
                lostGames++;
            }
        }
        System.out.print("Store (lost games - won games): ");
        System.out.print(lostGames);
        System.out.println(" - " + (n - lostGames));
        assert (((double) lostGames) / n < percentOfLost);
    }
}
