package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public final class MineShip extends EnemyShip {
    private static final Dimension2D SIZE = new Dimension2D(68, 68);
    private static final double PROJECTILE_SPEED = 400;

    private double elapsedSinceLastShot;

    public MineShip(
            Dimension2D playfield,
            Point2D position,
            int health,
            double movementSpeed
    ) {
        super(
                playfield,
                SIZE,
                position,
                new Point2D(0, Math.clamp(position.getY(), 0, Math.max(0, playfield.getHeight() - SIZE.getHeight()))),
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
            List<EnemyShip> enemies
    ) {
        translateY(movementSpeed * elapsedSeconds, playfield);
        if (getPosition().getY() > playfield.getHeight() - 70) {
            health = 0;
        }
        if (!isAlive()) {
            for (int i = 0; i < 12; i++) {
                fireProjectile(projectiles, new Point2D(PROJECTILE_SPEED * Math.cos(i * Math.PI / 6), PROJECTILE_SPEED * Math.sin(i * Math.PI / 6)), powerUps, 2);
            }
        }
    }

    @Override
    public URL getImage() {
        return Objects.requireNonNull(
                getClass().getResource("/Mine.gif"),
                "Missing laser resource: /Mine.gif"
        );
    }
}
