package finalproject;

import javafx.geometry.Point2D;

public class Ship {
    Point2D position = Point2D.ZERO;
    int health;

    public void translateX(int distance) {
        position = position.add(distance, 0);
    }

    public void translateY(int distance) {
        position = position.add(0, distance);
    }

    public void spawnProjectile(int type) {
    }

    public void collideWithProjectile(Projectile projectile) {
        health -= projectile.damage;
    }
}
