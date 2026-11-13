package finalproject.ui;

import finalproject.model.Ship;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

/** Blinks a ship's opacity when a projectile hits it. */
final class HitFlash {
    private final Ship ship;
    private final DoubleProperty opacity = new SimpleDoubleProperty(1);
    private final Timeline animation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.25)),
            new KeyFrame(Duration.millis(80), new KeyValue(opacity, 1)),
            new KeyFrame(Duration.millis(160), new KeyValue(opacity, 0.25)),
            new KeyFrame(Duration.millis(240), new KeyValue(opacity, 1))
    );
    private int previousHits;

    HitFlash(Ship ship) {
        this.ship = ship;
    }

    double opacity() {
        if (ship.getHitCount() != previousHits) {
            previousHits = ship.getHitCount();
            opacity.set(0.25);
            animation.playFromStart();
        }
        return opacity.get();
    }

    void stop() {
        animation.stop();
        opacity.set(1);
        previousHits = ship.getHitCount();
    }
}
