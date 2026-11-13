package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BossLaser extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(56, 86);
    private static final double PROJECTILE_SPEED = 200;
    private static final double ATTACK_SEQUENCE_SECONDS = 20;

    private final Random random = new Random();
    private double attackSequenceElapsed;

    public BossLaser(
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
        if (attackSequenceElapsed > ATTACK_SEQUENCE_SECONDS / 2) {
            fireProjectile(projectiles, new Point2D(0, PROJECTILE_SPEED), powerUps, 1);
        }
        if (attackSequenceElapsed >= ATTACK_SEQUENCE_SECONDS) {
            attackSequenceElapsed = 0;
        }
    }


    @Override
    public URL getImage() {
        return Objects.requireNonNull(
                getClass().getResource("/BossCannon.png"),
                "Missing laser resource: /BossCannon.png"
        );
    }
}
