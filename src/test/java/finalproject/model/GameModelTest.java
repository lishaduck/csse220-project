package finalproject.model;

import finalproject.model.ship.EnemyShip;
import finalproject.model.ship.PlayerShip;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static finalproject.model.HorizontalDirection.*;
import static finalproject.model.PowerUp.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Drives the model only through its public API, arranging enemies with level-file lines.
 * Expectations compare behaviors rather than balance numbers, so tuning the game does not break them.
 */
class GameModelTest {
    private static final Dimension2D PLAYFIELD = new Dimension2D(640, 480);
    private static final PlayerInput IDLE = new PlayerInput(NONE, false);
    private static final PlayerInput MOVE_LEFT = new PlayerInput(LEFT, false);
    private static final PlayerInput MOVE_RIGHT = new PlayerInput(RIGHT, false);
    private static final PlayerInput FIRE = new PlayerInput(NONE, true);
    /** A stationary bonus ship away from the player's line of fire; it never shoots. */
    private static final String HARMLESS = "BONUS 0.05 0 0.05 1 0";

    /** A stationary enemy on the player's spawn point, so a shot fired with no elapsed time hits it. */
    private static String atPlayer(String type, int health) {
        Point2D spawn = new PlayerShip(PLAYFIELD).getPosition();
        double x = spawn.getX() / PLAYFIELD.getWidth();
        double y = spawn.getY() / PLAYFIELD.getHeight();
        return type + " " + x + " " + y + " " + x + " " + health + " 0";
    }

    private static GameModel model(String... lines) {
        return new GameModel(PLAYFIELD, LevelLoader.parse(List.of(lines)));
    }

    private static GameModel started(String enemy, PowerUp... pickups) {
        GameModel model = model("WAVE Test", enemy);
        model.update(IDLE, 0, PLAYFIELD);
        for (PowerUp pickup : pickups) model.addPowerup(pickup);
        return model;
    }

    private static void shootUntilWaveCleared(GameModel model) {
        for (int shot = 0; shot < 1000 && !model.isWaveCleared(); shot++) {
            model.update(FIRE, 0, PLAYFIELD);
        }
        assertTrue(model.isWaveCleared());
    }

    private static long playerShots(GameModel model) {
        return model.getProjectiles().stream().filter(shot -> shot.isSourcedFrom(model.getPlayer())).count();
    }

    private static int oneShotDamage(PowerUp... pickups) {
        GameModel model = started(atPlayer("SKILIFT", 1000), pickups);
        EnemyShip target = model.getEnemies().getFirst();
        int before = target.getHealth();
        model.update(FIRE, 0, PLAYFIELD);
        return before - target.getHealth();
    }

    private static int killReward(PowerUp... pickups) {
        GameModel model = started(atPlayer("SKILIFT", 1), pickups);
        int before = model.getScore();
        shootUntilWaveCleared(model);
        return model.getScore() - before;
    }

    private static double shotTravel(PowerUp... pickups) {
        GameModel model = started(HARMLESS, pickups);
        model.update(FIRE, 0, PLAYFIELD);
        double start = model.getProjectiles().getFirst().getPosition().getY();
        model.update(IDLE, 0.1, PLAYFIELD);
        return start - model.getProjectiles().getFirst().getPosition().getY();
    }

    private static double refireDelay(PowerUp... pickups) {
        GameModel model = started(HARMLESS, pickups);
        model.update(FIRE, 0, PLAYFIELD);
        double elapsed = 0;
        while (playerShots(model) < 2 && elapsed < 5) {
            model.update(FIRE, 0.01, PLAYFIELD);
            elapsed += 0.01;
        }
        return elapsed;
    }

    private static double enemyTravel(PowerUp... pickups) {
        GameModel model = started("SKILIFT 0.5 0.2 0 100 100", pickups);
        double start = model.getEnemies().getFirst().getPosition().getX();
        model.update(IDLE, 0.5, PLAYFIELD);
        return Math.abs(model.getEnemies().getFirst().getPosition().getX() - start);
    }

    @Test
    void startsOnTheFirstWaveWithoutPoints() {
        GameModel model = model("WAVE First", atPlayer("SKILIFT", 1), "WAVE Second", atPlayer("SKILIFT", 1));
        assertFalse(model.isWaveCleared());
        model.update(IDLE, 0, PLAYFIELD);
        assertEquals(1, model.getWaveNumber());
        assertEquals(0, model.getScore());
        assertFalse(model.getEnemies().isEmpty());
        assertTrue(model.getPlayer().isAlive());
    }

