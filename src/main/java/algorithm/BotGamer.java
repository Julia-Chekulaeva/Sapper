package algorithm;

import gui.AppGUI;
import javafx.util.Pair;

import java.util.*;

public class BotGamer {

    private final int MAX_ENDING_ATTEMPTS_COUNT = 10000;

    private int detectedBombs;

    List<Pair<Integer, Integer>> coordinates;

    Set<GroupCells> groups;

    private final int size;

    private final AppGUI parentApp;

    private final Integer[][] cells;

    private boolean guess(int x, int y) {
        parentApp.guess(x, y);
        return parentApp.gameIsFinished();
    }

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

    private void makeGroups() {
        groups = new HashSet<>();
        Set<Pair<Integer, Integer>> guaranteedBombs = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] != null && cells[i][j] > 0) {
                    int cell = cells[i][j];
                    Set<Pair<Integer, Integer>> coordinates = new HashSet<>();
                    for (int x = Integer.max(i - 1, 0); x < Integer.min(i + 2, size); x++) {
                        for (int y = Integer.max(j - 1, 0); y < Integer.min(j + 2, size); y++) {
                            if (cells[x][y] == null) {
                                coordinates.add(new Pair<>(x, y));
                                continue;
                            }
                            if (cells[x][y].equals(-1)) {
                                Pair<Integer, Integer> pair = new Pair<>(x, y);
                                coordinates.add(pair);
                                guaranteedBombs.add(pair);
                            }
                        }
                    }
                    groups.add(new GroupCells(coordinates, cell));
                }
            }
        }
        groups.add(new GroupCells(guaranteedBombs, guaranteedBombs.size()));
        Set<GroupCells> groupsToAdd = new HashSet<>();
        Set<GroupCells> lastGroups = new HashSet<>();
        GroupCells emptyGroup = new GroupCells(new HashSet<>(), 0);
        boolean noChanges = false;
        while (!noChanges) {
            groups.remove(emptyGroup);
            lastGroups.clear();
            lastGroups.addAll(groups);
            groups.clear();
            for (GroupCells group : lastGroups) {
                groups.addAll(group.split());
            }
            for (GroupCells group1 : groups) {
                for (GroupCells group2 : groups) {
                    groupsToAdd.add(group1.mergeOther(group2));
                }
            }
            groups.addAll(groupsToAdd);
            groupsToAdd.clear();
            noChanges = true;
            for (GroupCells group : groups) {
                noChanges = noChanges && lastGroups.contains(group);
            }
        }
    }

    private boolean setGuaranteed() {
        boolean res = false;
        for (GroupCells group : groups) {
            System.out.println(group.getBombsCount() + " " + group.getGroup());
            if (group.getBombsCount() == 0 && group.getGroup().size() != 0) {
                for (Pair<Integer, Integer> pair : group.getGroup()) {
                    if (guess(pair.getKey(), pair.getValue())) {
                        return true;
                    }
                }
                res = true;
            }
            if (group.getBombsCount() == group.getGroup().size()) {
                for (Pair<Integer, Integer> pair : group.getGroup()) {
                    int x = pair.getKey(); int y = pair.getValue();
                    if (cells[x][y] == null) {
                        detectedBombs++;
                        cells[x][y] = -1;
                        parentApp.getTile(x, y).setFlag(true);
                    }
                }
            }
        }
        return res;
    }

    private boolean checkingIfPossibleComb(int[] bombsPlacing, int[] bombsCountInAllCases) {
        for (int i : bombsPlacing) {
            int x = coordinates.get(i).getKey();
            int y = coordinates.get(i).getValue();
            cells[x][y] = -1;
        }
        boolean rightComb = true;
        loop:
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] != null && cells[i][j] != -1) {
                    int bombsCount = 0;
                    for (int x = Integer.max(i - 1, 0); x < Integer.min(i + 2, size); x++) {
                        for (int y = Integer.max(j - 1, 0); y < Integer.min(j + 2, size); y++) {
                            if (cells[x][y] != null && cells[x][y] == -1) {
                                bombsCount++;
                            }
                        }
                    }
                    if (bombsCount != cells[i][j]) {
                        rightComb = false;
                        break loop;
                    }
                }
            }
        }
        for (int i : bombsPlacing) {
            int x = coordinates.get(i).getKey();
            int y = coordinates.get(i).getValue();
            cells[x][y] = null;
            if (rightComb) {
                bombsCountInAllCases[i]++;
            }
        }
        return rightComb;
    }

    private boolean enumerationOfCombs(boolean fullAutoGame) {
        int closedBombs = parentApp.getNumOfBombs() - detectedBombs;
        int closedCells = coordinates.size();
        int min = Math.min(closedBombs, closedCells - closedBombs);
        double a = 1;
        double b = closedCells;
        for (int i = 2; i <= min; i++) {
            a *= i;
            b *= (closedCells - i + 1);
        }
        System.out.println("");
        if (b / a > MAX_ENDING_ATTEMPTS_COUNT) {
            return false;
        }
        int combsCount = 0;
        int[] bombsPlacing = new int[closedBombs];
        int[] bombInCellCases = new int[closedCells];
        int[] arrayForRandomise = new int[closedCells];
        for (int i = 0; i < closedBombs; i++) {
            bombsPlacing[i] = i;
        }
        int asserting = 0;
        while (true) {
            asserting++;
            if (checkingIfPossibleComb(bombsPlacing, bombInCellCases)) {
                combsCount++;
            }
            if (bombsPlacing[0] == closedCells - closedBombs) {
                break;
            }
            System.out.println(bombsPlacing[closedBombs - 1]);
            for (int i = 0; i < closedBombs; i++) {
                if (i != closedBombs - 1) {
                    assert (bombsPlacing[i] != closedCells - closedBombs + i);
                    if (bombsPlacing[i + 1] == closedCells - closedBombs + i + 1) {
                        bombsPlacing[i]++;
                        for (int j = i + 1; j < closedBombs; j++) {
                            bombsPlacing[j] = bombsPlacing[j - 1] + 1;
                        }
                        break;
                    }
                } else {
                    assert (bombsPlacing[i] < closedCells - 1);
                    bombsPlacing[i]++;
                }
            }
        }
        assert (b / a == asserting);
        boolean res = false;
        for (int i = 0; i < closedCells; i++) {
            int x = coordinates.get(i).getKey(); int y = coordinates.get(i).getValue();
            if (bombInCellCases[i] == combsCount) {
                detectedBombs++;
                cells[x][y] = -1;
                parentApp.getTile(x, y).setFlag(true);
            }
            if (bombInCellCases[i] == 0) {
                res = true;
                if (guess(x, y)) {
                    return true;
                }
            }
        }
        if (!res && fullAutoGame) {
            arrayForRandomise[0] = combsCount - bombInCellCases[0];
            for (int i = 1; i < closedCells; i++) {
                arrayForRandomise[i] = arrayForRandomise[i - 1] +
                        combsCount - bombInCellCases[i];
            }
            int rand = new Random().nextInt(arrayForRandomise[closedCells - 1]);
            for (int i = 0; i < closedCells; i++) {
                if (rand <= arrayForRandomise[i]) {
                    int x = coordinates.get(i).getKey();
                    int y = coordinates.get(i).getValue();
                    if (guess(x, y)) {
                        return true;
                    }
                }
            }
        }
        return res;
    }

    public boolean play(boolean fullAutoGame) {
        while (parentApp.gameIsNotFinished()) {
            coordinates.removeIf(coord -> cells[coord.getKey()][coord.getValue()] != null);
            System.out.println("Hi");
            makeGroups();
            for (GroupCells group : groups) {
                System.out.println(group.getBombsCount() + " " + group.getGroup());
            }
            parentApp.gamer.board.printProcess();
            if (!setGuaranteed()) {
                coordinates.removeIf(coord -> cells[coord.getKey()][coord.getValue()] != null);
                if (enumerationOfCombs(fullAutoGame)) {
                    continue;
                }
                coordinates.removeIf(coord -> cells[coord.getKey()][coord.getValue()] != null);
                if (!fullAutoGame) {
                    return parentApp.gameIsNotFinished();
                }
                if (coordinates.size() == 0) { // ???
                    continue;
                }
                int index = (int) (Math.random() * coordinates.size());
                Pair<Integer, Integer> pair = coordinates.get(index);
                guess(pair.getKey(), pair.getValue());
            }
            parentApp.gamer.board.printProcess();
        }
        return false;
    }
}
