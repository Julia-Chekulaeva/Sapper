package game;

public class Cell {

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

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