    @Test
    void movesThePlayerWithInputAndKeepsItInsideThePlayfield() {
        GameModel model = started(HARMLESS);
        Point2D start = model.getPlayer().getPosition();

        model.update(MOVE_LEFT, 0.1, PLAYFIELD);
        assertTrue(model.getPlayer().getPosition().getX() < start.getX());
        model.update(MOVE_RIGHT, 0.2, PLAYFIELD);
        assertTrue(model.getPlayer().getPosition().getX() > start.getX());
        model.update(IDLE, 0.2, PLAYFIELD);
        assertEquals(start.getY(), model.getPlayer().getPosition().getY());

        for (int i = 0; i < 200; i++) model.update(MOVE_LEFT, 0.05, PLAYFIELD);
        assertEquals(0, model.getPlayer().getPosition().getX());
        for (int i = 0; i < 200; i++) model.update(MOVE_RIGHT, 0.05, PLAYFIELD);
        assertEquals(PLAYFIELD.getWidth(), model.getPlayer().getPosition().getX() + model.getPlayer().getWidth());
        model.update(MOVE_LEFT, 0.05, PLAYFIELD);
        assertTrue(model.getPlayer().getPosition().getX() + model.getPlayer().getWidth() < PLAYFIELD.getWidth(),
                "The player can leave the edge it was pressed against");
    }

    @Test
    void fitsThePlayerToAResizedPlayfield() {
        GameModel model = started(HARMLESS);
        for (int i = 0; i < 200; i++) model.update(MOVE_RIGHT, 0.05, PLAYFIELD);

        Dimension2D resized = new Dimension2D(499, 320);
        model.update(IDLE, 0, resized);
        assertEquals(resized, model.getPlayfield());
        Point2D fitted = model.getPlayer().getPosition();
        assertTrue(fitted.getX() + model.getPlayer().getWidth() <= resized.getWidth());
        assertTrue(fitted.getY() + model.getPlayer().getHeight() <= resized.getHeight());
        assertTrue(fitted.getY() > resized.getHeight() / 2, "The player stays near the bottom");

        for (int i = 0; i < 200; i++) model.update(MOVE_RIGHT, 0.05, resized);
        assertEquals(resized.getWidth(), model.getPlayer().getPosition().getX() + model.getPlayer().getWidth());
    }

    @Test
    void firesUpwardAndRefiresOnlyAfterTheLastShotTravels() {
        GameModel model = started(HARMLESS);
        model.update(FIRE, 0, PLAYFIELD);
        assertEquals(1, playerShots(model));
        double start = model.getProjectiles().getFirst().getPosition().getY();

        model.update(FIRE, 0, PLAYFIELD);
        assertEquals(1, playerShots(model), "Holding fire must not shoot every frame");
        model.update(IDLE, 0.05, PLAYFIELD);
        assertTrue(model.getProjectiles().getFirst().getPosition().getY() < start);

        assertTrue(refireDelay() < 5, "Holding fire shoots again once the last shot has travelled");
        for (int i = 0; i < 100; i++) model.update(IDLE, 0.05, PLAYFIELD);
        assertTrue(model.getProjectiles().isEmpty(), "Shots leave once they pass the top");
    }

    @Test
    void scoresKillsButNotDamage() {
        GameModel model = started(atPlayer("SKILIFT", 1000));
        EnemyShip enemy = model.getEnemies().getFirst();
        model.update(FIRE, 0, PLAYFIELD);
        assertEquals(1, enemy.getHitCount());
        assertTrue(enemy.getHealth() < 1000);
        assertEquals(0, model.getScore());

        assertTrue(killReward() > 0);
    }

    @Test
    void bonusShipsAreWorthMoreThanOrdinaryEnemies() {
        GameModel bonus = started(atPlayer("BONUS", 1));
        shootUntilWaveCleared(bonus);
        assertTrue(bonus.getScore() > killReward());
    }

