package gameTests;

import game.Cell;
import game.Gamer;
import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GamerTests {
    // Вид метода start довольно простой, и единственная ф-я, которую стоит тестировать,
    // - это ф-я set из класса Board, которая уже была протестирована.
    // Поэтому главная ф-я, которая тестируется в ф-и test, - это ф-я guess.
    // Тем не менее, для инициализации игры ф-я start нужна.

    private Gamer initGame(
            int size, int bombs,
            List<Pair<Integer, Integer>> list, boolean countingBombs
    ) {
        Gamer gamer = new Gamer(size, bombs);
        gamer.start();
        Cell[][] cells = gamer.board.getCells();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].hasBomb() == countingBombs) {
                    list.add(new Pair<>(i, j));
                }
            }
        }
        return gamer;
    }

    @Test
    public void test() {
        int size = 8; int bombs = 10;
        List<Pair<Integer, Integer>> neededCells = new ArrayList<>();
        Gamer gamer = initGame(size, bombs, neededCells, false);
        Random random = new Random();
        int countedNulls = bombs + 1;
        while (countedNulls > bombs) {
            countedNulls = 0;
            assertFalse(gamer.board.wonTheGame());
            int index = random.nextInt(neededCells.size());
            Pair<Integer, Integer> pair = neededCells.get(index);
            gamer.guess(pair.getKey(), pair.getValue());
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (gamer.board.openCells[i][j] == null)
                        countedNulls++;
                }
            }
            assertFalse(gamer.board.lostTheGame());
            neededCells.remove(index);
        }
        assertTrue(gamer.board.wonTheGame());
        neededCells.clear();
        gamer = initGame(size, bombs, neededCells, true);
        int index = random.nextInt(neededCells.size());
        Pair<Integer, Integer> pair = neededCells.get(index);
        gamer.guess(pair.getKey(), pair.getValue());
        assertFalse(gamer.board.wonTheGame());
        assertTrue(gamer.board.lostTheGame());
    }
}
