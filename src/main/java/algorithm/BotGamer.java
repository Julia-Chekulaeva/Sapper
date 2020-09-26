package algorithm;

import game.Helper;
import gui.AppGUI;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BotGamer {

    List<Pair<Integer, Integer>> coordinates;

    private final int size;

    private final AppGUI parentApp;

    private final Integer[][] cells;

    public BotGamer(AppGUI parentApp) {
        this.parentApp = parentApp;
        size = parentApp.getBoardSize();
        coordinates = new ArrayList<>();
        cells = parentApp.gamer.board.openCells;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                coordinates.add(new Pair<>(i, j));
            }
        }
    }

    private int countCells(int i, int j, boolean isNull) {
        int res = 0;
        for (int x = Integer.max(i - 1, 0); x < Integer.min(i + 2, size); x++) {
            for (int y = Integer.max(j - 1, 0); y < Integer.min(j + 2, size); y++) {
                Integer c = cells[x][y];
                if ((isNull && c == null) || (c != null && c.equals(-1))) {
                    res++;
                }
            }
        }
        return res;
    }

    public void setGuarantedBombs() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] != null && cells[i][j] > 0) {
                    int nullCells = countCells(i, j, true);
                    if (nullCells == cells[i][j]) {
                        for (int x = Integer.max(i - 1, 0); x < Integer.min(i + 2, size); x++) {
                            for (int y = Integer.max(j - 1, 0); y < Integer.min(j + 2, size); y++) {
                                if (cells[x][y] == null) {
                                    cells[x][y] = -1;
                                    parentApp.getTile(x, y).setFlag(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean setGuaranteedSafeCells() {
        boolean isSomethingOpened = false;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] != null && cells[i][j] > 0) {
                    int flagCells = countCells(i, j, false);
                    if (flagCells == cells[i][j]) {
                        for (int x = Integer.max(i - 1, 0); x < Integer.min(i + 2, size); x++) {
                            for (int y = Integer.max(j - 1, 0); y < Integer.min(j + 2, size); y++) {
                                if (cells[x][y] == null) {
                                    parentApp.guess(x, y);
                                    isSomethingOpened = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSomethingOpened;
    }

    public boolean play(boolean fullAutoGame) {
        while (!parentApp.gameIsFinished()) {
            /*try {
                this.wait(100000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }*/
            setGuarantedBombs();
            parentApp.gamer.board.printProcess();
            if (!setGuaranteedSafeCells()) {
                if (!fullAutoGame) {
                    return !parentApp.gameIsFinished();
                }
                coordinates.removeIf(coord -> cells[coord.getKey()][coord.getValue()] != null);
                if (coordinates.size() == 0) { // ???
                    continue;
                }
                int index = (int) (Math.random() * coordinates.size());
                Pair<Integer, Integer> pair = coordinates.get(index);
                parentApp.guess(pair.getKey(), pair.getValue());
            }
            parentApp.gamer.board.printProcess();
        }
        return false;
    }
}
