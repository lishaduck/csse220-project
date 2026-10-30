package finalproject;


import javafx.geometry.Point2D;

public class Projectile {
    Point2D position;
    double velocityY;
    double velocityX;
    int[] special;
    int damage;

    Projectile() {
        position = new Point2D(50, 50);
        velocityY = -10;
        velocityX = 0;
        damage = 2;
    }

    Projectile(Point2D p) {
        position = p;
        velocityY = -10;
        velocityX = 0;
        damage = 2;
    }

    Projectile(Point2D p, double x, double y) {
        position = p;
        velocityY = y;
        velocityX = x;
        damage = 2;
    }

    Projectile(Point2D p, double x, double y, int[] s) {
        position = p;
        velocityY = y;
        velocityX = x;
        special = s;
        damage = 2;
    }

    Projectile(Point2D p, double x, double y, int[] s, int d) {
        position = p;
        velocityY = y;
        velocityX = x;
        special = s;
        damage = d;
    }

    void updatePosition() {
        position = (new Point2D(position.getX() + velocityX, position.getY() + velocityY));
    }
}
