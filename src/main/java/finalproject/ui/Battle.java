package finalproject.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;


public class Battle {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Scene scene;


    public Battle() {
        Canvas root = new Canvas();
        var gc = root.getGraphicsContext2D();

        // TODO 4: Set the window title and size.
        scene = new Scene(root.getParent(), 420, 220);

        // TODO 5: Replace this starter view with your real app design.


        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressedKeys.contains(KeyCode.W)) ;
                if (pressedKeys.contains(KeyCode.A)) ;
                if (pressedKeys.contains(KeyCode.S)) ;
                if (pressedKeys.contains(KeyCode.D)) ;
                if (pressedKeys.contains(KeyCode.SPACE)) ;
            }
        };
        gameLoop.start();


        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));

    }


    public Scene getView() {
        return scene;
    }
}