    @Test
    void advancingToALaterWaveAwardsPointsAndStartsFresh() {
        GameModel model = model("WAVE First", atPlayer("SKILIFT", 1), "WAVE Second", atPlayer("SKILIFT", 1));
        model.update(IDLE, 0, PLAYFIELD);
        shootUntilWaveCleared(model);
        List<EnemyShip> cleared = List.copyOf(model.getEnemies());
        int score = model.getScore();

        model.spawnWave();
        assertEquals(2, model.getWaveNumber());
        assertTrue(model.getScore() > score);
        assertFalse(model.isWaveCleared());
        assertTrue(model.getProjectiles().isEmpty());
        assertTrue(model.getEnemies().stream().noneMatch(cleared::contains));
    }

    @Test
    void pausesWhileAWaveIsClearedOrThePlayerIsDead() {
        GameModel cleared = model("WAVE First", atPlayer("SKILIFT", 1), "WAVE Second", atPlayer("SKILIFT", 1));
        cleared.update(IDLE, 0, PLAYFIELD);
        shootUntilWaveCleared(cleared);
        Point2D paused = cleared.getPlayer().getPosition();
        cleared.update(MOVE_RIGHT, 1, PLAYFIELD);
        assertEquals(paused, cleared.getPlayer().getPosition());
        assertEquals(1, cleared.getWaveNumber(), "Only spawnWave starts the next wave");

        GameModel dead = started("SKILIFT 0.5 0.2 0 100 100");
        dead.getPlayer().heal(-dead.getPlayer().getHealth());
        assertFalse(dead.getPlayer().isAlive());
        Point2D player = dead.getPlayer().getPosition();
        Point2D enemy = dead.getEnemies().getFirst().getPosition();
        dead.update(MOVE_RIGHT, 1, PLAYFIELD);
        assertEquals(player, dead.getPlayer().getPosition());
        assertEquals(enemy, dead.getEnemies().getFirst().getPosition());
    }

    @Test
    void offersPowerupsAfterWavesOneFourSevenAndTenAndWinsOnlyAfterTheLastWave() {
        GameModel model = new GameModel(PLAYFIELD, Collections.nCopies(10,
                LevelLoader.parse(List.of("WAVE Test", atPlayer("SKILIFT", 1))).getFirst()));
        assertFalse(model.isPowerupDue());
        model.update(IDLE, 0, PLAYFIELD);
        for (int wave = 1; wave <= 10; wave++) {
            assertFalse(model.isPowerupDue(), "No offer during wave " + wave);
            assertFalse(model.hasWon(), "No win during wave " + wave);
            shootUntilWaveCleared(model);
            assertEquals(List.of(1, 4, 7, 10).contains(wave), model.isPowerupDue(), "Cleared wave " + wave);
            assertEquals(wave == 10, model.hasWon(), "Cleared wave " + wave);
            if (wave < 10) model.spawnWave();
        }
    }

    @Test
    void enemyShotsDamageThePlayerButNotTheirShooter() {
        GameModel model = started(atPlayer("SKILIFT", 100));
        EnemyShip shooter = model.getEnemies().getFirst();
        int health = model.getPlayer().getHealth();
        for (int i = 0; i < 60; i++) model.update(IDLE, 0.05, PLAYFIELD);

        assertTrue(model.getPlayer().getHitCount() > 0);
        assertTrue(model.getPlayer().getHealth() < health);
        assertEquals(0, shooter.getHitCount());
    }

    @Test
    void enemyShotsHitOtherEnemiesInTheirWay() {
        Point2D spawn = new PlayerShip(PLAYFIELD).getPosition();
        double x = spawn.getX() / PLAYFIELD.getWidth();
        GameModel model = model("WAVE Friendly fire",
                "SKILIFT " + x + " 0.1 " + x + " 100 0",
                "SKILIFT " + x + " 0.4 " + x + " 100 0");
        model.update(IDLE, 0, PLAYFIELD);
        EnemyShip upper = model.getEnemies().getFirst();
        EnemyShip lower = model.getEnemies().getLast();
        for (int i = 0; i < 60; i++) model.update(IDLE, 0.05, PLAYFIELD);

        assertTrue(lower.getHitCount() > 0, "The upper ship's shots hit the ship below it");
        assertEquals(0, upper.getHitCount());
    }

