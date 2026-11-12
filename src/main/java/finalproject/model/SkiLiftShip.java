package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public final class SkiLiftShip extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(48, 36);
    private static final double SHOT_INTERVAL_SECONDS = 2;
    private static final double PROJECTILE_SPEED = 400;

    private double elapsedSinceLastShot;

    public SkiLiftShip(
            Dimension2D playfield,
            Point2D position,
            double targetX,
            int health,
            double movementSpeed
    ) {
        super(
                playfield,
                SIZE,
                position,
                new Point2D(targetX, Math.clamp(position.getY(), 0, Math.max(0, playfield.getHeight() - SIZE.getHeight()))),
                health,
                movementSpeed
        );
    }

    @Override
    public void update(
            Point2D playerPosition,
            Dimension2D playfield,
            double elapsedSeconds,
            List<Projectile> projectiles,
            PowerUps powerUps
    ) {
        elapsedSeconds = powerUps.enemyElapsed(elapsedSeconds);
        double previousShotElapsed = elapsedSinceLastShot;
        elapsedSinceLastShot += elapsedSeconds;
        moveTowardTarget(playfield, elapsedSeconds);

        if (crossedThreshold(previousShotElapsed, elapsedSeconds, SHOT_INTERVAL_SECONDS)) {
            fireProjectile(projectiles, new Point2D(0, PROJECTILE_SPEED), powerUps);
            elapsedSinceLastShot = 0;
        }

        updatePatrolTarget(playfield);
    }

    private void updatePatrolTarget(Dimension2D playfield) {
        if (getPosition().getX() >= playfield.getWidth() - getWidth()) {
            target = new Point2D(-playfield.getWidth(), getPosition().getY());
        } else if (getPosition().getX() <= 0) {
            target = new Point2D(playfield.getWidth(), getPosition().getY());
        }
    }

    @Override
    public URL getImage() {
        return Objects.requireNonNull(
                getClass().getResource("/EnemyShip.gif"),
                "Missing laser resource: /EnemyShip.gif"
        );
    }
}
