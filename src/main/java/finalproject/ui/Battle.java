package finalproject.ui;

import finalproject.model.GameModel;
import finalproject.model.HorizontalDirection;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class Battle {
    private final GameModel model;
    private final BattleView view;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Scene scene;
    private final AnimationTimer gameLoop;

    public Battle(GameModel model) {
        this.model = model;
        view = new BattleView(model.getPlayfield());
        scene = new Scene(view);

        gameLoop = new AnimationTimer() {
            private long previousFrame;

            @Override
            public void handle(long now) {
                double elapsed = previousFrame == 0
                        ? 0
                        : Math.min((now - previousFrame) / 1_000_000_000.0, 0.05);
                previousFrame = now;
                update(elapsed);
                view.render(model);
            }
        };

        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
    }

    private void update(double elapsed) {
        boolean left = pressedKeys.contains(KeyCode.A);
        boolean right = pressedKeys.contains(KeyCode.D);
        HorizontalDirection direction = HorizontalDirection.NONE;
        if (left != right) {
            direction = left ? HorizontalDirection.LEFT : HorizontalDirection.RIGHT;
        }
        model.update(direction, elapsed, view.getWidth(), view.getHeight());
        if (pressedKeys.contains(KeyCode.SPACE)) {
            if (model.isRefireable()) {
                model.addPlayerProjectile();
                System.out.println("Added projectile at: " + elapsed);
            } else {
                System.out.println("This attempted projectile spawn was blocked by the cooldown!");
            }

        }

        model.updateProjectiles(elapsed);
    }

    public void start() {
        gameLoop.start();
    }

    public void clearInput() {
        pressedKeys.clear();
    }

    public void stop() {
        gameLoop.stop();
        clearInput();
    }

    public Scene getView() {
        return scene;
    }
}
