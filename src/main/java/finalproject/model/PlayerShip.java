package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/** Owns player movement rules within its playfield. */
public final class PlayerShip extends Ship {
    public static final double WIDTH = 64;
    public static final double HEIGHT = 64;

    private static final double BOTTOM_MARGIN = 24;

    PlayerShip(Dimension2D playfield) {
        super(playfield, new Dimension2D(WIDTH, HEIGHT));
        translateX((playfield.getWidth() - getWidth()) / 2);
        fitToPlayfield();
    }

    void fitToPlayfield() {
        Dimension2D playfield = getPlayfield();
        Point2D position = getPosition();
        double destinationY = playfield.getHeight() - getHeight() - BOTTOM_MARGIN;
        translateY(destinationY - position.getY());
    }
}
