package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BossEdge extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(32, 16);
    private static final double PROJECTILE_SPEED = 200;
    private static final double ATTACK_SEQUENCE_SECONDS = 1.5;
    private static final double WIDE_SPREAD_ANGLE = Math.PI / 10;

    private final Random random = new Random();
    private double attackSequenceElapsed;

    public BossEdge(
            Dimension2D playfield,
            Point2D position,
            int health
    ) {
        super(playfield, SIZE, position, new Point2D(0, 0), health, 0);
    }

    @Override
    public void update(
            Point2D playerPosition,
            Dimension2D playfield,
            double elapsedSeconds,
            List<Projectile> projectiles,
            PowerUps powerUps,
            List<EnemyShip> enemies
    ) {
        elapsedSeconds = powerUps.enemyElapsed(elapsedSeconds);

        double previousAttackElapsed = attackSequenceElapsed;
        attackSequenceElapsed += elapsedSeconds;
        if (crossedThreshold(previousAttackElapsed, elapsedSeconds, ATTACK_SEQUENCE_SECONDS)) {
            fireWideVolley(playerPosition, projectiles, powerUps);
        }
        if (attackSequenceElapsed >= ATTACK_SEQUENCE_SECONDS) {
            attackSequenceElapsed = 0;
        }
    }

    private void fireWideVolley(Point2D playerPosition, List<Projectile> projectiles, PowerUps powerUps) {
        fireProjectile(projectiles, velocityToward(playerPosition, PROJECTILE_SPEED), powerUps, 1);
        fireProjectile(projectiles, velocityAtOffset(playerPosition, WIDE_SPREAD_ANGLE), powerUps, 1);
        fireProjectile(projectiles, velocityAtOffset(playerPosition, -WIDE_SPREAD_ANGLE), powerUps, 1);
    }


    private Point2D velocityAtOffset(Point2D playerPosition, double angleOffset) {
        Point2D velocity = velocityToward(playerPosition, PROJECTILE_SPEED);
        return new Point2D(
                velocity.getX() * Math.cos(angleOffset) - velocity.getY() * Math.sin(angleOffset),
                velocity.getX() * Math.sin(angleOffset) + velocity.getY() * Math.cos(angleOffset)
        );
    }


    @Override
    public URL getImage() {
        return Objects.requireNonNull(
                getClass().getResource("/EnemyShip2.gif"),
                "Missing laser resource: /EnemyShip.gif"
        );
    }
}
