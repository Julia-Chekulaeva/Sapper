package game;

public class Gamer {

    private final int size;

    private final int numOfBombs;

    int numOfGuesses;

    public Board board;

    public Gamer(int size, int numOfBombs) {
        this.size = size;
        this.numOfBombs = numOfBombs;
    }

    public void start() {
        board = new Board();
        board.set(size, numOfBombs);
    }

    public void guess(int x, int y) {
        board.guess(x, y);
        board.analyseProcess();
        numOfGuesses++;
    }

    protected void setFlag(int x, int y) {
        board.setFlag(x, y);
    }

    public int getNumOfGuesses() {
        return numOfGuesses;
    }
}
