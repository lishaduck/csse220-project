package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BonusShip extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(52, 24);
    private static final double PROJECTILE_SPEED = 400;
    private int cycle = 0;
    private double elapsedSinceLastShot;

    public BonusShip(
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
            PowerUps powerUps,
            ArrayList<EnemyShip> enemies
    ) {
        elapsedSeconds = powerUps.enemyElapsed(elapsedSeconds);
        moveTowardTarget(playfield, elapsedSeconds);
        if (cycle == 5) {
            health = 0;
        }
        updatePatrolTarget(playfield);
    }

    private void updatePatrolTarget(Dimension2D playfield) {
        if (getPosition().getX() >= playfield.getWidth() - getWidth()) {
            target = new Point2D(-playfield.getWidth(), getPosition().getY());
            cycle++;
        } else if (getPosition().getX() <= 0) {
            target = new Point2D(playfield.getWidth(), getPosition().getY());
            cycle++;
        }
    }

    @Override
    public URL getImage() {
        return Objects.requireNonNull(
                getClass().getResource("/BonusShip.gif"),
                "Missing laser resource: /BonusShip.gif"
        );
    }
}