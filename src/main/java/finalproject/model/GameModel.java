package finalproject.model;

import javafx.geometry.Dimension2D;

/**
 * Coordinates the player and the playfield for each game update.
 */
public final class GameModel {
    private final PlayerShip player;
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
     * Fits the player to the current visible playfield dimensions.
     */
    public void resizePlayfield(double width, double height) {
        if (width != playfield.getWidth() || height != playfield.getHeight()) {
            playfield = new Dimension2D(width, height);
            player.resizePlayfield(playfield);
            player.fitToPlayfield();
        }
    }

    /**
     * Moves the player in pixels within the playfield.
     */
    public void movePlayer(double distance) {
        player.translateX(distance);
    }
}
