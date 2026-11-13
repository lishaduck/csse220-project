package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * A named wave that creates fresh enemies from immutable starting data.
 */
public record Level(String name, List<Enemy> enemies) {
    public Level {
        enemies = List.copyOf(enemies);
    }

    public List<EnemyShip> createEnemies(Dimension2D playfield, RandomGenerator random) {
        List<EnemyShip> ships = new ArrayList<>();
        for (Enemy enemy : enemies) {
            Point2D position = new Point2D(enemy.x().sample(random) * playfield.getWidth(),
                    enemy.y().sample(random) * playfield.getHeight());
            double targetX = enemy.targetX().sample(random) * playfield.getWidth();
            ships.add(switch (enemy.type()) {
                case SKILIFT -> new SkiLiftShip(playfield, position, targetX, enemy.health(), enemy.speed());
                case SPREADSHOT -> new SpreadshotShip(playfield, position,
                        new Point2D(targetX, enemy.targetY().sample(random) * playfield.getHeight()),
                        enemy.health(), enemy.speed());
                case MINE -> new MineShip(playfield, position, enemy.health(), enemy.speed());
                case BONUS -> new BonusShip(playfield, position, targetX, enemy.health(), enemy.speed());
                case BOSSEDGE -> new BossEdge(playfield, position, enemy.health());
                case BOSSMIDDLE -> new BossMiddle(playfield, position, enemy.health());
                case BOSSLASER -> new BossLaser(playfield, position, enemy.health());
            });
        }
        return ships;
    }

    public enum EnemyType {
        SKILIFT, SPREADSHOT, MINE, BONUS, BOSSEDGE, BOSSMIDDLE, BOSSLASER
    }

    public record Enemy(EnemyType type, Coordinate x, Coordinate y, Coordinate targetX,
                        Coordinate targetY, int health, double speed) {
    }

    public record Coordinate(double minimum, double maximum) {
        public double sample(RandomGenerator random) {
            return minimum == maximum ? minimum : random.nextDouble(minimum, maximum);
        }
    }
}
