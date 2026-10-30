package finalproject;

import javafx.geometry.Point2D;

public class EnemyShip extends Ship {
    int type;

    public EnemyShip(Point2D position, int type, int health) {
        this.position = position;
        this.type = type;
        this.health = health;
    }
}