    @Test
    void bonusShipsHoldAWaveOpenOnlyWhenTheWaveHasNothingElse() {
        GameModel mixed = model("WAVE Mixed", atPlayer("SKILIFT", 1), HARMLESS);
        mixed.update(IDLE, 0, PLAYFIELD);
        shootUntilWaveCleared(mixed);
        assertTrue(mixed.getEnemies().stream().anyMatch(EnemyShip::isAlive), "The bonus ship is still flying");

        GameModel bonusOnly = started(HARMLESS);
        bonusOnly.update(IDLE, 0.05, PLAYFIELD);
        assertFalse(bonusOnly.isWaveCleared());
        GameModel bonusTarget = started(atPlayer("BONUS", 1));
        shootUntilWaveCleared(bonusTarget);
    }

    @ParameterizedTest
    @EnumSource(PowerUp.class)
    void offersOnlyUnownedUpgradesAndRepeatableBenefits(PowerUp remaining) {
        GameModel model = started(HARMLESS);
        assertEquals(3, model.getPowerupChoices().size());
        assertEquals(3, model.getPowerupChoices().stream().distinct().count());
        for (PowerUp powerUp : PowerUp.values()) {
            if (powerUp != remaining) model.addPowerup(powerUp);
        }
        assertEquals(EnumSet.of(HEALTH, BONUS_POINTS, remaining), EnumSet.copyOf(model.getPowerupChoices()));

        model.addPowerup(remaining);
        assertEquals(EnumSet.of(HEALTH, BONUS_POINTS), EnumSet.copyOf(model.getPowerupChoices()));
        assertEquals(3, started(HARMLESS).getPowerupChoices().size(), "Pickups stay with their own game");
    }

    @Test
    void repairAndBonusPointsApplyEveryTimeTheyAreCollected() {
        GameModel model = started(HARMLESS);
        List<Integer> health = new ArrayList<>(List.of(model.getPlayer().getHealth()));
        List<Integer> score = new ArrayList<>(List.of(model.getScore()));
        for (int i = 0; i < 2; i++) {
            model.addPowerup(HEALTH);
            model.addPowerup(BONUS_POINTS);
            health.add(model.getPlayer().getHealth());
            score.add(model.getScore());
        }
        assertTrue(health.get(1) > health.get(0));
        assertEquals(health.get(1) - health.get(0), health.get(2) - health.get(1));
        assertTrue(score.get(1) > score.get(0));
        assertEquals(score.get(1) - score.get(0), score.get(2) - score.get(1));
    }

    @ParameterizedTest
    @EnumSource(names = {"DAMAGE_AND_SCORE", "GLASS_CANNON"})
    void damageUpgradesHitHarderWithoutStacking(PowerUp upgrade) {
        assertTrue(oneShotDamage(upgrade) > oneShotDamage());
        assertEquals(oneShotDamage(upgrade), oneShotDamage(upgrade, upgrade));
    }

    @Test
    void glassCannonCostsHealth() {
        GameModel model = started(HARMLESS);
        int health = model.getPlayer().getHealth();
        model.addPowerup(GLASS_CANNON);
        assertTrue(model.getPlayer().getHealth() < health);
    }

    @ParameterizedTest
    @EnumSource(names = {"KILL_SCORE", "DAMAGE_AND_SCORE"})
    void killRewardUpgradesScoreMoreWithoutStacking(PowerUp upgrade) {
        assertTrue(killReward(upgrade) > killReward());
        assertEquals(killReward(upgrade), killReward(upgrade, upgrade));
    }

    @Test
    void fastProjectilesTravelFartherWithoutStacking() {
        assertTrue(shotTravel(FAST_PROJECTILES) > shotTravel());
        assertEquals(shotTravel(FAST_PROJECTILES), shotTravel(FAST_PROJECTILES, FAST_PROJECTILES));
    }

    @Test
    void rapidFireRefiresSoonerWithoutStacking() {
        assertTrue(refireDelay(RAPID_FIRE) < refireDelay());
        assertEquals(refireDelay(RAPID_FIRE), refireDelay(RAPID_FIRE, RAPID_FIRE), 1e-9);
    }

    @Test
    void slowEnemiesSlowsEnemyMovementWithoutStacking() {
        assertTrue(enemyTravel(SLOW_ENEMIES) < enemyTravel());
        assertEquals(enemyTravel(SLOW_ENEMIES), enemyTravel(SLOW_ENEMIES, SLOW_ENEMIES), 1e-9);
    }
}
