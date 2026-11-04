package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class EnemyShip extends Ship {
    int type;
    Point2D target;
    int speed;

    public EnemyShip(
            Dimension2D playfield,
            Dimension2D size,
            Point2D position,
            int type,
            int health,
            int speed
    ) {
        super(playfield, size);
        setPosition(position);
        this.type = type;
        this.health = health;
        this.speed = speed;
    }
    public void goToPoint(){
        double distance=Math.sqrt(Math.pow(getPosition().getX()-target.getX(), 2)+Math.pow(getPosition().getY()-target.getY(), 2));
        double angle=Math.atan((target.getY()-getPosition().getY())/(target.getX()-getPosition().getX()));
        if(distance<speed){
            setPosition(target);
        }
        else{
            translateX(Math.cos(angle)*speed);
            translateY(Math.sin(angle)*speed);
        }
    }
}
