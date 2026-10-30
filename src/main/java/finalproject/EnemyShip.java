package finalproject;

public class EnemyShip extends Ship {
    int type;

    public EnemyShip(int x, int y, int type, int health) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.health = health;
    }
}

