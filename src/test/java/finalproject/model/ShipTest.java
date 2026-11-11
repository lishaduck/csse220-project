package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ShipTest {
    @Test
    void projectilesSnapshotTheirShipsPositionAndUseTheirFiringVelocity() {
        Dimension2D playfield = new Dimension2D(640, 480);
        PlayerShip player = new PlayerShip(playfield);
        Point2D origin = player.getPosition();
        EnemyShip skiLift = new SkiLiftShip(playfield, origin, origin.getX(), 2, 0);
        EnemyShip spreadshot = new SpreadshotShip(playfield, origin, origin, 2, 0);

        for (EnemyShip enemy : List.of(skiLift, spreadshot)) {
            Projectile enemyShot = new Projectile(enemy, new Point2D(17, 43), 2);
            assertSame(enemy, enemyShot.getSource());
            assertEquals(origin, enemyShot.getPosition());
            enemy.translateX(100, playfield);
            assertEquals(origin, enemyShot.getPosition());
            enemyShot.updatePosition(0.5);
            assertEquals(origin.add(8.5, 21.5), enemyShot.getPosition());
        }

        Projectile playerShot = new Projectile(player, PlayerShip.PROJECTILE_VELOCITY, 2);
        assertSame(player, playerShot.getSource());
        assertEquals(origin, playerShot.getPosition());
        player.translateX(30, playfield);
        assertEquals(origin, playerShot.getPosition());
        playerShot.updatePosition(0.25);
        assertEquals(origin.add(0, -150), playerShot.getPosition());
    }

    @ParameterizedTest
    @CsvSource({
            "48, 36, 30, 0, false",
            "48, 36, -40, 0, true",
            "48, 36, 3.9, 0, true",
            "48, 36, 4, 0, false",
            "48, 36, -47.9, 0, true",
            "48, 36, -48, 0, false",
            "32, 16, 0, 19.9, true",
            "32, 16, 0, 20, false",
            "32, 16, 0, -15.9, true",
            "32, 16, 0, -16, false",
            "48, 36, -40, 20, false",
            "48, 36, 4, -30, false"
    })
    void projectilesDamageShipsOnlyWhenTheirTopLeftBoundsOverlap(
            double width, double height, double dx, double dy, boolean hit
    ) {
        Dimension2D playfield = new Dimension2D(640, 480);
        PlayerShip player = new PlayerShip(playfield);
        player.setPosition(new Point2D(100, 100), playfield);
        Ship target = new Ship(playfield, new Dimension2D(width, height), new Point2D(100 + dx, 100 + dy)) {};
        target.health = 2;

        assertEquals(hit, new Projectile(player, PlayerShip.PROJECTILE_VELOCITY, 2).collide(target));
        assertEquals(!hit, target.isAlive());
    }

    @ParameterizedTest
    @CsvSource({"83, 27", "19, 91"})
    void boundsTranslationByTheShipsOwnSizeAndCanReverseAtEveryEdge(double width, double height) {
        Dimension2D playfield = new Dimension2D(301, 197);
        Ship ship = new Ship(playfield, new Dimension2D(width, height), Point2D.ZERO) {};

        ship.translateX(17.5, playfield);
        ship.translateY(11.25, playfield);
        assertEquals(new Point2D(17.5, 11.25), ship.getPosition());

        for (int i = 0; i < 1000; i++) {
            ship.translateX(25.5, playfield);
            ship.translateY(7.75, playfield);
        }
        assertEquals(new Point2D(301 - width, 197 - height), ship.getPosition());
        ship.translateX(-13.25, playfield);
        ship.translateY(-9.5, playfield);
        assertEquals(new Point2D(301 - width - 13.25, 197 - height - 9.5), ship.getPosition());

        for (int i = 0; i < 1000; i++) {
            ship.translateX(-25.5, playfield);
            ship.translateY(-7.75, playfield);
        }
        assertEquals(Point2D.ZERO, ship.getPosition());
        ship.translateX(5.25, playfield);
        ship.translateY(6.75, playfield);
        assertEquals(new Point2D(5.25, 6.75), ship.getPosition());

        ship.translateX(1000, playfield);
        ship.translateY(1000, playfield);
        ship.translateX(0, new Dimension2D(101, 97));
        assertEquals(new Point2D(101 - width, 97 - height), ship.getPosition());

        ship.translateX(1000, playfield);
        ship.translateY(1000, playfield);
        assertEquals(new Point2D(301 - width, 197 - height), ship.getPosition());
        ship.translateY(0, new Dimension2D(13, 17));
        assertEquals(Point2D.ZERO, ship.getPosition());
        ship.translateX(5.25, playfield);
        ship.translateY(6.75, playfield);
        assertEquals(new Point2D(5.25, 6.75), ship.getPosition());
    }
}
