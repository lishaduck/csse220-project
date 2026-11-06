package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Random;

abstract sealed public class EnemyShip extends Ship permits SkiLiftShip, SpreadshotShip {
    Point2D target;
    int speed;
    double cycle;
    Random random = new Random();

    public EnemyShip(
            Dimension2D playfield,
            Dimension2D size,
            Point2D position,
            Point2D target,
            int health,
            int speed
    ) {
        super(playfield, size, position);
        this.health = health;
        this.speed = speed;
        this.cycle = 0;
        this.target = target;
    }

    public void goTowardsTarget(Dimension2D playfield, double elapsed) {
        double distance = Math.sqrt(Math.pow(getPosition().getX() - target.getX(), 2) + Math.pow(getPosition().getY() - target.getY(), 2));
        double angle = Math.atan2((target.getY() - getPosition().getY()), (target.getX() - getPosition().getX()));
        if (distance < speed * elapsed) {
            setPosition(target, playfield);
        } else {
            translateX(Math.cos(angle) * speed * elapsed, playfield);
            translateY(Math.sin(angle) * speed * elapsed, playfield);
        }
    }

    abstract public void enemyAI(Point2D playerPosition, Dimension2D playfield, double elapsed, ArrayList<Projectile> projectiles);
}
