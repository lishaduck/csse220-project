package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyShipTest {
    private static final Dimension2D PLAYFIELD = new Dimension2D(640, 480);
    private static final Point2D PLAYER_POSITION = new Point2D(100, 300);

    @Test
    void skiLiftFiresAtItsNamedShotInterval() {
        SkiLiftShip enemy = new SkiLiftShip(
                PLAYFIELD,
                new Point2D(100, 100),
                new Point2D(200, 100),
                20,
                50
        );
        List<Projectile> projectiles = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles);
        }
        assertTrue(projectiles.isEmpty());

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles);
        assertEquals(1, projectiles.size());
        assertSame(enemy, projectiles.getFirst().getSource());
        assertEquals(new Point2D(0, 400), projectiles.getFirst().velocity);
    }

    @Test
    void skiLiftMovesTowardTargetsToItsLeft() {
        SkiLiftShip enemy = new SkiLiftShip(
                PLAYFIELD,
                new Point2D(300, 100),
                new Point2D(100, 100),
                20,
                50
        );

        enemy.update(PLAYER_POSITION, PLAYFIELD, 1, new ArrayList<>());

        assertEquals(new Point2D(250, 100), enemy.getPosition());
    }

    @Test
    void spreadshotFiresWhenPositionMatchesTargetCoordinates() {
        SpreadshotShip enemy = new SpreadshotShip(
                PLAYFIELD,
                new Point2D(100, 100),
                new Point2D(100, 100),
                20,
                50
        );
        List<Projectile> projectiles = new ArrayList<>();

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles);
        assertEquals(3, projectiles.size());
        assertSame(enemy, projectiles.getFirst().getSource());
        assertEquals(new Point2D(0, 200), projectiles.getFirst().velocity);

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles);
        assertEquals(5, projectiles.size());

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles);
        assertEquals(8, projectiles.size());
    }
}
