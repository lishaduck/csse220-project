package finalproject.ui;

import finalproject.model.PlayerShip;
import finalproject.model.Projectile;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

/** Loads player artwork and draws the player at its model position and size. */
public class LaserView {
    private final Image laserImage;

    public LaserView() {
        URL laserResource = Objects.requireNonNull(
                LaserView.class.getResource("/laser.png"),
                "Missing player ship resource: /laser.png"
        );
        laserImage = new Image(laserResource.toExternalForm());
    }

    public void render(GraphicsContext gc, Projectile laser) {
        Point2D position = laser.getPosition();
        gc.drawImage(
                laserImage,
                position.getX(),
                position.getY(),
                laser.getWidth(),
                laser.getHeight()
        );
    }
}
