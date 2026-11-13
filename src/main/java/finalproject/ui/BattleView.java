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
    private final Canvas canvas = new Canvas();
    private final PlayerView playerView = new PlayerView();
    private final LaserView laserView = new LaserView();
    private final Map<EnemyShip, EnemyView> enemyViews = new IdentityHashMap<>();

    BattleView(Dimension2D playfield) {
        setPrefSize(playfield.getWidth(), playfield.getHeight());
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }

    void render(GameModel model) {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#dce8ef"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#152536"));
        gc.setFont(Font.font(16));
        gc.fillText("Hold A / D to move", 24, 32);
        gc.fillText("Score: " + model.getScore(), 24, 100);

        for (Projectile projectile : model.getProjectiles()) {
            laserView.render(gc, projectile);
        }
        enemyViews.keySet().retainAll(model.getEnemies());
        for (EnemyShip enemy : model.getEnemies()) {
            enemyViews.computeIfAbsent(enemy, EnemyView::new).render(gc);
        }
        playerView.render(gc, model.getPlayer());
    }
}
