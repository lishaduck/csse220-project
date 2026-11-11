package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

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
    Random random = new Random();
    int wave = 1;
    private Dimension2D playfield;
    private int score;

    public GameModel(Dimension2D playfield) {
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
        player.move(input.direction(), elapsed, playfield);
        if (input.firing() && isRefireable()) {
            projectiles.add(new Projectile(player, powerups.playerProjectileVelocity(), powerups.projectileDamage()));
        }
        enemies.removeIf(enemy -> {
            enemy.update(player.getPosition(), playfield, elapsed, projectiles, powerups);
            return !enemy.isAlive();
        });
        if (enemies.isEmpty()) {
            spawnWave();
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
                            score += powerups.killScore();
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

    public void spawnWave() {
        double spreadshots = random.nextDouble(wave / 5.0);
        if (wave != 5) {
            for (int i = 0; i < wave - (spreadshots * 2); i++) {
                if (i >= spreadshots || wave < 5) {
                    double targetY = random.nextDouble(playfield.getHeight() / 3);
                    enemies.add(
                            new SkiLiftShip(
                                    playfield,
                                    new Point2D(random.nextDouble(playfield.getWidth()), targetY),
                                    0,
                                    wave,
                                    wave * 2.5
                            )
                    );
                } else {
                    enemies.add(
                            new SpreadshotShip(
                                    playfield,
                                    new Point2D(random.nextDouble(playfield.getWidth()), random.nextDouble(playfield.getHeight() / 3)),
                                    new Point2D(random.nextDouble(playfield.getWidth()), random.nextDouble(playfield.getHeight() / 3)),
                                    wave,
                                    wave * 10
                            )
                    );
                }
            }
        } else {
            enemies.add(
                    new SpreadshotShip(
                            playfield,
                            new Point2D(random.nextDouble(playfield.getWidth()), random.nextDouble(playfield.getHeight() / 3)),
                            new Point2D(random.nextDouble(playfield.getWidth()), random.nextDouble(playfield.getHeight() / 3)),
                            wave,
                            wave * 10
                    )
            );
        }
        wave++;
    }
}
