package algorithm;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class GroupCells {

    private final Set<Pair<Integer, Integer>> group;

    private int bombsCount;

    public GroupCells(Set<Pair<Integer, Integer>> group, int bombsCount) {
        this.group = group;
        this.bombsCount = bombsCount;
    }

    public GroupCells mergeOther(@NotNull GroupCells other) {
        if (this.equals(other))
            return this;
        Set<Pair<Integer, Integer>> newGroup = new HashSet<>(group);
        newGroup.retainAll(other.group);
        if (newGroup.isEmpty())
            return this;
        if (group.equals(newGroup)) {
            other.bombsCount = other.bombsCount - bombsCount;
            other.group.removeAll(newGroup);
            return this;
        }
        if (other.group.equals(newGroup)) {
            bombsCount = bombsCount - other.bombsCount;
            group.removeAll(newGroup);
            return this;
        }
        return this;
        /*if (bombsCount == other.bombsCount)
            return this;
        GroupCells newGroupCells;
        int newBombsCount;
        if (bombsCount > other.bombsCount) {
            newBombsCount = bombsCount - (other.group.size() - newGroup.size());
            if (newBombsCount != other.bombsCount)
                return this;

        } else {
            newBombsCount = other.bombsCount - (group.size() - newGroup.size());
            if (newBombsCount != bombsCount)
                return this;
        }
        newGroupCells = new GroupCells(newGroup, newBombsCount);
        other.group.removeAll(newGroup);
        group.removeAll(newGroup);
        return newGroupCells;*/
    }

    public Set<GroupCells> split() {
        Set<GroupCells> res = new HashSet<>();
        int bomb = bombsCount == group.size() ? 1 : bombsCount == 0 ? 0 : -1;
        if (bomb >= 0) {
            for (Pair<Integer, Integer> coords : group) {
                Set<Pair<Integer, Integer>> set = new HashSet<>();
                set.add(coords);
                res.add(new GroupCells(set, bomb));
            }
        } else {
            res.add(this);
        }
        return res;
    }

    public Set<Pair<Integer, Integer>> getGroup() {
        return group;
    }

    public int getBombsCount() {
        return bombsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupCells that = (GroupCells) o;
        return bombsCount == that.bombsCount &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, bombsCount);
    }
}
