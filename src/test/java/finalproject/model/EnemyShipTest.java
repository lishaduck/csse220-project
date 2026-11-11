package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnemyShipTest {
    private static final Dimension2D PLAYFIELD = new Dimension2D(640, 480);
    private static final Point2D PLAYER_POSITION = new Point2D(100, 300);

    @Test
    void enemyTypesUseTheirOwnSpriteAndHitboxDimensions() {
        EnemyShip skiLift = new SkiLiftShip(PLAYFIELD, Point2D.ZERO, 0, 20, 50);
        EnemyShip spreadshot = new SpreadshotShip(PLAYFIELD, Point2D.ZERO, Point2D.ZERO, 20, 50);

        assertEquals(48, skiLift.getWidth());
        assertEquals(36, skiLift.getHeight());
        assertEquals(EnemyShipTest.class.getResource("/EnemyShip.gif"), skiLift.getImage());
        assertEquals(32, spreadshot.getWidth());
        assertEquals(16, spreadshot.getHeight());
        assertEquals(EnemyShipTest.class.getResource("/EnemyShip2.gif"), spreadshot.getImage());
    }

    @Test
    void skiLiftFiresAtItsNamedShotInterval() {
        SkiLiftShip enemy = new SkiLiftShip(
                PLAYFIELD,
                new Point2D(100, 100),
                200,
                20,
                50
        );
        List<Projectile> projectiles = new ArrayList<>();
        List<Integer> powerUps = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles, powerUps);
        }
        assertTrue(projectiles.isEmpty());

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles, powerUps);
        assertEquals(1, projectiles.size());
        assertSame(enemy, projectiles.getFirst().getSource());
        assertEquals(new Point2D(0, 400), projectiles.getFirst().velocity);
    }

    @ParameterizedTest
    @CsvSource({"100, 100", "1000, 444"})
    void skiLiftMovesHorizontallyTowardTargetsToItsLeft(double spawnY, double expectedY) {
        SkiLiftShip enemy = new SkiLiftShip(
                PLAYFIELD,
                new Point2D(300, spawnY),
                100,
                20,
                50
        );
        List<Integer> powerUps = new ArrayList<>();
        enemy.update(PLAYER_POSITION, PLAYFIELD, 1, new ArrayList<>(), powerUps);

        assertEquals(new Point2D(250, expectedY), enemy.getPosition());
    }

    @Test
    void skiLiftsKeepTheirOwnPatrolHeightWhenTurning() {
        SkiLiftShip lower = new SkiLiftShip(
                PLAYFIELD, new Point2D(592, 70), 592, 20, 50
        );
        SkiLiftShip upper = new SkiLiftShip(
                PLAYFIELD, new Point2D(0, 130), 0, 20, 50
        );
        List<Projectile> projectiles = new ArrayList<>();

        lower.update(PLAYER_POSITION, PLAYFIELD, 0, projectiles, List.of());
        upper.update(PLAYER_POSITION, PLAYFIELD, 0, projectiles, List.of());
        lower.update(PLAYER_POSITION, PLAYFIELD, 1, projectiles, List.of());
        upper.update(PLAYER_POSITION, PLAYFIELD, 1, projectiles, List.of());

        assertEquals(new Point2D(542, 70), lower.getPosition());
        assertEquals(new Point2D(50, 130), upper.getPosition());
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
        List<Integer> powerUps = new ArrayList<>();

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles, powerUps);
        assertEquals(3, projectiles.size());
        assertSame(enemy, projectiles.getFirst().getSource());
        assertEquals(new Point2D(0, 200), projectiles.getFirst().velocity);

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles, powerUps);
        assertEquals(5, projectiles.size());

        enemy.update(PLAYER_POSITION, PLAYFIELD, 0.5, projectiles, powerUps);
        assertEquals(8, projectiles.size());
    }
}
