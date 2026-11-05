package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class EnemyShip extends Ship {
    int type;

    public EnemyShip(
            Dimension2D playfield,
            Dimension2D size,
            Point2D position,
            int type,
            int health
    ) {
        super(playfield, size, position);
        this.type = type;
        this.health = health;
    }
}
