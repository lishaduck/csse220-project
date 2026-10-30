package finalproject;


import javafx.geometry.Point2D;

public class Projectile {
    Point2D position;
    Point2D velocity;
    int[] special;
    int damage;

    Projectile() {
        position = new Point2D(50, 50);
        velocity = new Point2D(0, -10);
        damage = 2;
    }

    Projectile(Point2D p) {
        position = p;
        velocity = new Point2D(0, -10);
        damage = 2;
    }

    Projectile(Point2D p, Point2D velocity) {
        position = p;
        this.velocity = velocity;
        damage = 2;
    }

    Projectile(Point2D p, Point2D velocity, int[] s) {
        position = p;
        this.velocity = velocity;
        special = s;
        damage = 2;
    }

    Projectile(Point2D p, Point2D velocity, int[] s, int d) {
        position = p;
        this.velocity = velocity;
        special = s;
        damage = d;
    }

    void updatePosition() {
        position = position.add(velocity);
    }
}
