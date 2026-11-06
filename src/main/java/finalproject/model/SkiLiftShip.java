package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public final class SkiLiftShip extends EnemyShip {
    public SkiLiftShip(
            Dimension2D playfield,
            Point2D position,
            Point2D target,
            int health,
            int speed
    ) {
        super(playfield, new Dimension2D(50, 50), position, target, health, speed);
    }

    public void enemyAI(Point2D playerPosition, Dimension2D playfield, double elapsed, ArrayList<Projectile> projectiles) {
        cycle += elapsed;
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

}
