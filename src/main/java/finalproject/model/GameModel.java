package finalproject.model;

import javafx.geometry.Dimension2D;

import java.util.ArrayList;

/**
 * Coordinates the player and the playfield for each game update.
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

    /**
     * Updates movement within the current visible playfield dimensions.
     */
    public void update(HorizontalDirection direction, double elapsed, double width, double height) {
        if (width != playfield.getWidth() || height != playfield.getHeight()) {
            playfield = new Dimension2D(width, height);
            player.resizePlayfield(playfield);
            player.fitToPlayfield();
        }
        player.move(direction, elapsed);
    }

    public void addPlayerProjectile() {
        projectiles.add(player.spawnProjectile());
    }

    public void updateProjectiles(double elapsed) {
        for (int i = 0; i < projectiles.size(); i++) {
            if (i == -1) break;
            if (projectiles.get(i).updatePosition(elapsed)) {
                projectiles.remove(i);
                i--;
            }
        }
    }

    public boolean isRefireable() {
        if (projectiles.isEmpty()) {
            return true;
        }
        return (projectiles.getLast().getPosition().getY() < player.getPosition().getY() - 200);
    }
}
