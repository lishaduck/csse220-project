package finalproject.model;


import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class Projectile {
    public static final Dimension2D SIZE = new Dimension2D(20, 50);
    private final Ship source;
    Point2D velocity;
    int[] special;
    int damage;
    private Point2D position;

    Projectile(Ship source, Point2D velocity) {
        this.source = source;
        this.position = source.getPosition();
        this.velocity = velocity;
        damage = 2;
    }

    public Ship getSource() {
        return source;
    }

    public Point2D getPosition() {
        return position;
    }

    public boolean updatePosition(double elapsed) {
        position = position.add(velocity.multiply(elapsed));

        return position.getY() < 0;
    }

    public boolean collide(Ship target) {
        Point2D displacement = target.getPosition().subtract(position);
        if (displacement.getX() > -target.getWidth() && displacement.getX() < SIZE.getWidth()
                && displacement.getY() > -target.getHeight() && displacement.getY() < SIZE.getHeight()) {
            target.collideWithProjectile(this);
            return true;
        }
        return false;
    }
}
