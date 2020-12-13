package gameTests;

import game.Board;
import game.Cell;
import javafx.util.Pair;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.Assert.*;

public class BoardTests {

    @Test
    public void setTest() {
        int size = 8; int bombs = 10;
        Board board = new Board();
        board.set(size, bombs);
        Cell[][] cells = board.getCells();
        Integer[][] openCells = board.openCells;
        assertEquals(size, cells.length);
        assertEquals(size, openCells.length);
        int newBombsCount = 0;
        for (int i = 0; i < size; i++) {
            assertEquals(size, cells[i].length);
            assertEquals(size, openCells[i].length);
            for (int j = 0; j < size; j++) {
                assertNull(openCells[i][j]);
                if (cells[i][j].hasBomb()) {
                    newBombsCount++;
                } else {
                    int neighborBombs = 0;
                    for (int x = max(0, i - 1); x < min(size, i + 2); x++) {
                        for (int y = max(0, j - 1); y < min(size, j + 2); y++) {
                            if (cells[x][y].hasBomb()) {
                                neighborBombs++;
                            }
                        }
                    }
                    assertEquals(neighborBombs, cells[i][j].getNeighbourBombs());
                }
            }
        }
        assertEquals(bombs, newBombsCount);
    }

    @Test
    public void guessTest() {
        int size = 8; int bombs = 10;
        Board board = new Board();
        board.set(size, bombs);
        List<Pair<Integer, Integer>> bombsPlacing = new ArrayList<>();
        Cell[][] cells = board.getCells();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].hasBomb()) {
                    bombsPlacing.add(new Pair<>(i, j));
                }
            }
        }
        Random random = new Random();
        Pair<Integer, Integer> coord =
                new Pair<>(random.nextInt(size), random.nextInt(size));
        while (bombsPlacing.contains(coord)) {
            coord = new Pair<>(random.nextInt(size), random.nextInt(size));
        }
        board.guess(coord.getKey(), coord.getValue());
        assertFalse(board.lostTheGame());
        int index = random.nextInt(bombs);
        coord = bombsPlacing.get(index);
        board.guess(coord.getKey(), coord.getValue());
        assertTrue(board.lostTheGame());
    }

    @Test
    public void analyseProcessTest() throws FileNotFoundException {
        int size = 5; int numOfBombs = 3;
        Integer[][] field = new Integer[8][8];
        BufferedReader reader = new BufferedReader(new FileReader("src/test/test resources/field for test"));
        List<String> rows = reader.lines().collect(Collectors.toList());
        for (int i = 0; i < size; i++) {
            String[] cells = rows.get(i).split(" ");
            for (int j = 0; j < size; j++) {
                if (!cells[j].equals("*"))
                    field[i][j] = Integer.parseInt(cells[j]);
            }
        }
        Board board = new Board();
        board.setCellsFromIntegerCells(field, size, numOfBombs);
        board.guess(0, 4);
        board.analyseProcess();
        assertFalse(board.wonTheGame());
        board.guess(1, 0);
        board.analyseProcess();
        assertFalse(board.wonTheGame());
        board.guess(4, 0);
        board.analyseProcess();
        assertFalse(board.wonTheGame());
        board.guess(4, 1);
        board.analyseProcess();
        assertFalse(board.wonTheGame());
        board.guess(0, 2);
        board.analyseProcess();
        assertTrue(board.wonTheGame());
    }
}
