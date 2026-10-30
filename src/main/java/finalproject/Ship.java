package finalproject;

public class Ship {
    double x;
    double y;
    int health;

    public void translateX(int distance) {
        x += distance;
    }

    public void translateY(int distance) {
        y += distance;
    }

    public void spawnProjectile(int type) {
    }

    public void collideWithProjectile(Projectile projectile) {
        health -= projectile.damage;
    }
}

