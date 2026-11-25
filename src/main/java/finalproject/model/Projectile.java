package finalproject.model;


import finalproject.model.ship.Ship;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class Projectile {
    public static final Dimension2D SIZE = new Dimension2D(4, 20);
    private final Ship source;
    private final int damage;
    private final Point2D velocity;
    private Point2D position;

    public Projectile(Ship source, Point2D velocity, int damage) {
        this.source = source;
        this.position = source.getPosition().add((source.getWidth() - SIZE.getWidth()) / 2, 0);
        this.velocity = velocity;
        this.damage = damage;
    }

    public boolean isSourcedFrom(Ship ship) {
        return source == ship;
    }

    public Point2D getPosition() {
        return position;
    }

    public int getDamage() {
        return damage;
    }

    public boolean updatePosition(double elapsed) {
        position = position.add(velocity.multiply(elapsed));

        return position.getY() < 0;
    }

    public boolean collide(Ship target) {
        if (this.source == target) return false;

        Point2D displacement = target.getPosition().subtract(position);
        if (displacement.getX() > -target.getWidth() && displacement.getX() < SIZE.getWidth()
                && displacement.getY() > -target.getHeight() && displacement.getY() < SIZE.getHeight()) {
            target.collideWithProjectile(this);
            return true;
        }
        return false;
    }
}
