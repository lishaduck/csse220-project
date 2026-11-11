package finalproject.model;

import java.util.function.Consumer;

/**
 * Named pickups with activation actions and repeatable collection effects.
 */
public enum PowerUp {
    KILL_SCORE(effects -> effects.upgradeKillReward(3, 1)),
    HEALTH {
        @Override
        void onCollect(GameModel game) {
            game.getPlayer().heal(2);
        }
    },
    DAMAGE_AND_SCORE(effects -> {
        effects.upgradeWeapon(1, 1, 1);
        effects.upgradeKillReward(0, 2);
    }),
    FAST_PROJECTILES(effects -> effects.upgradeWeapon(0, 2, 2)),
    GLASS_CANNON(effects -> effects.upgradeWeapon(3, 1, 1)) {
        @Override
        void onCollect(GameModel game) {
            game.getPlayer().heal(-1);
        }
    },
    RAPID_FIRE(effects -> effects.upgradeWeapon(0, 1, 0.5)),
    BONUS_POINTS {
        @Override
        void onCollect(GameModel game) {
            game.addScore(100);
        }
    },
    SLOW_ENEMIES(effects -> effects.slowEnemies(1.5));

    private final Consumer<PowerUps> activation;

    PowerUp() {
        this(effects -> {});
    }

    PowerUp(Consumer<PowerUps> activation) {
        this.activation = activation;
    }

    void activate(PowerUps effects) {
        activation.accept(effects);
    }

    void onCollect(GameModel game) {
    }
}
