package finalproject.model;

import java.util.ArrayList;

/**
 * Owns the ships, power-ups, and projectiles in the game.
 */
public class GameModel {
    private final ArrayList<Ship> ships = new ArrayList<>();
    private final ArrayList<PowerUp> powerUps = new ArrayList<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();
}
