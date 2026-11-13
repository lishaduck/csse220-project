package finalproject.ui;

import finalproject.model.EnemyShip;
import finalproject.model.GameModel;
import finalproject.model.Projectile;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Owns the playfield's canvas and layout, and composes the game scene.
 */
class BattleView extends Pane {
    private final javafx.scene.image.Image background = GameBackground.IMAGE;
    private final Canvas canvas = new Canvas();
    private final PlayerView playerView;
    private final LaserView laserView = new LaserView();
    private final Map<EnemyShip, EnemyView> enemyViews = new IdentityHashMap<>();

    BattleView(GameModel model) {
        Dimension2D playfield = model.getPlayfield();
        playerView = new PlayerView(model.getPlayer());
        setPrefSize(playfield.getWidth(), playfield.getHeight());
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }

    void render(GameModel model) {
        var gc = canvas.getGraphicsContext2D();
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(16));
        gc.fillText("Hold A / D to move", 24, 32);
        gc.fillText("Score: " + model.getScore(), 24, 100);

        for (Projectile projectile : model.getProjectiles()) {
            laserView.render(gc, projectile);
        }
        enemyViews.entrySet().removeIf(entry -> {
            if (model.getEnemies().contains(entry.getKey())) return false;
            entry.getValue().stopAnimations();
            return true;
        });
        for (EnemyShip enemy : model.getEnemies()) {
            enemyViews.computeIfAbsent(enemy, EnemyView::new).render(gc);
        }
        playerView.render(gc, model.getPlayer());
    }

    void stopAnimations() {
        playerView.stopAnimations();
        enemyViews.values().forEach(EnemyView::stopAnimations);
    }
}
