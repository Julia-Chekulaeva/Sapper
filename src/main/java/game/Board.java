package game;

public class Board {

    private Integer[][] openCells;// Это для консоли

    public boolean lostTheGame = false;

    public boolean winTheGame = false;

    private Cell[][] cells;

    private int size;

    private int numOfBombs;

    public void set(int size, int numOfBombs) {
        this.size = size;
        this.numOfBombs = numOfBombs;
        cells = new Cell[size][size];
        openCells = new Integer[size][size];
        //Заполняем
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                cells[a][b] = new Cell();
                // ??
                cells[a][b].setX(a);//
                cells[a][b].setY(b);//
                // Нужны ли эти сеттеры?
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
        for (int x = a - 1; x < a + 2; x++) {
            if (x == -1 || x == size)
                continue;
            for (int y = b - 1; y < b + 2; y++) {
                if (y == -1 || y == size)
                    continue;
                openAllNulls(x, y);
            }
        }
    }

    public void printAndAnalyseProcess() {
        int numOfNulls = 0;
        for (Integer[] cellRow : openCells) {
            for (Integer cell : cellRow) {
                if (cell == null) {
                    numOfNulls++;
                }
                System.out.print(String.format("% 4d   ", cell));
            }
            System.out.println();
        }
        System.out.println();
        if (numOfNulls == numOfBombs && !lostTheGame) {
            winTheGame = true;
        }
    }

    public Cell[][] getCells() {
        return cells;
    }
}
