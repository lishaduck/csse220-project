package finalproject.ui;

import finalproject.app.StageManager;
import finalproject.model.GameModel;
import finalproject.model.PowerUp;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Offers three distinct powerups between waves. */
public final class PowerupScreen extends MenuScreen {
    public PowerupScreen(GameModel model, Battle battle) {
        super("Wave cleared", "Choose one powerup to continue");
        List<PowerUp> choices = new ArrayList<>(List.of(PowerUp.values()));
        Collections.shuffle(choices);
        HBox buttons = new HBox(16);
        buttons.setAlignment(Pos.CENTER);
        for (PowerUp powerup : choices.subList(0, 3)) {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(
                    PowerupScreen.class.getResource(iconPath(powerup)), "Missing powerup resource").toExternalForm()));
            icon.setFitWidth(48);
            icon.setFitHeight(48);
            icon.setPreserveRatio(true);
            Button choice = createButton(title(powerup));
            choice.setGraphic(icon);
            choice.setPrefSize(176, 144);
            choice.setPadding(new Insets(16, 8, 16, 8));
            choice.setWrapText(true);
            choice.setAlignment(Pos.CENTER);
            choice.setUserData(powerup);
            choice.setContentDisplay(ContentDisplay.TOP);
            choice.setGraphicTextGap(16);
            choice.getStyleClass().add("powerup");
            choice.setOnAction(event -> {
                if (choice.isDisabled()) return;
                buttons.getChildren().forEach(button -> button.setDisable(true));
                model.addPowerup(powerup);
                if (model.getPlayer().isAlive()) {
                    model.spawnWave();
                    StageManager.from(choice).show(battle);
                } else {
                    StageManager.from(choice).show(new GameOverScreen(model.getScore()));
                }
            });
            buttons.getChildren().add(choice);
        }
        content.getChildren().add(buttons);
        buttons.setDisable(true);
        PauseTransition inputDelay = new PauseTransition(Duration.millis(500));
        inputDelay.setOnFinished(event -> buttons.setDisable(false));
        inputDelay.play();
    }

    private static String title(PowerUp powerup) {
        return switch (powerup) {
            case KILL_SCORE -> "Bounty boost";
            case HEALTH -> "Repair";
            case DAMAGE_AND_SCORE -> "Damage & score";
            case FAST_PROJECTILES -> "Fast projectiles";
            case GLASS_CANNON -> "Glass cannon";
            case RAPID_FIRE -> "Rapid fire";
            case BONUS_POINTS -> "Bonus points";
            case SLOW_ENEMIES -> "Slow enemies";
        };
    }

    private static String iconPath(PowerUp powerup) {
        return switch (powerup) {
            case KILL_SCORE -> "/Powerup0.png";
            case HEALTH -> "/Powerup1.png";
            case DAMAGE_AND_SCORE -> "/Powerup3.png";
            case FAST_PROJECTILES -> "/Powerup4.png";
            case GLASS_CANNON -> "/Powerup5.png";
            case RAPID_FIRE -> "/Powerup6.png";
            case BONUS_POINTS -> "/Powerup7.png";
            case SLOW_ENEMIES -> "/Powerup8.png";
        };
    }
}
