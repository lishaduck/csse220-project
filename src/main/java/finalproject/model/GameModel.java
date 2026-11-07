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
    private final PlayerShip player;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private final ArrayList<EnemyShip> enemies = new ArrayList<>();
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

    public PlayerShip getPlayer() {
        return player;
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
            projectiles.add(player.spawnProjectile());
        }
        for (EnemyShip enemy : enemies) {
            enemy.update(player.getPosition(), playfield, elapsed, projectiles);
        }
        updateProjectiles(elapsed);
    }

    private void updateProjectiles(double elapsed) {
        for (int i = 0; i < projectiles.size(); i++) {
            if (projectiles.get(i).updatePosition(elapsed)) {
                projectiles.remove(i);
                i--;
            }
        }
    }

    private boolean isRefireable() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            if (projectile.getSource() == player) {
                return projectile.getPosition().getY() < player.getPosition().getY() - 200;
            }
        }
        return true;
    }
}
