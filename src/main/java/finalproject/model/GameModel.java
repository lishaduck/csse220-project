package finalproject.model;

import javafx.geometry.Dimension2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Advances player movement, firing, and projectiles within the visible playfield.
 */
public final class GameModel {
    private final PlayerShip player;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final ArrayList<EnemyShip> enemies = new ArrayList<>();
    private final PowerUps powerups = new PowerUps();
    private final List<Level> levels;
    private final Random random = new Random();
    private int nextWave;
    private int waveNumber;
    private int wavesStarted;
    private boolean waveStarted;
    private Dimension2D playfield;
    private int score;

    public GameModel(Dimension2D playfield) {
        this(playfield, LevelLoader.load("/levels/levels.txt"));
    }

    public GameModel(Dimension2D playfield, List<Level> levels) {
        this.levels = List.copyOf(levels);
        this.playfield = playfield;
        player = new PlayerShip(playfield);
    }

    public void addScore(int num) {
        score += num;
    }

    public PlayerShip getPlayer() {
        return player;
    }

    /**
     * Returns points earned from enemy kills and powerups.
     */
    public int getScore() {
        return score;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public Dimension2D getPlayfield() {
        return playfield;
    }

    public List<Projectile> getProjectiles() {
        return Collections.unmodifiableList(projectiles);
    }

    public List<EnemyShip> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    /**
     * Fits and moves the player, handles firing, then advances all projectiles.
     */
    public void update(PlayerInput input, double elapsed, Dimension2D visiblePlayfield) {
        if (!visiblePlayfield.equals(playfield)) {
            playfield = visiblePlayfield;
            player.fitToPlayfield(playfield);
        }
        if (!player.isAlive() || isWaveCleared()) {
            return;
        }
        if (!waveStarted) {
            spawnWave();
        }
        player.move(input.direction(), elapsed, playfield);
        if (input.firing() && isRefireable()) {
            projectiles.add(new Projectile(player, powerups.playerProjectileVelocity(), powerups.projectileDamage()));
        }
        for (int i = 0; i < enemies.size(); ) {
            enemies.get(i).update(player.getPosition(), playfield, elapsed, projectiles, powerups, enemies);
            if (!enemies.get(i).isAlive()) {
                enemies.remove(i);
            } else {
                i++;
            }
        }
        updateProjectiles(elapsed);
    }

    private void updateProjectiles(double elapsed) {
        projectiles.removeIf(projectile -> {
            if (projectile.updatePosition(elapsed)) {
                return true;
            }
            if (projectile.getSource() == player) {
                for (EnemyShip enemy : enemies) {
                    boolean wasAlive = enemy.isAlive();
                    if (projectile.collide(enemy)) {
                        if (wasAlive && !enemy.isAlive()) {
                            score += powerups.killScore() * enemy.points();
                        }
                        return true;
                    }
                }
                return false;
            }
            return projectile.collide(player);
        });
    }

    private boolean isRefireable() {
        double minDis = powerups.refireDistance();
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.getSource() == player) {
                return projectile.getPosition().getY() < player.getPosition().getY() - minDis;
            }
        }
        return true;
    }

    public void addPowerup(PowerUp powerUp) {
        powerups.collect(powerUp, this);
    }

    /** Returns up to three distinct pickups with benefits the player can collect. */
    public List<PowerUp> getPowerupChoices() {
        List<PowerUp> choices = new ArrayList<>();
        for (PowerUp powerUp : PowerUp.values()) {
            if (powerups.canOffer(powerUp)) choices.add(powerUp);
        }
        Collections.shuffle(choices, random);
        return List.copyOf(choices.subList(0, Math.min(3, choices.size())));
    }

    /** Offers a pickup after the first cleared wave and every three waves thereafter. */
    public boolean isPowerupDue() {
        return isWaveCleared() && (wavesStarted - 1) % 3 == 0;
    }

    public boolean isWaveCleared() {
        return waveStarted && enemies.stream().noneMatch(Ship::isAlive);
    }

    public void spawnWave() {
        waveStarted = true;
        wavesStarted++;
        enemies.clear();
        projectiles.clear();
        waveNumber = nextWave + 1;
        enemies.addAll(levels.get(nextWave).createEnemies(playfield, random));
        nextWave = Math.min(nextWave + 1, levels.size() - 1);
        score += 100;
    }
}
