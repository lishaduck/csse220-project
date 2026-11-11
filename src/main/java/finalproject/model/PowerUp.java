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
}
