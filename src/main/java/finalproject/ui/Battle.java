package finalproject.ui;

import finalproject.app.Screen;
import finalproject.app.StageManager;
import finalproject.model.GameModel;
import finalproject.model.HorizontalDirection;
import finalproject.model.PlayerInput;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class Battle implements Screen {
    private final GameModel model;
    private final BattleView view;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Scene scene;
    private final AnimationTimer gameLoop;
    private long previousFrame;
    private final ChangeListener<Boolean> focusListener = (observable, wasFocused, isFocused) -> {
        if (!isFocused) {
            clearInput();
        }
    };
    private final ChangeListener<Boolean> showingListener = (observable, wasShowing, isShowing) -> {
        if (isShowing) {
            start();
        } else {
            stop();
        }
    };

    public Battle(GameModel model) {
        this.model = model;
        view = new BattleView(model);
        scene = new Scene(view);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsed = previousFrame == 0
                        ? 0
                        : Math.min((now - previousFrame) / 1_000_000_000.0, 0.05);
                previousFrame = now;
                update(elapsed);
                view.render(model);
                if (!model.getPlayer().isAlive() || model.isWaveCleared()) {
                    MenuScreen next = !model.getPlayer().isAlive()
                            ? new GameOverScreen(model.getScore())
                            : new PowerupScreen(model, Battle.this);
                    next.releaseFireBeforeChoosing(pressedKeys.contains(KeyCode.SPACE));
                    StageManager.from(view).show(next);
                }
            }
        };

        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
        scene.windowProperty().addListener((observable, oldWindow, newWindow) -> {
            stop();
            if (oldWindow != null) {
                oldWindow.focusedProperty().removeListener(focusListener);
                oldWindow.showingProperty().removeListener(showingListener);
            }
            if (newWindow != null) {
                newWindow.focusedProperty().addListener(focusListener);
                newWindow.showingProperty().addListener(showingListener);
                if (newWindow.isShowing()) {
                    start();
                }
            }
        });
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
        previousFrame = 0;
        gameLoop.start();
    }

    public void clearInput() {
        pressedKeys.clear();
    }

    public void stop() {
        gameLoop.stop();
        view.stopAnimations();
        clearInput();
    }

    @Override
    public Scene getView() {
        return scene;
    }
}
