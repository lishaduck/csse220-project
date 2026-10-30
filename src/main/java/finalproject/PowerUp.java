package finalproject;

import java.awt.geom.Point2D;

public class Powerup {
    Point2D position;
    double velocityY;
    double velocityX;
    int type;


    Powerup() {
        position = new Point2D.Double(50, 50);
        velocityY = 10;
        velocityX = 0;
    }

    Powerup(Point2D p) {
        position = p;
        velocityY = 10;
        velocityX = 0;
    }

    Powerup(Point2D p, double x, double y) {
        position = p;
        velocityY = y;
        velocityX = x;
    }

    Powerup(Point2D p, double x, double y, int s) {
        position = p;
        velocityY = y;
        velocityX = x;
        type = s;
    }

    void updatePosition() {
        position.setLocation(new Point2D.Double(position.getX() + velocityX, position.getY() + velocityY));
    }
}
