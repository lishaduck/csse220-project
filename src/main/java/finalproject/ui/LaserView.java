package finalproject.ui;

import finalproject.model.Projectile;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

/** Loads laser artwork and draws each projectile at its model position and size. */
public class LaserView {
    private final Image laserImage;

    public LaserView() {
        URL laserResource = Objects.requireNonNull(
                LaserView.class.getResource("/laser.png"),
                "Missing laser resource: /laser.png"
        );
        laserImage = new Image(laserResource.toExternalForm());
    }

    public void render(GraphicsContext gc, Projectile laser) {
        Point2D position = laser.getPosition();
        gc.drawImage(
                laserImage,
                position.getX(),
                position.getY(),
                Projectile.SIZE.getWidth(),
                Projectile.SIZE.getHeight()
        );
    }
}
