package algorithm;

import gui.AppGUI;
import javafx.util.Pair;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BotGamer {

    List<Pair<Integer, Integer>> coordinates;

    Set<GroupCells> groups;

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
                    parentApp.guess(pair.getKey(), pair.getValue());
                    if (parentApp.gameIsFinished()) {
                        return true;
                    }
                }
                res = true;
            }
            if (group.getBombsCount() == group.getGroup().size()) {
                for (Pair<Integer, Integer> pair : group.getGroup()) {
                    int x = pair.getKey(); int y = pair.getValue();
                    if (cells[x][y] == null) {
                        cells[x][y] = -1;
                        parentApp.getTile(x, y).setFlag(true);
                    }
                }
            }
        }
        return res;
    }

    public boolean play(boolean fullAutoGame) {
        while (parentApp.gameIsNotFinished()) {
            makeGroups();
            for (GroupCells group : groups) {
                System.out.println(group.getBombsCount() + " " + group.getGroup());
            }
            parentApp.gamer.board.printProcess();
            if (!setGuaranteed()) {
                if (!fullAutoGame) {
                    return parentApp.gameIsNotFinished();
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
