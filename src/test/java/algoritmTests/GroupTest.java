package algoritmTests;

import algorithm.GroupCells;
import javafx.util.Pair;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GroupTest {

    @Test
    public void mergeTest() {
        Set<Pair<Integer, Integer>> group1 = new HashSet<>();
        group1.add(new Pair<>(2, 3));
        group1.add(new Pair<>(3, 3));
        group1.add(new Pair<>(3, 5));
        GroupCells groupCells1 = new GroupCells(group1, 2);
        Set<Pair<Integer, Integer>> group2 = new HashSet<>();
        group2.add(new Pair<>(2, 3));
        group2.add(new Pair<>(3, 3));
        GroupCells groupCells2 = new GroupCells(group2, 1);
        Set<Pair<Integer, Integer>> group3 = new HashSet<>();
        group3.add(new Pair<>(2, 2));
        group3.add(new Pair<>(2, 3));
        group3.add(new Pair<>(3, 3));
        GroupCells groupCells3 = new GroupCells(group3, 1);
        GroupCells groupCellsEmpty = new GroupCells(
                new HashSet<>(), 0
        );
        GroupCells groupCells2_1 = groupCells2.mergeOther(
                new GroupCells(new HashSet<>(groupCells1.getGroup()), groupCells1.getBombsCount())
        );
        GroupCells groupCells1_2 = groupCells1.mergeOther(groupCells2);
        assertEquals(groupCells1_2, groupCells2_1);
        Set<Pair<Integer, Integer>> groupMerged = new HashSet<>();
        groupMerged.add(new Pair<>(3, 5));
        GroupCells groupCellsAssert = new GroupCells(groupMerged, 1);
        assertEquals(groupCellsAssert, groupCells1_2);
        GroupCells groupCells1_3 = groupCells1.mergeOther(groupCells3);
        GroupCells groupCells1_1 = groupCells1.mergeOther(groupCells1);
        GroupCells groupCells1_empty = groupCells1.mergeOther(groupCellsEmpty);
        assertEquals(groupCells1, groupCells1_3);
        assertEquals(groupCells1, groupCells1_1);
        assertEquals(groupCells1, groupCells1_empty);
    }

    @Test
    public void splitTest() {
        Set<Pair<Integer, Integer>> group1 = new HashSet<>();
        group1.add(new Pair<>(2, 3));
        group1.add(new Pair<>(3, 3));
        group1.add(new Pair<>(3, 5));
        GroupCells groupCells1 = new GroupCells(group1, 2);
        GroupCells groupCells2 = new GroupCells(group1, 3);
        GroupCells groupCells3 = new GroupCells(group1, 0);
        Set<GroupCells> split1 = groupCells1.split();
        Set<GroupCells> split2 = groupCells2.split();
        Set<GroupCells> split3 = groupCells3.split();
        Set<GroupCells> checkSet1 = new HashSet<>();
        checkSet1.add(groupCells1);
        Set<GroupCells> checkSet2 = new HashSet<>();
        Set<GroupCells> checkSet3 = new HashSet<>();
        Set<Pair<Integer, Integer>> pair1 = new HashSet<>();
        pair1.add(new Pair<>(2, 3));
        Set<Pair<Integer, Integer>> pair2 = new HashSet<>();
        pair2.add(new Pair<>(3, 3));
        Set<Pair<Integer, Integer>> pair3 = new HashSet<>();
        pair3.add(new Pair<>(3, 5));
        checkSet2.add(new GroupCells(pair1, 1));
        checkSet2.add(new GroupCells(pair2, 1));
        checkSet2.add(new GroupCells(pair3, 1));
        checkSet3.add(new GroupCells(pair1, 0));
        checkSet3.add(new GroupCells(pair2, 0));
        checkSet3.add(new GroupCells(pair3, 0));
        assertEquals(checkSet1, split1);
        assertEquals(checkSet2, split2);
        assertEquals(checkSet3, split3);
    }
}
