package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static finalproject.model.HorizontalDirection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTest {
    private static final PlayerInput MOVE_LEFT = new PlayerInput(LEFT, false);
    private static final PlayerInput MOVE_RIGHT = new PlayerInput(RIGHT, false);
    private static final PlayerInput IDLE = new PlayerInput(NONE, false);

    @Test
    void startsPlayersWithTwentyHealthAndKeepsDamageSeparateFromScore() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        PlayerShip player = model.getPlayer();
        assertEquals(20, player.getHealth());
        assertTrue(player.isAlive());

        player.collideWithProjectile(new Projectile(model.getEnemies().getFirst(), Point2D.ZERO, 2));
        assertEquals(18, player.getHealth());
        assertTrue(player.isAlive());
        assertEquals(0, model.getScore());
        assertEquals(20, new GameModel(playfield).getPlayer().getHealth());
    }

    @Test
    void scoresKillsRatherThanDamageAndKeepsScoreReadableAfterRemoval() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        EnemyShip enemy = model.getEnemies().getFirst();
        enemy.health = 3;
        enemy.setPosition(model.getPlayer().getPosition(), playfield);
        enemy.target = enemy.getPosition();
        assertEquals(0, model.getScore());

        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(1, enemy.getHealth());
        assertEquals(0, model.getScore());

        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(-1, enemy.getHealth());
        assertFalse(enemy.isAlive());
        assertEquals(10, model.getScore());
        assertEquals(10, model.getScore());
        assertEquals(1, model.getEnemies().size());

        for (int i = 0; i < 3; i++) {
            model.update(IDLE, 0, playfield);
            assertEquals(10, model.getScore());
            assertEquals(2, model.getEnemies().size());
        }
        assertEquals(0, new GameModel(playfield).getScore());
    }

    @Test
    void powerupDamageAndScoreBonusesStayWithTheirGame() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.addPowerup(7);
        assertEquals(100, model.getScore());

        GameModel otherGame = new GameModel(playfield);
        assertEquals(0, otherGame.getScore());
        assertEquals(100, model.getScore());

        model.addPowerup(0);
        model.addPowerup(3);
        model.addPowerup(5);
        EnemyShip enemy = model.getEnemies().getFirst();
        enemy.health = 7;
        enemy.setPosition(model.getPlayer().getPosition(), playfield);
        enemy.target = enemy.getPosition();

        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(1, enemy.getHealth());
        assertEquals(100, model.getScore());
        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(-5, enemy.getHealth());
        assertEquals(126, model.getScore());
        model.update(IDLE, 0, playfield);
        assertEquals(126, model.getScore());
        assertEquals(0, otherGame.getScore());
    }

    @Test
    void multipleProjectilesHittingTheSameDeadEnemyAwardOnlyOneKill() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.getEnemies().getFirst().collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 2));
        model.update(IDLE, 0, playfield);
        assertEquals(2, model.getEnemies().size());
        model.update(new PlayerInput(NONE, true), 0, playfield);
        model.update(new PlayerInput(NONE, true), 0.34, playfield);
        model.update(new PlayerInput(NONE, true), 0, playfield);
        model.getProjectiles().getLast().updatePosition(0.34);

        EnemyShip first = model.getEnemies().getFirst();
        EnemyShip second = model.getEnemies().getLast();
        first.health = 1;
        for (EnemyShip enemy : model.getEnemies()) {
            enemy.setPosition(model.getProjectiles().getFirst().getPosition(), playfield);
            enemy.target = enemy.getPosition();
        }

        model.update(IDLE, 0, playfield);
        assertTrue(model.getProjectiles().isEmpty());
        assertEquals(-3, first.getHealth());
        assertEquals(2, second.getHealth());
        assertEquals(10, model.getScore());
        model.update(IDLE, 0, playfield);
        assertEquals(10, model.getScore());
    }

    @Test
    void preservesProjectileSpawningCooldownAndRemovalAlongsidePlayerMovement() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        EnemyShip enemy = model.getEnemies().getFirst();
        enemy.setPosition(new Point2D(100, 100), playfield);
        enemy.target = enemy.getPosition();
        model.update(new PlayerInput(RIGHT, true), 0.125, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(333.5, 337), model.getProjectiles().getFirst().getPosition());

        model.update(new PlayerInput(LEFT, true), 0.2, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(333.5, 217), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(LEFT, true), 1.0 / 120, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(333.5, 212), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(LEFT, true), 1.0 / 600, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(333.5, 211), model.getProjectiles().getFirst().getPosition());

        model.update(new PlayerInput(RIGHT, true), 0.05, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(new Point2D(333.5, 181), model.getProjectiles().getFirst().getPosition());
        assertEquals(new Point2D(285.5, 382), model.getProjectiles().getLast().getPosition());

        model.update(IDLE, 0.05, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(new Point2D(333.5, 151), model.getProjectiles().getFirst().getPosition());
        assertEquals(new Point2D(285.5, 352), model.getProjectiles().getLast().getPosition());
        model.update(IDLE, 1, playfield);
        assertTrue(model.getProjectiles().stream().noneMatch(projectile -> projectile.getSource() == model.getPlayer()));
        assertTrue(model.getProjectiles().isEmpty());

        Dimension2D resized = new Dimension2D(499, 320);
        model.update(new PlayerInput(RIGHT, true), 0.125, resized);
        Projectile playerProjectile = model.getProjectiles().stream()
                .filter(projectile -> projectile.getSource() == model.getPlayer())
                .findFirst()
                .orElseThrow();
        assertEquals(new Point2D(323, 177), playerProjectile.getPosition());
    }

    @Test
    void removesConsecutiveCollidingProjectilesAndDeadEnemies() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.getEnemies().getFirst().collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 2));
        model.update(IDLE, 0, playfield);
        assertEquals(2, model.getEnemies().size());
        for (EnemyShip enemy : model.getEnemies()) {
            enemy.setPosition(new Point2D(100, 100), playfield);
            enemy.target = enemy.getPosition();
        }
        model.update(new PlayerInput(NONE, true), 0, playfield);
        model.update(new PlayerInput(NONE, true), 0.34, playfield);
        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(2, model.getProjectiles().size());

        EnemyShip first = model.getEnemies().getFirst();
        EnemyShip second = model.getEnemies().getLast();
        assertTrue(first.isAlive());
        assertTrue(second.isAlive());
        first.setPosition(model.getProjectiles().getFirst().getPosition(), playfield);
        second.setPosition(model.getProjectiles().getLast().getPosition(), playfield);
        first.target = first.getPosition();
        second.target = second.getPosition();

        model.update(IDLE, 0, playfield);
        assertTrue(model.getProjectiles().isEmpty());
        assertTrue(model.getEnemies().stream().noneMatch(Ship::isAlive));
        assertEquals(20, model.getScore());

        model.update(IDLE, 0, playfield);
        assertFalse(model.getEnemies().contains(first));
        assertFalse(model.getEnemies().contains(second));
        assertFalse(model.getEnemies().isEmpty());
        assertTrue(model.getEnemies().stream().allMatch(Ship::isAlive));
        assertEquals(20, model.getScore());
    }

    @Test
    void enemyProjectilesIgnoreAlliesAndDamageThePlayer() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.getEnemies().getFirst().collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 2));
        model.update(IDLE, 0, playfield);
        assertEquals(2, model.getEnemies().size());
        Point2D origin = new Point2D(200, 100);
        for (EnemyShip enemy : model.getEnemies()) {
            enemy.setPosition(origin, playfield);
            enemy.target = origin;
            enemy.health = 2;
        }
        model.getPlayer().health = 4;

        model.update(IDLE, 1.99, playfield);
        model.update(IDLE, 0.01, playfield);
        assertTrue(model.getEnemies().stream().allMatch(Ship::isAlive));
        assertTrue(model.getPlayer().isAlive());
        Projectile skiLiftShot = model.getProjectiles().getLast();
        assertEquals(new Point2D(200, 104), skiLiftShot.getPosition());

        model.getPlayer().setPosition(skiLiftShot.getPosition(), playfield);
        model.update(IDLE, 0, playfield);
        assertFalse(model.getPlayer().isAlive());
        assertEquals(0, model.getPlayer().getHealth());
        assertEquals(0, model.getScore());
        assertFalse(model.getProjectiles().contains(skiLiftShot));
        assertTrue(model.getEnemies().stream().allMatch(Ship::isAlive));
    }

    @Test
    void advancesClearedWavesThroughTheFifthWaveBoss() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        Projectile shot = new Projectile(model.getPlayer(), Point2D.ZERO, 2);
        model.update(IDLE, 0, playfield);

        for (int wave = 1; wave <= 5; wave++) {
            assertFalse(model.getEnemies().isEmpty());
            if (wave == 5) {
                assertEquals(1, model.getEnemies().size());
                assertTrue(model.getEnemies().getFirst() instanceof SpreadshotShip);
            } else {
                assertTrue(model.getEnemies().stream().allMatch(enemy -> enemy instanceof SkiLiftShip));
            }
            List<EnemyShip> currentWave = List.copyOf(model.getEnemies());
            model.update(IDLE, 0, playfield);
            assertEquals(currentWave, model.getEnemies());
            for (EnemyShip enemy : currentWave) {
                for (int damage = 0; damage < wave; damage += 2) {
                    assertTrue(enemy.isAlive());
                    enemy.collideWithProjectile(shot);
                }
                assertFalse(enemy.isAlive());
            }
            model.update(IDLE, 0, playfield);
            assertTrue(model.getEnemies().stream().noneMatch(currentWave::contains));
            assertTrue(model.getEnemies().stream().allMatch(Ship::isAlive));
        }
        assertFalse(model.getEnemies().isEmpty());
    }

    @Test
    void fitsTheVisiblePlayfieldWhenTheWindowManagerChangesItsSize() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        Ship player = model.getPlayer();
        model.update(MOVE_RIGHT, 10, playfield);

        Dimension2D resized = new Dimension2D(499, 320);
        model.update(IDLE, 0, resized);
        assertEquals(resized, model.getPlayfield());
        assertEquals(499, player.getPosition().getX() + PlayerShip.SIZE.getWidth());
        assertEquals(296, player.getPosition().getY() + PlayerShip.SIZE.getHeight());
        for (int i = 0; i < 1000; i++) {
            model.update(MOVE_RIGHT, 0.05, resized);
        }
        assertEquals(499, player.getPosition().getX() + PlayerShip.SIZE.getWidth());
        model.update(MOVE_LEFT, 0.125, resized);
        assertEquals(499 - PlayerShip.SIZE.getWidth() - 37.5, player.getPosition().getX());

        Dimension2D expanded = new Dimension2D(800, 600);
        model.update(MOVE_RIGHT, 10, expanded);
        assertEquals(800, player.getPosition().getX() + PlayerShip.SIZE.getWidth());
        model.update(MOVE_LEFT, 10, expanded);
        assertEquals(0, player.getPosition().getX());
        model.update(MOVE_RIGHT, 0.125, expanded);
        assertEquals(37.5, player.getPosition().getX());
    }

    @Test
    void movesHorizontallyAndCanLeaveBothBoundariesAfterRepeatedInput() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        PlayerShip player = model.getPlayer();
        double initialX = player.getPosition().getX();
        double initialY = player.getPosition().getY();
        assertTrue(initialX > 0);
        assertTrue(initialX + PlayerShip.SIZE.getWidth() < 640);
        assertTrue(initialY >= 0);
        assertTrue(initialY + PlayerShip.SIZE.getHeight() <= 480);

        model.update(MOVE_LEFT, 0.125, playfield);
        assertEquals(initialX - 37.5, player.getPosition().getX());
        model.update(MOVE_RIGHT, 0.0625, playfield);
        assertEquals(initialX - 18.75, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.update(MOVE_LEFT, 0.05, playfield);
        }
        assertEquals(0, player.getPosition().getX());
        model.update(MOVE_RIGHT, 0.125, playfield);
        assertEquals(37.5, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.update(MOVE_RIGHT, 0.075, playfield);
        }
        assertEquals(
                640,
                player.getPosition().getX() + PlayerShip.SIZE.getWidth()
        );
        model.update(MOVE_LEFT, 0.0625, playfield);
        assertEquals(
                640 - PlayerShip.SIZE.getWidth() - 18.75,
                player.getPosition().getX()
        );
        model.update(IDLE, 0.2, playfield);
        assertEquals(
                640 - PlayerShip.SIZE.getWidth() - 18.75,
                player.getPosition().getX()
        );
        assertEquals(initialY, player.getPosition().getY());
    }
}
