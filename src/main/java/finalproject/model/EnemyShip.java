package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Random;

public class EnemyShip extends Ship {
    int type;
    Point2D target;
    int speed;
    double cycle;
    Random random = new Random();

    public EnemyShip(
            Dimension2D playfield,
            Dimension2D size,
            Point2D position,
            int type,
            int health,
            int speed,
            int targetX,
            int targetY
    ) {
        super(playfield, size, position);
        this.type = type;
        this.health = health;
        this.speed = speed;
        this.cycle = 0;
        target = new Point2D(targetX, targetY);
    }

    //everything below here is not tested and may be broken
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

    public ArrayList<Projectile> enemyAI(Point2D playerPosition, Dimension2D playfield, double elapsed, ArrayList<Projectile> projectiles) {
        cycle += elapsed;
        double angle = Math.atan2((playerPosition.getY() - getPosition().getY()), (playerPosition.getX() - getPosition().getX()));
        if (type == 0) {
            goTowardsTarget(playfield, elapsed);
            if (cycle > 2 && cycle - elapsed <= 2) {
                projectiles.add(spawnProjectile(new Point2D(0, 400)));
                cycle = 0;
            }
            if (getPosition().getX() >= playfield.getWidth() - 50) {
                target = new Point2D(-playfield.getWidth(), 100);
            }
            if (getPosition().getX() <= 0) {
                target = new Point2D(playfield.getWidth(), 100);
            }
        }
        if (type == 1) {
            goTowardsTarget(playfield, elapsed);
            if (getPosition().equals(target)) {
                if (cycle > 2.1)
                    cycle = 0;
                if ((cycle > 0.5 && cycle - elapsed <= 0.5) || (cycle > 1.5 && cycle - elapsed <= 1.5)) {
                    projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle), 200 * Math.sin(angle))));
                    projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle + (Math.PI / 8)), 200 * Math.sin(angle + (Math.PI / 8)))));
                    projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle - (Math.PI / 8)), 200 * Math.sin(angle - (Math.PI / 8)))));
                }
                if (cycle > 1 && cycle - elapsed <= 1) {
                    projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle + (Math.PI / 16)), 200 * Math.sin(angle + (Math.PI / 16)))));
                    projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle - (Math.PI / 16)), 200 * Math.sin(angle - (Math.PI / 16)))));
                }
                if (cycle > 2 && cycle - elapsed <= 2)
                    target = new Point2D(random.nextInt((int) playfield.getWidth() - 50), random.nextInt((int) (playfield.getHeight() / 3)));
            }
        }
        return projectiles;
    }
}
