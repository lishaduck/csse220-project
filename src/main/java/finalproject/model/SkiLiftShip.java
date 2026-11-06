package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public final class SkiLiftShip extends EnemyShip {
    public SkiLiftShip(
            Dimension2D playfield,
            Dimension2D size,
            Point2D position,
            int health,
            int speed,
            Point2D target
    ) {
        super(playfield, size, position, health, speed, target);
    }

    public ArrayList<Projectile> enemyAI(Point2D playerPosition, Dimension2D playfield, double elapsed, ArrayList<Projectile> projectiles) {
        cycle += elapsed;
        double angle = Math.atan2((playerPosition.getY() - getPosition().getY()), (playerPosition.getX() - getPosition().getX()));
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
        return projectiles;
    }

}
