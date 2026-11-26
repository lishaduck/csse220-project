package finalproject.model.ship;

import finalproject.model.HorizontalDirection;
import finalproject.model.Projectile;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {
    private static final Dimension2D PLAYFIELD = new Dimension2D(640, 480);

    /** Fires from a stationary ski lift placed so the shot starts at the given point. */
    private static Projectile shotFrom(Point2D shotPosition) {
        double offset = (new SkiLiftShip(PLAYFIELD, Point2D.ZERO, 0, 1, 0).getWidth() - Projectile.SIZE.getWidth()) / 2;
        Point2D shooter = shotPosition.subtract(offset, 0);
        return new Projectile(new SkiLiftShip(PLAYFIELD, shooter, shooter.getX(), 1, 0), Point2D.ZERO, 1);
    }

    @Test
    void shotsStartAtTheShootersHorizontalCenterAndIgnoreItsLaterMovement() {
        PlayerShip player = new PlayerShip(PLAYFIELD);
        EnemyShip skiLift = new SkiLiftShip(PLAYFIELD, new Point2D(100, 100), 100, 2, 0);
        EnemyShip spreadshot = new SpreadshotShip(PLAYFIELD, new Point2D(300, 100), new Point2D(300, 100), 2, 0);
        for (Ship shooter : List.of(player, skiLift, spreadshot)) {
            Point2D velocity = new Point2D(17, -43);
            Projectile shot = new Projectile(shooter, velocity, 2);
            Point2D start = shot.getPosition();
            assertTrue(shot.isSourcedFrom(shooter));
            assertEquals(shooter.getPosition().getX() + shooter.getWidth() / 2,
                    start.getX() + Projectile.SIZE.getWidth() / 2, 1e-9);
            assertEquals(shooter.getPosition().getY(), start.getY());

            player.move(HorizontalDirection.RIGHT, 0.5, PLAYFIELD);
            shot.updatePosition(0.5);
            assertEquals(start.add(velocity.multiply(0.5)), shot.getPosition());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1, 0, 0.1, true", "1, 0, 0, false",
            "-1, 0, 0.1, true", "-1, 0, 0, false",
            "0, 1, 0.1, true", "0, 1, 0, false",
            "0, -1, 0.1, true", "0, -1, 0, false"
    })
    void shotsHitOnlyWhenTheyOverlapTheTarget(int edgeX, int edgeY, double inward, boolean hit) {
        EnemyShip target = new SkiLiftShip(PLAYFIELD, new Point2D(200, 200), 200, 2, 0);
        // Puts the shot touching the chosen edge from outside, then nudges it inward.
        double x = edgeX > 0 ? target.getWidth() : edgeX < 0 ? -Projectile.SIZE.getWidth() : 0;
        double y = edgeY > 0 ? target.getHeight() : edgeY < 0 ? -Projectile.SIZE.getHeight() : 0;
        Point2D position = target.getPosition().add(x - edgeX * inward, y - edgeY * inward);

        assertEquals(hit, shotFrom(position).collide(target), "Shot at " + position);
        assertEquals(hit ? 1 : 0, target.getHitCount());
    }

    @Test
    void shotsNeverHitTheirOwnShooter() {
        EnemyShip shooter = new SkiLiftShip(PLAYFIELD, new Point2D(200, 200), 200, 2, 0);
        assertFalse(new Projectile(shooter, Point2D.ZERO, 5).collide(shooter));
        assertTrue(shooter.isAlive());
        assertEquals(0, shooter.getHitCount());
    }

    @Test
    void hitCountsTrackShotsRatherThanHealthChanges() {
        PlayerShip player = new PlayerShip(PLAYFIELD);
        Projectile shot = shotFrom(player.getPosition());
        int health = player.getHealth();
        player.collideWithProjectile(shot);
        assertTrue(player.getHealth() < health);
        player.heal(5);
        player.heal(-1);
        assertEquals(1, player.getHitCount());
        player.collideWithProjectile(shot);
        assertEquals(2, player.getHitCount());
    }
}
