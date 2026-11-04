package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public class EnemyShip extends Ship {
    int type;
    Point2D target;
    int speed;
    int cycle;

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
        this.cycle=0;
    }
    public void goTowardsTarget(){
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
    public void enemyAI(){
        cycle++;
        if(type==0){
            goTowardsTarget();
            if(cycle%100==0){
                new Projectile(getPosition(), new Point2D(0, 600));
            }
            if(getPosition().getX()==getPlayfield().getWidth()){
                target.add(-getPlayfield().getWidth(), 0);
            }
            if(getPosition().getX()==0){
                target.add(getPlayfield().getWidth(), 0);
            }
        }
    }
}
