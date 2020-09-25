package game;

public class Cell {
    // Нужны ли в этом классе координаты?

    private int x;

    private int y;

    private boolean isOpen = false;

    private boolean hasBomb = false;

    private int neighbourBombs;

    public void setBomb(boolean hasBomb) {
        this.hasBomb = hasBomb;
    }

    public boolean hasBomb() {
        return hasBomb;
    }

    public void addNeighbourBomb() {
        neighbourBombs++;
    }

    public int getNeighbourBombs() {
        return neighbourBombs;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
