package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BossMiddle extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(80, 86);
    private static final double PROJECTILE_SPEED = 200;
    private static final double ATTACK_SEQUENCE_SECONDS = 15;

    private final Random random = new Random();
    private double attackSequenceElapsed;

    public BossMiddle(
            Dimension2D playfield,
            Point2D position,
            int health
    ) {
        super(playfield, SIZE, position, new Point2D(0, 0), health, 0);
    }


    public void update(
            Point2D playerPosition,
            Dimension2D playfield,
            double elapsedSeconds,
            List<Projectile> projectiles,
            PowerUps powerUps,
            ArrayList<EnemyShip> enemies
    ) {
        elapsedSeconds = powerUps.enemyElapsed(elapsedSeconds);
        double previousAttackElapsed = attackSequenceElapsed;
        attackSequenceElapsed += elapsedSeconds;
        if (crossedThreshold(previousAttackElapsed, elapsedSeconds, ATTACK_SEQUENCE_SECONDS)) {
            enemies.add(new MineShip(playfield, getPosition(), 30, 10));
        }
        if (attackSequenceElapsed >= ATTACK_SEQUENCE_SECONDS) {
            attackSequenceElapsed = 0;
        }
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
                getClass().getResource("/BossHead.png"),
                "Missing laser resource: /BossHead.png"
        );
    }
}
