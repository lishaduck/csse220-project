package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Owns ship state and constrains translation using supplied bounds and the ship's size.
 */
public abstract class Ship {
    private final Dimension2D size;
    int health;
    private Point2D position;

    protected Ship(Dimension2D playfield, Dimension2D size, Point2D position) {
        this.size = size;
        setPosition(position, playfield);
    }

    public final Point2D getPosition() {
        return position;
    }

    protected void setPosition(Point2D position, Dimension2D playfield) {
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

    protected final void translateX(double distance, Dimension2D playfield) {
        setPosition(position.add(distance, 0), playfield);
    }

    protected final void translateY(double distance, Dimension2D playfield) {
        setPosition(position.add(0, distance), playfield);
    }

    public void collideWithProjectile(Projectile projectile) {
        health -= projectile.damage;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
