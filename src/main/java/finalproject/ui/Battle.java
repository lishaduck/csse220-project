package finalproject.ui;

import finalproject.model.GameModel;
import finalproject.model.HorizontalDirection;
import finalproject.model.PlayerInput;
import javafx.animation.AnimationTimer;
import javafx.geometry.Dimension2D;
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
        PlayerInput input = new PlayerInput(direction, pressedKeys.contains(KeyCode.SPACE));
        model.update(input, elapsed, new Dimension2D(view.getWidth(), view.getHeight()));
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
