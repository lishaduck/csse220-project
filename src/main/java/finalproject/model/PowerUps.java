package finalproject.model;

import javafx.geometry.Point2D;

import java.util.EnumSet;
import java.util.Set;

/**
 * Combines active kinds without stacking duplicates or depending on pickup order.
 */
public final class PowerUps {
    private final Set<PowerUp> active = EnumSet.noneOf(PowerUp.class);
    private int projectileDamage = 2;
    private Point2D playerProjectileVelocity = PlayerShip.PROJECTILE_VELOCITY;
    private double refireDistance = 200;
    private int killScoreBonus;
    private int killScoreMultiplier = 1;
    private double enemySpeedDivisor = 1;

    void collect(PowerUp powerUp, GameModel game) {
        if (active.add(powerUp)) {
            powerUp.activate(this);
        }
        powerUp.onCollect(game);
    }

    void upgradeWeapon(int damageBonus, double speedMultiplier, double refireDistanceMultiplier) {
        projectileDamage += damageBonus;
        playerProjectileVelocity = playerProjectileVelocity.multiply(speedMultiplier);
        refireDistance *= refireDistanceMultiplier;
    }

    void upgradeKillReward(int bonus, int multiplier) {
        killScoreBonus += bonus;
        killScoreMultiplier *= multiplier;
    }

    void slowEnemies(double divisor) {
        enemySpeedDivisor *= divisor;
    }

    int projectileDamage() {
        return projectileDamage;
    }

    int killScore() {
        return (10 + killScoreBonus) * killScoreMultiplier;
    }

    Point2D playerProjectileVelocity() {
        return playerProjectileVelocity;
    }

    double refireDistance() {
        return refireDistance;
    }

    double enemyElapsed(double elapsed) {
        return elapsed / enemySpeedDivisor;
    }

    Point2D enemyProjectileVelocity(Point2D velocity) {
        return new Point2D(velocity.getX() / enemySpeedDivisor, velocity.getY() / enemySpeedDivisor);
    }
}
