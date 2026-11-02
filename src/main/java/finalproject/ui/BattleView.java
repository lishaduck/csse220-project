package finalproject.ui;

import finalproject.model.Ship;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Objects;

/** Owns the playfield's canvas, layout, and drawing. */
class BattleView extends Pane {
    private final Canvas canvas = new Canvas();
    private final Image playerImage;

    BattleView(Dimension2D playfield) {
        URL playerResource = BattleView.class.getResource("/PlayerShip.gif");
        Objects.requireNonNull(playerResource, "Missing player ship resource: /PlayerShip.gif");
        playerImage = new Image(playerResource.toExternalForm());
        setPrefSize(playfield.getWidth(), playfield.getHeight());
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }

    void render(Ship player) {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#dce8ef"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#152536"));
        gc.setFont(Font.font(16));
        gc.fillText("Hold A / D to move", 24, 32);

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
