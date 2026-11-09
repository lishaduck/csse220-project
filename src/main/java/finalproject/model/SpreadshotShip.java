package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class SpreadshotShip extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(32, 16);
    private static final double PROJECTILE_SPEED = 200;
    private static final double ATTACK_SEQUENCE_SECONDS = 2.1;
    private static final double FIRST_VOLLEY_SECONDS = 0.5;
    private static final double SECOND_VOLLEY_SECONDS = 1;
    private static final double THIRD_VOLLEY_SECONDS = 1.5;
    private static final double RETARGET_SECONDS = 2;
    private static final double WIDE_SPREAD_ANGLE = Math.PI / 8;
    private static final double NARROW_SPREAD_ANGLE = Math.PI / 16;

    private final Random random = new Random();
    private double attackSequenceElapsed;

    public SpreadshotShip(
            Dimension2D playfield,
            Point2D position,
            Point2D target,
            int health,
            double movementSpeed
    ) {
        super(playfield, SIZE, position, target, health, movementSpeed);
    }

    @Override
    public void update(
            Point2D playerPosition,
            Dimension2D playfield,
            double elapsedSeconds,
            List<Projectile> projectiles
    ) {
        boolean reachedTarget = moveTowardTarget(playfield, elapsedSeconds);
        if (!reachedTarget) {
            return;
        }

        double previousAttackElapsed = attackSequenceElapsed;
        attackSequenceElapsed += elapsedSeconds;
        if (crossedThreshold(previousAttackElapsed, elapsedSeconds, FIRST_VOLLEY_SECONDS)
                || crossedThreshold(previousAttackElapsed, elapsedSeconds, THIRD_VOLLEY_SECONDS)) {
            fireWideVolley(playerPosition, projectiles);
        }
        if (crossedThreshold(previousAttackElapsed, elapsedSeconds, SECOND_VOLLEY_SECONDS)) {
            fireNarrowVolley(playerPosition, projectiles);
        }
        if (crossedThreshold(previousAttackElapsed, elapsedSeconds, RETARGET_SECONDS)) {
            target = chooseNextTarget(playfield);
        }
        if (attackSequenceElapsed >= ATTACK_SEQUENCE_SECONDS) {
            attackSequenceElapsed = 0;
        }
    }

    private void fireWideVolley(Point2D playerPosition, List<Projectile> projectiles) {
        fireProjectile(projectiles, velocityToward(playerPosition, PROJECTILE_SPEED));
        fireProjectile(projectiles, velocityAtOffset(playerPosition, WIDE_SPREAD_ANGLE));
        fireProjectile(projectiles, velocityAtOffset(playerPosition, -WIDE_SPREAD_ANGLE));
    }

    private void fireNarrowVolley(Point2D playerPosition, List<Projectile> projectiles) {
        fireProjectile(projectiles, velocityAtOffset(playerPosition, NARROW_SPREAD_ANGLE));
        fireProjectile(projectiles, velocityAtOffset(playerPosition, -NARROW_SPREAD_ANGLE));
    }

    private Point2D velocityAtOffset(Point2D playerPosition, double angleOffset) {
        Point2D velocity = velocityToward(playerPosition, PROJECTILE_SPEED);
        return new Point2D(
                velocity.getX() * Math.cos(angleOffset) - velocity.getY() * Math.sin(angleOffset),
                velocity.getX() * Math.sin(angleOffset) + velocity.getY() * Math.cos(angleOffset)
        );
    }

    private Point2D chooseNextTarget(Dimension2D playfield) {
        int maximumX = Math.max(0, (int) Math.floor(playfield.getWidth() - getWidth()));
        int maximumY = Math.max(0, (int) Math.floor(playfield.getHeight() / 3 - getHeight()));
        return new Point2D(random.nextInt(maximumX + 1), random.nextInt(maximumY + 1));
    }

    @Override
    public URL getImage() {
        return Objects.requireNonNull(
                getClass().getResource("/EnemyShip2.gif"),
                "Missing laser resource: /EnemyShip.gif"
        );
    }
}
