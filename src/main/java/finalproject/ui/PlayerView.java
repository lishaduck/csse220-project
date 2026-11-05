package finalproject.ui;

import finalproject.model.PlayerShip;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

/** Loads player artwork and draws the player at its model position and size. */
public class PlayerView {
    private final Image playerImage;

    public PlayerView() {
        URL playerResource = Objects.requireNonNull(
                PlayerView.class.getResource("/PlayerShip.gif"),
                "Missing player ship resource: /PlayerShip.gif"
        );
        playerImage = new Image(playerResource.toExternalForm());
    }

    public void render(GraphicsContext gc, PlayerShip player) {
        Point2D position = player.getPosition();
        gc.drawImage(
                playerImage,
                position.getX(),
                position.getY(),
                player.getWidth(),
                player.getHeight()
        );
    }
}
