package finalproject.model;

import javafx.geometry.Dimension2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Advances player movement, firing, and projectiles within the visible playfield.
 */
public final class GameModel {
    private final PlayerShip player;
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
    private Dimension2D playfield;

    public GameModel(Dimension2D playfield) {
        this.playfield = playfield;
        player = new PlayerShip(playfield);
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

    /**
     * Fits and moves the player, handles firing, then advances all projectiles.
     */
    public void update(PlayerInput input, double elapsed, Dimension2D visiblePlayfield) {
        if (!visiblePlayfield.equals(playfield)) {
            playfield = visiblePlayfield;
            player.resizePlayfield(playfield);
            player.fitToPlayfield();
        }
        player.move(input.direction(), elapsed);
        if (input.firing() && isRefireable()) {
            projectiles.add(player.spawnProjectile());
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
        if (projectiles.isEmpty()) {
            return true;
        }
        return (projectiles.getLast().getPosition().getY() < player.getPosition().getY() - 200);
    }
}
