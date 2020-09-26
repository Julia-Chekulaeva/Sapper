package game;

public class Board {

    public Integer[][] openCells;

    private boolean lostTheGame = false;

    private boolean winTheGame = false;

    private Cell[][] cells;

    private int size;

    private int numOfBombs;

    public void setFlag(int x, int y) {
        openCells[x][y] = -1;
    }

    public void set(int size, int numOfBombs) {
        this.size = size;
        this.numOfBombs = numOfBombs;
        cells = new Cell[size][size];
        openCells = new Integer[size][size];
        //Заполняем
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                cells[a][b] = new Cell();
            }
        }
        //Ставим бомбы
        for (int i = 0; i < numOfBombs; i++) {
            int x = (int) (Math.random() * size);
            int y = (int) (Math.random() * size);
            if (cells[x][y].hasBomb()) {
                i--;
            } else {
                cells[x][y].setBomb(true);
            }
        }
        //Счет соседей
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                for (int x = a - 1; x < a + 2; x++) {
                    if (x == -1 || x == size)
                        continue;
                    for (int y = b - 1; y < b + 2; y++) {
                        if (y == -1 || y == size)
                            continue;
                        if (cells[x][y].hasBomb()) {
                            cells[a][b].addNeighbourBomb();
                        }
                    }
                }
            }
        }
    }

    public void guess(int x, int y) {
        //Проиграл?
        if (cells[x][y].hasBomb()) {
            for (int a = 0; a < size; a++) {
                for (int b = 0; b < size; b++) {
                    openCells[a][b] = (cells[a][b].hasBomb()) ? -1 : cells[a][b].getNeighbourBombs();
                    cells[a][b].setOpen(true);
                }
            }
            //(нужно все ячейки сделать известными)
            lostTheGame = true;
            return;
        }
        //Открыть все не соседние с бомбами клетки
        openAllNulls(x, y);
    }

    private void openAllNulls(int a, int b) {
        openCells[a][b] = cells[a][b].getNeighbourBombs();
        if (cells[a][b].isOpen() || openCells[a][b] > 0) {
            cells[a][b].setOpen(true);
            return;
        }
        cells[a][b].setOpen(true);
        for (int x = Integer.max(a - 1, 0); x < Integer.min(a + 2, size); x++) {
            for (int y = Integer.max(b - 1, 0); y < Integer.min(b + 2, size); y++) {
                openAllNulls(x, y);
            }
        }
    }

    public void printProcess() {
        for (Integer[] cellRow : openCells) {
            for (Integer cell : cellRow) {
                System.out.print(String.format("% 4d ", cell));
            }
            System.out.println();
        }
        System.out.println();
    }

    public void analyseProcess() {
        int numOfNulls = 0;
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (!cell.isOpen()) {
                    numOfNulls++;
                }
            }
        }
        if (numOfNulls == numOfBombs && !lostTheGame) {
            winTheGame = true;
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public boolean lostTheGame() {
        return lostTheGame;
    }

    public boolean winTheGame() {
        return winTheGame;
    }
}
