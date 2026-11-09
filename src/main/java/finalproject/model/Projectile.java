package finalproject.model;


import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Projectile {
    public static final Dimension2D SIZE = new Dimension2D(20, 50);
    private final Ship source;
    Point2D velocity;
    int[] special;
    int damage;
    boolean isEnemy;
    private Point2D position;

    Projectile(Ship source, Point2D position, boolean isEnemy) {
        this(source, position, new Point2D(0, -600), isEnemy);
    }

    Projectile(Ship source, Point2D position, Point2D velocity, boolean isEnemy) {
        this.source = source;
        this.position = position;
        this.velocity = velocity;
        damage = 2;
        this.isEnemy = isEnemy;
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

    public boolean collide(PlayerShip player, ArrayList<EnemyShip> enemies) {
        if (isEnemy && Math.abs(player.getPosition().getX() - position.getX()) < (player.getWidth() + SIZE.getWidth()) / 2 && Math.abs(player.getPosition().getY() - position.getY()) < (player.getHeight() + SIZE.getHeight()) / 2) {
            player.collideWithProjectile(this);
            return true;
        }
        for (EnemyShip i : enemies) {
            if (!isEnemy && Math.abs(i.getPosition().getX() - position.getX()) < (i.getWidth() + SIZE.getWidth()) / 2 && Math.abs(i.getPosition().getY() - position.getY()) < (i.getHeight() + SIZE.getHeight()) / 2) {
                i.collideWithProjectile(this);
                return true;
            }
        }
        return false;
    }
}
