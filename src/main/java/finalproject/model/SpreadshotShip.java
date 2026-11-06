package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public final class SpreadshotShip extends EnemyShip {
    public SpreadshotShip(
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
        if (getPosition().equals(target)) {
            if (cycle > 2.1) {
                cycle = 0;
            }
            if ((cycle > 0.5 && cycle - elapsed <= 0.5) || (cycle > 1.5 && cycle - elapsed <= 1.5)) {
                projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle), 200 * Math.sin(angle))));
                projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle + (Math.PI / 8)), 200 * Math.sin(angle + (Math.PI / 8)))));
                projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle - (Math.PI / 8)), 200 * Math.sin(angle - (Math.PI / 8)))));
            }
            if (cycle > 1 && cycle - elapsed <= 1) {
                projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle + (Math.PI / 16)), 200 * Math.sin(angle + (Math.PI / 16)))));
                projectiles.add(spawnProjectile(new Point2D(200 * Math.cos(angle - (Math.PI / 16)), 200 * Math.sin(angle - (Math.PI / 16)))));
            }
            if (cycle > 2 && cycle - elapsed <= 2) {
                target = new Point2D(random.nextInt((int) playfield.getWidth() - 50), random.nextInt((int) (playfield.getHeight() / 3)));
            }
        }

        return projectiles;
    }
}
