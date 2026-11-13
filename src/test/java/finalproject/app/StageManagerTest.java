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
            assertShipsFlashOnHits();
            assertBossBackground();
            assertGameScreens();
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

    private static void assertShipsFlashOnHits() throws Exception {
        GameModel model = new GameModel(new Dimension2D(640, 480), LevelLoader.parse(List.of(
                "WAVE Flash", "SKILIFT 0.25 0.25 0.25 7 0")));
        model.update(new finalproject.model.PlayerInput(finalproject.model.HorizontalDirection.NONE, true),
                0, model.getPlayfield());
        var enemy = model.getEnemies().getFirst();
        var player = model.getPlayer();
        var shot = model.getProjectiles().getFirst();
        var canvas = onFxThread(() -> new javafx.scene.canvas.Canvas(640, 480));
        var playerView = onFxThread(() -> new finalproject.ui.PlayerView(player));
        var enemyView = onFxThread(() -> new finalproject.ui.EnemyView(enemy));
        java.util.concurrent.Callable<double[]> render = () -> {
            var gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, 640, 480);
            enemyView.render(gc);
            playerView.render(gc, player);
            var params = new javafx.scene.SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.TRANSPARENT);
            var pixels = canvas.snapshot(params, null).getPixelReader();
            return new double[] {spriteAlpha(pixels, player), spriteAlpha(pixels, enemy)};
        };
        double[] opaque = onFxThread(render);
        assertTrue(opaque[0] > 0 && opaque[1] > 0, "Both sprites must render");
        onFxThread(() -> {
            player.heal(-1);
            return null;
        });
        double[] healthCost = onFxThread(render);
        assertEquals(opaque[0], healthCost[0], "Health costs are not projectile hits");
        double[] enemyHit = onFxThread(() -> {
            enemy.collideWithProjectile(shot);
            return render.call();
        });
        assertTrue(enemyHit[1] < opaque[1] * 0.4, "An enemy hit must reduce its rendered opacity");
        assertEquals(opaque[0], enemyHit[0], "An enemy flash must not fade the player");
        double[] playerHit = onFxThread(() -> {
            player.collideWithProjectile(shot);
            return render.call();
        });
        assertTrue(playerHit[0] < opaque[0] * 0.4, "A player hit must reduce its rendered opacity");
        Thread.sleep(350);
        double[] restored = onFxThread(render);
        assertTrue(restored[0] > opaque[0] * 0.8, "The player must return to full opacity");
        assertTrue(restored[1] > opaque[1] * 0.8, "The enemy must return to full opacity");
    }

    private static double spriteAlpha(javafx.scene.image.PixelReader pixels, finalproject.model.Ship ship) {
        double sum = 0;
        for (int y = 0; y < ship.getHeight(); y++) {
            for (int x = 0; x < ship.getWidth(); x++) {
                sum += pixels.getArgb((int) ship.getPosition().getX() + x,
                        (int) ship.getPosition().getY() + y) >>> 24;
            }
        }
        return sum;
    }

    private static void assertBossBackground() throws Exception {
        GameModel model = new GameModel(new Dimension2D(640, 480));
        for (int wave = 1; wave <= 19; wave++) model.spawnWave();
        Stage stage = onFxThread(Stage::new);
        try {
            onFxThread(() -> {
                new StageManager(stage).show(new Battle(model));
                return null;
            });
            Thread.sleep(100);
            int bodyPixel = 0xff715f5f;
            assertEquals(19, model.getWaveNumber());
            assertNotEquals(bodyPixel, onFxThread(() -> stage.getScene().getRoot()
                    .snapshot(null, null).getPixelReader().getArgb(8, 8)));
            onFxThread(() -> { model.spawnWave(); return null; });
            assertEquals(20, model.getWaveNumber());
            await(() -> stage.getScene().getRoot().snapshot(null, null)
                    .getPixelReader().getArgb(8, 8) == bodyPixel);
            onFxThread(() -> { model.spawnWave(); return null; });
            assertEquals(20, model.getWaveNumber(), "The final boss wave repeats without advancing its number");
        } finally {
            onFxThread(() -> { stage.close(); return null; });
        }
    }

    private static void assertGameScreens() throws Exception {
        Stage stage = onFxThread(Stage::new);
        GameModel model = new GameModel(new Dimension2D(640, 480), LevelLoader.parse(List.of(
                "WAVE First", "SKILIFT 0.4625 0.85 0.4625 2 0",
                "WAVE Second", "SKILIFT 0.75 0.25 0.75 7 0")));
        Battle battle = onFxThread(() -> new Battle(model));
        StageManager screens = onFxThread(() -> new StageManager(stage));
        try {
            onFxThread(() -> {
                new GameApp().start(stage);
                assertEquals("Start game", ((Button) stage.getScene().lookup("#start-game")).getText());
                ((Button) stage.getScene().lookup("#start-game")).fire();
                assertNull(stage.getScene().lookup("#start-game"));
                screens.show(battle);
                return null;
            });
            await(() -> !model.getEnemies().isEmpty());
            onFxThread(() -> {
                battle.getView().getOnKeyPressed().handle(new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", KeyCode.D, false, false, false, false));
                battle.getView().getOnKeyPressed().handle(new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", KeyCode.SPACE, false, false, false, false));
                model.update(new finalproject.model.PlayerInput(
                        finalproject.model.HorizontalDirection.NONE, true), 0, model.getPlayfield());
                assertTrue(model.isWaveCleared(), "Shot must clear fixture wave: player "
                        + model.getPlayer().getPosition() + " enemy " + model.getEnemies().getFirst().getPosition()
                        + " health " + model.getEnemies().getFirst().getHealth());
                return null;
            });
            await(() -> stage.getScene() != battle.getView());
            Scene choices = onFxThread(stage::getScene);
            onFxThread(() -> {
                Button earlyChoice = (Button) choices.lookup(".powerup");
                assertTrue(earlyChoice.isDisabled(), "Choices must ignore input during the opening delay");
                earlyChoice.fire();
                assertSame(choices, stage.getScene());
                return null;
            });
            await(() -> !choices.lookup(".powerup").isDisabled());
            Point2D paused = onFxThread(() -> model.getPlayer().getPosition());
            Thread.sleep(150);
            onFxThread(() -> {
                assertEquals(paused, model.getPlayer().getPosition());
                var buttons = choices.getRoot().lookupAll(".powerup").stream()
                        .map(node -> (Button) node).toList();
                assertEquals(3, buttons.size());
                assertEquals(3, buttons.stream().map(Button::getUserData).distinct().count());
                for (Button button : buttons) {
                    assertFalse(button.getText().isBlank());
                    var image = ((javafx.scene.image.ImageView) button.getGraphic()).getImage();
                    assertFalse(image.isError());
                    assertTrue(image.getWidth() > 0);
                }
                Button chosen = buttons.getFirst();
                chosen.requestFocus();
                for (int repeat = 0; repeat < 3; repeat++) {
                    chosen.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, " ", " ", KeyCode.SPACE,
                            false, false, false, false));
                }
                chosen.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, " ", " ", KeyCode.SPACE,
                        false, false, false, false));
                assertSame(choices, stage.getScene(), "Held fire must not choose a powerup");
                var powerup = (finalproject.model.PowerUp) chosen.getUserData();
                int expectedScore = powerup == finalproject.model.PowerUp.BONUS_POINTS ? 310 : 210;
                int expectedHealth = switch (powerup) {
                    case HEALTH -> 22;
                    case GLASS_CANNON -> 19;
                    default -> 20;
                };
                chosen.fire();
                assertSame(battle.getView(), stage.getScene());
                assertEquals(expectedScore, model.getScore());
                assertEquals(expectedHealth, model.getPlayer().getHealth());
                assertEquals(7, model.getEnemies().getFirst().getHealth());
                assertTrue(model.getProjectiles().isEmpty());
                return null;
            });
            Thread.sleep(150);
            onFxThread(() -> {
                assertEquals(paused, model.getPlayer().getPosition(), "Held movement must clear on resume");
                model.addScore(137);
                model.getPlayer().heal(-model.getPlayer().getHealth());
                return null;
            });
            await(() -> stage.getScene() != battle.getView());
            onFxThread(() -> {
                assertTrue(stage.getScene().getRoot().lookupAll(".label").stream()
                        .map(node -> ((javafx.scene.control.Label) node).getText())
                        .anyMatch(text -> text.equals("Final score: " + model.getScore())));
                ((Button) stage.getScene().lookup("#try-again")).fire();
                assertNotSame(battle.getView(), stage.getScene());
                assertNull(stage.getScene().lookup("#try-again"));
                return null;
            });
        } finally {
            onFxThread(() -> { stage.close(); return null; });
        }
    }

    private static void await(java.util.concurrent.Callable<Boolean> condition) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!onFxThread(condition) && System.nanoTime() < deadline) Thread.sleep(20);
        assertTrue(onFxThread(condition), "The screen transition must complete");
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
