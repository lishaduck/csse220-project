package finalproject.model;


import javafx.geometry.Point2D;

public class Projectile {
    Point2D position;
    Point2D velocity;
    int[] special;
    int damage;

    Projectile() {
        position = new Point2D(50, 50);
        velocity = new Point2D(0, -600);
        damage = 2;
    }

    Projectile(Point2D p) {
        position = p;
        velocity = new Point2D(0, -600);
        damage = 2;
    }

    Projectile(Point2D p, Point2D velocity) {
        position = p;
        this.velocity = velocity;
        damage = 2;
    }

    Projectile(Point2D p, Point2D velocity, int[] s) {
        position = p;
        this.velocity = velocity;
        special = s;
        damage = 2;
    }

    Projectile(Point2D p, Point2D velocity, int[] s, int d) {
        position = p;
        this.velocity = velocity;
        special = s;
        damage = d;
    }

    public Point2D getPosition() {
        return position;
    }

    public boolean updatePosition(double elapsed) {
        position = position.add(velocity.multiply(elapsed));

        // The below print statement is to test if the projectiles are removed after reaching the top of the screen
        //System.out.println(position + " <- I am here");
        return position.getY() < 0;
    }
}
