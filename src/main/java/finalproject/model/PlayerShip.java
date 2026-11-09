package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Owns player movement speed and bottom alignment.
 */
public final class PlayerShip extends Ship {
    public static final Dimension2D SIZE = new Dimension2D(64, 64);
    static final Point2D PROJECTILE_VELOCITY = new Point2D(0, -600);

    private static final double SPEED = 300;
    private static final double BOTTOM_MARGIN = 24;

    PlayerShip(Dimension2D playfield) {
        super(
                playfield,
                SIZE,
                new Point2D(
                        (playfield.getWidth() - SIZE.getWidth()) / 2,
                        playfield.getHeight() - SIZE.getHeight() - BOTTOM_MARGIN
                )
        );
    }

    void move(HorizontalDirection direction, double elapsed, Dimension2D playfield) {
        double distance = direction.getSign() * SPEED * elapsed;
        translateX(distance, playfield);
    }

    void fitToPlayfield(Dimension2D playfield) {
        Point2D position = getPosition();
        double destinationY = playfield.getHeight() - getHeight() - BOTTOM_MARGIN;
        translateY(destinationY - position.getY(), playfield);
    }
}
