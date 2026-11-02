package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Owns ship state and constrains translation using each ship's dimensions.
 */
public abstract class Ship {
    private final Dimension2D size;
    int health;
    private Dimension2D playfield;
    private Point2D position = Point2D.ZERO;

    protected Ship(Dimension2D playfield, Dimension2D size) {
        this.playfield = playfield;
        this.size = size;
    }

    public final Point2D getPosition() {
        return position;
    }

    protected final void setPosition(Point2D position) {
        double x = Math.clamp(position.getX(), 0, Math.max(0, playfield.getWidth() - getWidth()));
        double y = Math.clamp(position.getY(), 0, Math.max(0, playfield.getHeight() - getHeight()));
        this.position = new Point2D(x, y);
    }

    public final double getWidth() {
        return size.getWidth();
    }

    public final double getHeight() {
        return size.getHeight();
    }

    public final void translateX(double distance) {
        setPosition(position.add(distance, 0));
    }

    public final void translateY(double distance) {
        setPosition(position.add(0, distance));
    }

    protected final Dimension2D getPlayfield() {
        return playfield;
    }

    final void resizePlayfield(Dimension2D playfield) {
        this.playfield = playfield;
        setPosition(position);
    }

    public void spawnProjectile(int type) {
    }

    public void collideWithProjectile(Projectile projectile) {
        health -= projectile.damage;
    }
}
