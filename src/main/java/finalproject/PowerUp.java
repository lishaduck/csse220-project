package finalproject;


import javafx.geometry.Point2D;

public class PowerUp {
    Point2D position;
    double velocityY;
    double velocityX;
    int type;


    PowerUp() {
        position = new Point2D(50, 50);
        velocityY = 10;
        velocityX = 0;
    }

    PowerUp(Point2D p) {
        position = p;
        velocityY = 10;
        velocityX = 0;
    }

    PowerUp(Point2D p, double x, double y) {
        position = p;
        velocityY = y;
        velocityX = x;
    }

    PowerUp(Point2D p, double x, double y, int s) {
        position = p;
        velocityY = y;
        velocityX = x;
        type = s;
    }

    void UpdatePosition() {
        position = new Point2D(position.getX() + velocityX, position.getY() + velocityY);
    }
}
