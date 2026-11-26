package finalproject.model;

import finalproject.model.ship.EnemyShip;
import finalproject.model.ship.SkiLiftShip;
import finalproject.model.ship.SpreadshotShip;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks enemy behavior through each ship's public update, comparing outcomes rather than tuning values.
 */
class EnemyShipTest {
    private static final Dimension2D PLAYFIELD = new Dimension2D(640, 480);
    private static final Point2D PLAYER_POSITION = new Point2D(300, 420);
    private static final double STEP = 0.05;

    private static double secondsUntilFirstShot(EnemyShip enemy, PowerUps powerUps) {
        List<Projectile> projectiles = new ArrayList<>();
        double elapsed = 0;
        while (projectiles.isEmpty() && elapsed < 60) {
            enemy.update(PLAYER_POSITION, PLAYFIELD, STEP, projectiles, powerUps, List.of());
            elapsed += STEP;
        }
        assertFalse(projectiles.isEmpty());
        return elapsed;
    }

    private static PowerUps slowed() {
        PowerUps powerUps = new PowerUps();
        powerUps.slowEnemies(1.5);
        return powerUps;
    }

    @Test
    void skiLiftsFireStraightDownRepeatedlyButNotRightAway() {
        SkiLiftShip enemy = new SkiLiftShip(PLAYFIELD, new Point2D(100, 100), 0, 20, 0);
        List<Projectile> projectiles = new ArrayList<>();
        enemy.update(PLAYER_POSITION, PLAYFIELD, STEP, projectiles, new PowerUps(), List.of());
        assertTrue(projectiles.isEmpty());

        for (int i = 0; i < 200; i++) {
            enemy.update(PLAYER_POSITION, PLAYFIELD, STEP, projectiles, new PowerUps(), List.of());
        }
        assertTrue(projectiles.size() > 1);
        for (Projectile shot : projectiles) {
            assertTrue(shot.isSourcedFrom(enemy));
            assertEquals(0, shot.getVelocity().getX());
            assertTrue(shot.getVelocity().getY() > 0);
        }
    }

    @Test
    void skiLiftsPatrolAtTheirOwnHeightAndTurnAtTheEdges() {
        SkiLiftShip enemy = new SkiLiftShip(PLAYFIELD, new Point2D(300, 100), 0, 20, 200);
        double previousX = enemy.getPosition().getX();
        boolean reachedEdge = false;
        boolean turned = false;
        for (int i = 0; i < 200 && !turned; i++) {
            enemy.update(PLAYER_POSITION, PLAYFIELD, STEP, new ArrayList<>(), new PowerUps(), List.of());
            double x = enemy.getPosition().getX();
            assertEquals(100, enemy.getPosition().getY());
            if (!reachedEdge) assertTrue(x <= previousX, "Heads toward its target first");
            reachedEdge |= x == 0;
            turned = reachedEdge && x > 0;
            previousX = x;
        }
        assertTrue(turned, "Turns back after reaching the edge");
    }

    @Test
    void spreadshotsFlyToTheirTargetThenFireVolleysTowardThePlayer() {
        Point2D target = new Point2D(300, 100);
        SpreadshotShip enemy = new SpreadshotShip(PLAYFIELD, new Point2D(100, 100), target, 20, 100);
        List<Projectile> projectiles = new ArrayList<>();
        while (!enemy.getPosition().equals(target)) {
            assertTrue(projectiles.isEmpty(), "Holds fire until it arrives");
            enemy.update(PLAYER_POSITION, PLAYFIELD, STEP, projectiles, new PowerUps(), List.of());
        }
        for (int i = 0; i < 60; i++) {
            enemy.update(PLAYER_POSITION, PLAYFIELD, STEP, projectiles, new PowerUps(), List.of());
        }
        assertTrue(projectiles.size() > 3, "Fires several shots per attack");
        Point2D towardPlayer = PLAYER_POSITION.subtract(target);
        assertTrue(projectiles.stream().allMatch(shot -> shot.getVelocity().dotProduct(towardPlayer) > 0));
    }

    @Test
    void slowEnemiesDelaysEveryEnemysAttacks() {
        List<Supplier<EnemyShip>> enemies = List.of(
                () -> new SkiLiftShip(PLAYFIELD, new Point2D(100, 100), 0, 20, 0),
                // Already at its target, so only the volley timing can delay it.
                () -> new SpreadshotShip(PLAYFIELD, new Point2D(200, 100), new Point2D(200, 100), 20, 100));
        for (Supplier<EnemyShip> enemy : enemies) {
            assertTrue(secondsUntilFirstShot(enemy.get(), slowed())
                    > secondsUntilFirstShot(enemy.get(), new PowerUps()));
        }
    }
}
