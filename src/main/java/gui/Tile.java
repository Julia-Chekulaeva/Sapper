package gui;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    int x;

    int y;

    private static Image getImageNumber(int num) {
        return new Image("image" + num + ".png");
    }

    public static final ImagePattern BLUE_IMAGE = new ImagePattern(new Image("blue.jpg"));

    private static final Image IMAGE_BOMB = new Image("bomb.png");

    private static final Image IMAGE_BOOM = new Image("bomb_boom.png");

    private final Paint input;

    private boolean isOpen = false;

    private boolean hasBomb;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean hasBomb() {
        return hasBomb;
    }

    public Tile(boolean hasBomb, int neighbours, int x, int y) {
        this.hasBomb = hasBomb;
        this.x = x;
        this.y = y;
        input = new ImagePattern(hasBomb ? IMAGE_BOMB : getImageNumber(neighbours));
        setWidth(AppGUI.tileSize);
        setHeight(AppGUI.tileSize);
        relocate(x * AppGUI.tileSize, y * AppGUI.tileSize);
        setFill(BLUE_IMAGE);
    }

    public void openImage() {
        setFill(input);
    }

    public void makeBoom() {
        setFill(new ImagePattern(IMAGE_BOOM));
    }

}
