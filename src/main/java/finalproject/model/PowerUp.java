package finalproject.model;


import javafx.geometry.Point2D;

public class PowerUp {
    Point2D position;
    Point2D velocity;
    int type;


    PowerUp() {
        position = new Point2D(50, 50);
        velocity = new Point2D(0, 10);
    }

    PowerUp(Point2D p) {
        position = p;
        velocity = new Point2D(0, 10);
    }

    PowerUp(Point2D p, Point2D velocity) {
        position = p;
        this.velocity = velocity;
    }

    PowerUp(Point2D p, Point2D velocity, int s) {
        position = p;
        this.velocity = velocity;
        type = s;
    }

    void UpdatePosition() {
        position = position.add(velocity);
    }

    public void triggerPowerUp(PlayerShip player) { // call this when a powerup is gotten to trigger any one time effects
        if (type == 1) {
            player.heal(2);
        }
        if (type == 5) {
            player.heal(-1);
        }
        if (type == 7) {
            GameModel.addScore(100);
        }
    }
}
