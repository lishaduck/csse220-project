package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static finalproject.model.HorizontalDirection.*;
import static finalproject.model.PowerUp.*;
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

        assertTrue(model.isWaveCleared());
        model.spawnWave();
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
        model.addPowerup(BONUS_POINTS);
        assertEquals(100, model.getScore());

        GameModel otherGame = new GameModel(playfield);
        assertEquals(0, otherGame.getScore());
        assertEquals(100, model.getScore());

        model.addPowerup(KILL_SCORE);
        model.addPowerup(DAMAGE_AND_SCORE);
        model.addPowerup(GLASS_CANNON);
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

        otherGame.update(IDLE, 0, playfield);
        EnemyShip otherEnemy = otherGame.getEnemies().getFirst();
        otherEnemy.health = 4;
        otherEnemy.setPosition(otherGame.getPlayer().getPosition(), playfield);
        otherEnemy.target = otherEnemy.getPosition();
        otherGame.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(2, otherEnemy.getHealth());
        assertEquals(0, otherGame.getScore());
        otherGame.addPowerup(DAMAGE_AND_SCORE);
        otherGame.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(-1, otherEnemy.getHealth());
        assertEquals(20, otherGame.getScore());
        assertEquals(126, model.getScore());
    }

    @ParameterizedTest
    @CsvSource({"KILL_SCORE, 2, 13", "DAMAGE_AND_SCORE, 3, 20", "GLASS_CANNON, 5, 10"})
    void damageAndScorePickupsKeepTheirIndividualEffects(PowerUp kind, int damage, int killScore) {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.addPowerup(kind);
        EnemyShip enemy = model.getEnemies().getFirst();
        enemy.health = 1;
        enemy.setPosition(model.getPlayer().getPosition(), playfield);
        enemy.target = enemy.getPosition();

        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(1 - damage, enemy.getHealth());
        assertEquals(killScore, model.getScore());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void repeatedPickupsRepeatInstantEffectsButNotPersistentBonuses(boolean reverseOrder) {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        List<PowerUp> pickups = List.of(KILL_SCORE, DAMAGE_AND_SCORE, GLASS_CANNON);
        if (reverseOrder) {
            pickups = pickups.reversed();
        }
        for (int i = 0; i < 2; i++) {
            model.addPowerup(HEALTH);
            model.addPowerup(BONUS_POINTS);
            for (PowerUp pickup : pickups) {
                model.addPowerup(pickup);
            }
        }
        assertEquals(22, model.getPlayer().getHealth());
        assertEquals(200, model.getScore());

        EnemyShip enemy = model.getEnemies().getFirst();
        enemy.health = 7;
        enemy.setPosition(model.getPlayer().getPosition(), playfield);
        enemy.target = enemy.getPosition();
        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(1, enemy.getHealth());
        assertEquals(200, model.getScore());
        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(-5, enemy.getHealth());
        assertEquals(226, model.getScore());
    }

    @ParameterizedTest
    @CsvSource({"false, false, 600, 200", "true, false, 1200, 400",
            "false, true, 600, 100", "true, true, 1200, 200"})
    void projectileSpeedAndRefireBoundaryComposeWithoutStacking(
            boolean fastProjectiles, boolean rapidFire, double speed, double distance
    ) {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        EnemyShip enemy = model.getEnemies().getFirst();
        enemy.setPosition(new Point2D(10, 10), playfield);
        enemy.target = enemy.getPosition();
        if (rapidFire) {
            model.addPowerup(RAPID_FIRE);
            model.addPowerup(RAPID_FIRE);
        }
        if (fastProjectiles) {
            model.addPowerup(FAST_PROJECTILES);
            model.addPowerup(FAST_PROJECTILES);
        }

        model.update(new PlayerInput(NONE, true), 0, playfield);
        Point2D origin = model.getPlayer().getPosition().add(22, 0);
        model.update(IDLE, distance / speed, playfield);
        assertEquals(origin.add(0, -distance), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(1, model.getProjectiles().size());

        model.update(IDLE, 1 / speed, playfield);
        model.update(new PlayerInput(NONE, true), 0, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(origin, model.getProjectiles().getLast().getPosition());
    }

    @Test
    void slowEnemiesAffectsMovementFiringAndNewShotsWithoutStackingOrLeaking() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        GameModel otherGame = new GameModel(playfield);
        model.addPowerup(SLOW_ENEMIES);
        model.addPowerup(SLOW_ENEMIES);
        for (GameModel game : List.of(model, otherGame)) {
            game.update(IDLE, 0, playfield);
            EnemyShip enemy = game.getEnemies().getFirst();
            enemy.setPosition(new Point2D(100, 100), playfield);
            enemy.target = new Point2D(300, 100);
        }

        model.update(IDLE, 1.5, playfield);
        otherGame.update(IDLE, 1.5, playfield);
        assertEquals(new Point2D(102.5, 100), model.getEnemies().getFirst().getPosition());
        assertEquals(new Point2D(103.75, 100), otherGame.getEnemies().getFirst().getPosition());
        assertTrue(model.getProjectiles().isEmpty());
        model.update(IDLE, 0.75, playfield);
        otherGame.update(IDLE, 0.75, playfield);
        assertTrue(model.getProjectiles().isEmpty());
        assertEquals(1, otherGame.getProjectiles().size());
        model.update(IDLE, 0.75, playfield);
        assertEquals(1, model.getProjectiles().size());
        Projectile shot = model.getProjectiles().getFirst();
        assertEquals(new Point2D(127, 300), shot.getPosition());
        assertEquals(20, model.getPlayer().getHealth());
    }

    @Test
    void multipleProjectilesHittingTheSameDeadEnemyAwardOnlyOneKill() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.getEnemies().getFirst().collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 2));
        assertTrue(model.isWaveCleared());
        model.spawnWave();
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
        assertEquals(new Point2D(355.5, 337), model.getProjectiles().getFirst().getPosition());

        model.update(new PlayerInput(LEFT, true), 0.2, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(355.5, 217), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(LEFT, true), 1.0 / 120, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(355.5, 212), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(LEFT, true), 1.0 / 600, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(355.5, 211), model.getProjectiles().getFirst().getPosition());

        model.update(new PlayerInput(RIGHT, true), 0.05, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(new Point2D(355.5, 181), model.getProjectiles().getFirst().getPosition());
        assertEquals(new Point2D(307.5, 382), model.getProjectiles().getLast().getPosition());

        model.update(IDLE, 0.05, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(new Point2D(355.5, 151), model.getProjectiles().getFirst().getPosition());
        assertEquals(new Point2D(307.5, 352), model.getProjectiles().getLast().getPosition());
        model.update(IDLE, 1, playfield);
        assertTrue(model.getProjectiles().stream().noneMatch(projectile -> projectile.getSource() == model.getPlayer()));
        assertTrue(model.getProjectiles().isEmpty());

        Dimension2D resized = new Dimension2D(499, 320);
        model.update(new PlayerInput(RIGHT, true), 0.125, resized);
        Projectile playerProjectile = model.getProjectiles().stream()
                .filter(projectile -> projectile.getSource() == model.getPlayer())
                .findFirst()
                .orElseThrow();
        assertEquals(new Point2D(345, 177), playerProjectile.getPosition());
    }

    @Test
    void removesConsecutiveCollidingProjectilesAndDeadEnemies() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(IDLE, 0, playfield);
        model.getEnemies().getFirst().collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 2));
        assertTrue(model.isWaveCleared());
        model.spawnWave();
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

        assertTrue(model.isWaveCleared());
        model.spawnWave();
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
        assertTrue(model.isWaveCleared());
        model.spawnWave();
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
        assertEquals(new Point2D(222, 104), skiLiftShot.getPosition());

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
            assertTrue(model.isWaveCleared());
            Point2D pausedPosition = model.getPlayer().getPosition();
            model.update(MOVE_RIGHT, 1, playfield);
            assertEquals(pausedPosition, model.getPlayer().getPosition());
            assertEquals(currentWave, model.getEnemies());
            model.addPowerup(BONUS_POINTS);
            model.spawnWave();
            assertFalse(model.isWaveCleared());
            assertTrue(model.getProjectiles().isEmpty());
            assertTrue(model.getEnemies().stream().noneMatch(currentWave::contains));
            assertTrue(model.getEnemies().stream().allMatch(Ship::isAlive));
        }
        assertFalse(model.getEnemies().isEmpty());
    }

    @Test
    void loadsFileDefinedEnemiesAtPlayfieldRelativePositionsAndRepeatsTheLastWave() {
        Dimension2D playfield = new Dimension2D(800, 600);
        List<Level> levels = LevelLoader.parse(List.of(
                "WAVE Patrol", "SKILIFT 0.2 0.3 0.8 7 12.5",
                "WAVE Boss", "SPREADSHOT 0.7 0.1 0.3 0.25 13 40"));
        GameModel model = new GameModel(playfield, levels);
        model.update(IDLE, 0, playfield);
        EnemyShip patrol = model.getEnemies().getFirst();
        assertTrue(patrol instanceof SkiLiftShip);
        assertEquals(new Point2D(160, 180), patrol.getPosition());
        assertEquals(7, patrol.getHealth());
        model.update(IDLE, 0.4, playfield);
        assertEquals(new Point2D(165, 180), patrol.getPosition());
        patrol.collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 7));

        Dimension2D resized = new Dimension2D(400, 300);
        model.update(IDLE, 0, resized);
        model.spawnWave();
        EnemyShip boss = model.getEnemies().getFirst();
        assertEquals(1, model.getEnemies().size());
        assertTrue(boss instanceof SpreadshotShip);
        assertEquals(new Point2D(280, 30), boss.getPosition());
        assertEquals(13, boss.getHealth());
        model.update(IDLE, 0.5, resized);
        Point2D expected = new Point2D(280, 30).add(new Point2D(-160, 45).normalize().multiply(20));
        assertEquals(expected.getX(), boss.getPosition().getX(), 1e-9);
        assertEquals(expected.getY(), boss.getPosition().getY(), 1e-9);

        for (int repeat = 0; repeat < 5; repeat++) {
            List<EnemyShip> previous = List.copyOf(model.getEnemies());
            previous.getFirst().collideWithProjectile(new Projectile(model.getPlayer(), Point2D.ZERO, 13));
            model.update(IDLE, 0, resized);
            assertTrue(model.isWaveCleared());
            model.spawnWave();
            assertEquals(1, model.getEnemies().size());
            assertFalse(previous.contains(model.getEnemies().getFirst()));
            assertEquals(13, model.getEnemies().getFirst().getHealth());
            assertEquals(new Point2D(280, 30), model.getEnemies().getFirst().getPosition());
        }
        GameModel separateGame = new GameModel(playfield, levels);
        separateGame.update(IDLE, 0, playfield);
        assertTrue(separateGame.getEnemies().getFirst() instanceof SkiLiftShip);
        assertEquals(7, separateGame.getEnemies().getFirst().getHealth());
    }

    @Test
    void fitsTheVisiblePlayfieldWhenTheWindowManagerChangesItsSize() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        Ship player = model.getPlayer();
        model.getPlayer().heal(1000);
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
        player.heal(1000);
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
