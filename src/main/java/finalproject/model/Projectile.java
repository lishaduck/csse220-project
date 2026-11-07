package finalproject.model;


import javafx.geometry.Point2D;

public class Projectile {
    private final Ship source;
    private Point2D position;
    Point2D velocity;
    int[] special;
    int damage;

    Projectile(Ship source, Point2D position) {
        this(source, position, new Point2D(0, -600));
    }

    Projectile(Ship source, Point2D position, Point2D velocity) {
        this.source = source;
        this.position = position;
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

    public final double getWidth() {
        return 20;
    }

    public final double getHeight() {
        return 50;
    }
}
