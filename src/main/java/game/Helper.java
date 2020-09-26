package game;

public abstract class Helper {

    protected abstract void someFun();

    public void forEachNeighbour(int a, int b, int size) {
        for (int x = Integer.max(a - 1, 0); x < Integer.min(a + 2, size); x++) {
            for (int y = Integer.max(b - 1, 0); y < Integer.min(b + 2, size); y++) {
                someFun();
            }
        }
    }
}
