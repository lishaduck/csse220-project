package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

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
        double angle = Math.atan((target.getY() - getPosition().getY()) / (target.getX() - getPosition().getX()));
        if (distance < speed * elapsed) {
            setPosition(target, playfield);
        } else {
            translateX(Math.cos(angle) * speed * elapsed, playfield);
            translateY(Math.sin(angle) * speed * elapsed, playfield);
        }
    }

    public void enemyAI(Point2D playerPosition, Dimension2D playfield, double elapsed) {
        double oldCycle = cycle;
        cycle += elapsed;
        double angle = Math.atan((playerPosition.getY() - getPosition().getY()) / (playerPosition.getX() - getPosition().getX()));
        if (type == 0) {
            goTowardsTarget(playfield, elapsed);
            if (cycle > 100 && oldCycle <= 100) {
                spawnProjectile(new Point2D(0, 600));
                cycle = 0;
            }
            if (getPosition().getX() == playfield.getWidth()) {
                target.add(-playfield.getWidth(), 0);
            }
            if (getPosition().getX() == 0) {
                target.add(playfield.getWidth(), 0);
            }
        }
        if (type == 1) {
            goTowardsTarget(playfield, elapsed);
            if (getPosition() == target) {
                if (cycle > 200)
                    cycle = 0;
                if ((cycle > 50 && oldCycle <= 50) || (cycle > 150 && oldCycle <= 150)) {
                    spawnProjectile(new Point2D(600 * Math.cos(angle), 600 * Math.sin(angle)));
                    spawnProjectile(new Point2D(600 * Math.cos(angle + (Math.PI / 6)), 600 * Math.sin(angle + (Math.PI / 6))));
                    spawnProjectile(new Point2D(600 * Math.cos(angle - (Math.PI / 6)), 600 * Math.sin(angle - (Math.PI / 6))));
                }
                if (cycle > 100 && oldCycle <= 100) {
                    spawnProjectile(new Point2D(600 * Math.cos(angle + (Math.PI / 12)), 600 * Math.sin(angle + (Math.PI / 12))));
                    spawnProjectile(new Point2D(600 * Math.cos(angle - (Math.PI / 12)), 600 * Math.sin(angle - (Math.PI / 12))));
                }
                if (cycle > 200 && oldCycle <= 200)
                    target = new Point2D(random.nextInt((int) playfield.getWidth()), random.nextInt((int) (playfield.getHeight() / 2)));
            }
        }
    }
}
