package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.List;

abstract sealed public class EnemyShip extends Ship permits SkiLiftShip, SpreadshotShip {
    private final double movementSpeed;
    protected Point2D target;


    public EnemyShip(
            Dimension2D playfield,
            Dimension2D size,
            Point2D position,
            Point2D target,
            int health,
            double movementSpeed
    ) {
        super(playfield, size, position);
        this.health = health;
        this.movementSpeed = movementSpeed;
        this.target = target;
    }

    protected static boolean crossedThreshold(double previousElapsed, double elapsed, double threshold) {
        return previousElapsed < threshold && previousElapsed + elapsed >= threshold;
    }

    protected final boolean moveTowardTarget(Dimension2D playfield, double elapsedSeconds) {
        Point2D displacement = target.subtract(getPosition());
        double distanceToTarget = displacement.magnitude();
        double travelDistance = movementSpeed * elapsedSeconds;
        if (distanceToTarget <= travelDistance) {
            setPosition(target, playfield);
            return true;
        } else {
            Point2D movement = displacement.normalize().multiply(travelDistance);
            translateX(movement.getX(), playfield);
            translateY(movement.getY(), playfield);
            return false;
        }
    }

    protected final Point2D velocityToward(Point2D destination, double projectileSpeed) {
        Point2D direction = destination.subtract(getPosition());
        return direction.magnitude() == 0 ? Point2D.ZERO : direction.normalize().multiply(projectileSpeed);
    }

    protected final void fireProjectile(List<Projectile> projectiles, Point2D velocity) {
        projectiles.add(new Projectile(this, velocity));
    }

    public abstract java.net.URL getImage();

    public abstract void update(
            Point2D playerPosition,
            Dimension2D playfield,
            double elapsedSeconds,
            List<Projectile> projectiles
    );
}
