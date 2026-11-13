package finalproject.app;

import finalproject.model.GameModel;
import finalproject.model.LevelLoader;
import finalproject.ui.Battle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs with a graphical display, including xvfb-run on Linux.
 */
@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class StageManagerTest {
    @Test
    void switchesScreensStopsTheBattleAndStartsAnotherGame() throws Exception {
        Platform.startup(() -> Platform.setImplicitExit(false));
        try {
            assertEnemyGifAnimates();
            GameModel model = new GameModel(new Dimension2D(640, 480));
            double initialX = model.getPlayer().getPosition().getX();
            Battle battle = onFxThread(() -> new Battle(model));
            Stage stage = onFxThread(Stage::new);
            StageManager screens = onFxThread(() -> new StageManager(stage));
            onFxThread(() -> {
                screens.show(battle);
                return null;
            });

            long readyDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (onFxThread(() -> !stage.isFocused() || model.getEnemies().isEmpty())
                    && System.nanoTime() < readyDeadline) {
                Thread.sleep(20);
            }
            onFxThread(() -> {
                assertTrue(stage.isFocused(), "Wait for window focus before sending input");
                assertFalse(model.getEnemies().isEmpty(), "The battle must advance frames");
                stage.getScene().getOnKeyPressed().handle(new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", KeyCode.D, false, false, false, false));
                return null;
            });
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (onFxThread(() -> model.getPlayer().getPosition().getX()) <= initialX
                    && System.nanoTime() < deadline) {
                Thread.sleep(20);
            }
            Button startButton = onFxThread(() -> new Button("Start"));
            Scene menuScene = onFxThread(() -> {
                startButton.setOnAction(event -> StageManager.from(startButton).startNewGame());
                return new Scene(new StackPane(startButton), 640, 480);
            });
            Screen menu = () -> menuScene;
            double stoppedX = onFxThread(() -> {
                assertTrue(model.getPlayer().getPosition().getX() > initialX,
                        "The displayed battle must run and respond to input: "
                                + model.getPlayer().getPosition() + " in " + model.getPlayfield());
                StageManager.from(stage.getScene().getRoot()).show(menu);
                assertSame(menu.getView(), stage.getScene());
                assertSame(screens, StageManager.from(startButton));
                return model.getPlayer().getPosition().getX();
            });

            Point2D stoppedEnemy = onFxThread(() -> model.getEnemies().getFirst().getPosition());
            Thread.sleep(200);
            double expectedX = stoppedX;
            onFxThread(() -> {
                assertEquals(expectedX, model.getPlayer().getPosition().getX(),
                        "A hidden battle must not keep advancing its model");
                assertEquals(stoppedEnemy, model.getEnemies().getFirst().getPosition(),
                        "Hidden enemies must stop too, not just player input");
                StageManager.from(startButton).show(battle);
                return null;
            });
            Thread.sleep(200);
            onFxThread(() -> {
                assertEquals(expectedX, model.getPlayer().getPosition().getX(),
                        "Resuming a battle must not retain keys held before navigation");
                screens.show(menu);
                startButton.fire();
                Scene firstGame = stage.getScene();
                assertNotSame(menu.getView(), firstGame);
                StageManager.from(firstGame.getRoot()).startNewGame();
                assertNotSame(firstGame, stage.getScene());

                Scene primaryScene = stage.getScene();
                Stage otherStage = new Stage();
                StageManager otherScreens = new StageManager(otherStage);
                otherScreens.show(() -> new Scene(new StackPane(), 640, 480));
                assertSame(otherScreens, StageManager.from(otherStage.getScene().getRoot()));
                StageManager.from(otherStage.getScene().getRoot()).startNewGame();
                assertSame(primaryScene, stage.getScene(), "Navigation must stay in its own window");
                otherStage.close();

                screens.show(battle);
                battle.getView().getOnKeyPressed().handle(new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", KeyCode.D, false, false, false, false));
                stage.close();
                return null;
            });
            Point2D closedEnemy = onFxThread(() -> model.getEnemies().getFirst().getPosition());
            Thread.sleep(200);
            onFxThread(() -> {
                assertEquals(expectedX, model.getPlayer().getPosition().getX(),
                        "Closing the window must stop the active battle");
                assertEquals(closedEnemy, model.getEnemies().getFirst().getPosition());
                return null;
            });
        } finally {
            Platform.exit();
        }
    }

    private static void assertEnemyGifAnimates() throws Exception {
        CompletableFuture<Set<Integer>> result = new CompletableFuture<>();
        onFxThread(() -> {
            GameModel model = new GameModel(new Dimension2D(640, 480), LevelLoader.parse(List.of(
                    "WAVE Animation", "SKILIFT 0.25 0.25 0.25 2 0")));
            Battle battle = new Battle(model);
            Stage stage = new Stage();
            new StageManager(stage).show(battle);
            new AnimationTimer() {
                private long start;
                private final Set<Integer> frames = new HashSet<>();

                @Override
                public void handle(long now) {
                    try {
                        if (start == 0) start = now;
                        if (now - start < 200_000_000L) return;
                        int[] pixels = new int[48 * 36];
                        battle.getView().getRoot().snapshot(null, null).getPixelReader().getPixels(
                                160, 120, 48, 36, PixelFormat.getIntArgbInstance(), pixels, 0, 48);
                        frames.add(Arrays.hashCode(pixels));
                        if (now - start >= 1_200_000_000L) {
                            stop();
                            stage.close();
                            result.complete(frames);
                        }
                    } catch (Throwable error) {
                        stop();
                        stage.close();
                        result.completeExceptionally(error);
                    }
                }
            }.start();
            return null;
        });
        assertTrue(result.get(15, TimeUnit.SECONDS).size() > 1,
                "The stationary enemy must display more than its GIF's initial frame during battle");
    }

    private static <T> T onFxThread(java.util.concurrent.Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(5, TimeUnit.SECONDS);
    }
}
