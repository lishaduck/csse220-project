package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipTest {
    @ParameterizedTest
    @CsvSource({"83, 27", "19, 91"})
    void boundsTranslationByTheShipsOwnSizeAndCanReverseAtEveryEdge(double width, double height) {
        Dimension2D playfield = new Dimension2D(301, 197);
        Ship ship = new Ship(playfield, new Dimension2D(width, height), Point2D.ZERO) {};

        ship.translateX(17.5);
        ship.translateY(11.25);
        assertEquals(new Point2D(17.5, 11.25), ship.getPosition());

        for (int i = 0; i < 1000; i++) {
            ship.translateX(25.5);
            ship.translateY(7.75);
        }
        assertEquals(new Point2D(301 - width, 197 - height), ship.getPosition());
        ship.translateX(-13.25);
        ship.translateY(-9.5);
        assertEquals(new Point2D(301 - width - 13.25, 197 - height - 9.5), ship.getPosition());

        for (int i = 0; i < 1000; i++) {
            ship.translateX(-25.5);
            ship.translateY(-7.75);
        }
        assertEquals(Point2D.ZERO, ship.getPosition());
        ship.translateX(5.25);
        ship.translateY(6.75);
        assertEquals(new Point2D(5.25, 6.75), ship.getPosition());

        ship.translateX(1000);
        ship.translateY(1000);
        ship.resizePlayfield(new Dimension2D(101, 97));
        assertEquals(new Point2D(101 - width, 97 - height), ship.getPosition());
    }
}
