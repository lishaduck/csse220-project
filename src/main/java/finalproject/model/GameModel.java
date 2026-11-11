package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Advances player movement, firing, and projectiles within the visible playfield.
 */
public final class GameModel {
    private static int score;
    private final PlayerShip player;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final ArrayList<EnemyShip> enemies = new ArrayList<>();
    private final ArrayList<Integer> powerups = new ArrayList<>();
    private Dimension2D playfield;

    public GameModel(Dimension2D playfield) {
        this.playfield = playfield;
        player = new PlayerShip(playfield);
        enemies.add(
                new SkiLiftShip(
                        playfield,
                        new Point2D(0, 100),
                        new Point2D(200, 100),
                        20,
                        50
                )
        );
        enemies.add(
                new SpreadshotShip(
                        playfield,
                        new Point2D(0, 100),
                        new Point2D(200, 100),
                        20,
                        200
                )
        );
    }

    public static void addScore(int num) {
        score += num;
    }

    public PlayerShip getPlayer() {
        return player;
    }

    /**
     * Returns the number of enemies killed by player projectiles.
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
            int projDamage = 2;
            if (powerups.contains(3)) {
                projDamage += 1;
            }
            if (powerups.contains(5)) {
                projDamage += 3;
            }
            if (powerups.contains(4)) {
                projectiles.add(new Projectile(player, new Point2D(PlayerShip.PROJECTILE_VELOCITY.getX(), PlayerShip.PROJECTILE_VELOCITY.getY() * 2), projDamage));
            } else {
                projectiles.add(new Projectile(player, PlayerShip.PROJECTILE_VELOCITY, projDamage));
            }
        }
        enemies.removeIf(enemy -> {
            enemy.update(player.getPosition(), playfield, elapsed, projectiles, powerups);
            return !enemy.isAlive();
        });
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
                            int addscore = 10;
                            if (powerups.contains(0)) {
                                addscore += 3;
                            }
                            if (powerups.contains(3)) {
                                addscore *= 2;
                            }
                            score += addscore;
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
        int minDis = 200;
        if (powerups.contains(4)) {
            minDis *= 2;
        }
        if (powerups.contains(6)) {
            minDis /= 2;
        }
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.getSource() == player) {
                return projectile.getPosition().getY() < player.getPosition().getY() - minDis;
            }
        }
        return true;
    }

    public void addPowerup(int type){
        powerups.add(type);
        if (type == 1) {
            player.heal(2);
        }
        if (type == 5) {
            player.heal(-1);
        }
        if (type == 7) {
            GameModel.addScore(100);
        }
    }
}
