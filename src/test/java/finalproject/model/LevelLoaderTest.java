package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LevelLoaderTest {
    @Test
    void parsesNamedWavesAndBothEnemyTypesInOrder() {
        List<Level> levels = LevelLoader.parse(List.of(
                " # comment", "", " wave First flight ",
                "skilift\t0.2 0.3 0.8 7 12.5",
                "WAVE Boss room", "SPREADSHOT 1 0 0.6 0.25 13 0"));

        assertEquals(List.of("First flight", "Boss room"), levels.stream().map(Level::name).toList());
        assertEquals(List.of(new Level.Enemy(Level.EnemyType.SKILIFT,
                        new Level.Coordinate(0.2, 0.2), new Level.Coordinate(0.3, 0.3),
                        new Level.Coordinate(0.8, 0.8), new Level.Coordinate(0.3, 0.3), 7, 12.5)),
                levels.getFirst().enemies());
        assertEquals(List.of(new Level.Enemy(Level.EnemyType.SPREADSHOT,
                        new Level.Coordinate(1, 1), new Level.Coordinate(0, 0),
                        new Level.Coordinate(0.6, 0.6), new Level.Coordinate(0.25, 0.25), 13, 0)),
                levels.getLast().enemies());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "TREE 1 2", "SKILIFT 0 0", "SPREADSHOT 0 0 0 0 1 2 extra",
            "SKILIFT 0 two 0 1 2", "SKILIFT 0 0 0 1.5 2",
            "SKILIFT -0.1 0 0 1 2", "SKILIFT 0 1.1 0 1 2",
            "SPREADSHOT 0 0 NaN 0 1 2", "SPREADSHOT 0 0 0 Infinity 1 2",
            "SKILIFT 0 0 0 0 2", "SKILIFT 0 0 0 -1 2",
            "SKILIFT 0 0 0 1 -2", "SKILIFT 0 0 0 1 NaN", "SKILIFT 0 0 0 1 Infinity",
            "SKILIFT 0.8..0.2 0 0 1 2", "SKILIFT -0.1..0.2 0 0 1 2",
            "SKILIFT 0..1.1 0 0 1 2", "SKILIFT 0..NaN 0 0 1 2",
            "SKILIFT 0.. 0 0 1 2", "SKILIFT ..1 0 0 1 2", "SKILIFT 0..0.5..1 0 0 1 2"
    })
    void rejectsMalformedEnemyLinesWithTheirSourceLine(String badLine) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> LevelLoader.parse(List.of("# header", "WAVE Test", "", badLine)));
        assertTrue(error.getMessage().startsWith("Line 4:"), error.getMessage());
    }

    @Test
    void rejectsMissingHeadersNamesAndEmptyWaves() {
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> LevelLoader.parse(List.of("SKILIFT 0 0 0 1 2")))
                .getMessage().contains("before any WAVE"));
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> LevelLoader.parse(List.of("WAVE"))).getMessage().contains("Line 1"));
        for (List<String> lines : List.of(List.of("WAVE Empty"),
                List.of("WAVE Empty", "WAVE Next", "SKILIFT 0 0 0 1 2"))) {
            String error = assertThrows(IllegalArgumentException.class, () -> LevelLoader.parse(lines)).getMessage();
            assertTrue(error.contains("Empty") && error.contains("Line 1") && error.contains("no enemies"), error);
        }
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> LevelLoader.parse(List.of("# only comments", ""))).getMessage().contains("No waves"));
    }

    @Test
    void samplesRandomCoordinatesOnEachSpawnAndKeepsFixedCoordinates() {
        Level level = LevelLoader.parse(List.of("WAVE Random patrol",
                "SKILIFT 0.2..0.4 0.1..0.2 0.8 7 12.5",
                "SPREADSHOT 0.6..0.7 0.3..0.4 0.1..0.2 0.5..0.6 13 40",
                "SKILIFT 0.25..0.25 0.15 0 1 0")).getFirst();
        Dimension2D playfield = new Dimension2D(800, 600);
        Random random = new Random(123);
        Random sameSeed = new Random(123);
        Set<Point2D> patrolPositions = new HashSet<>();
        Set<Point2D> bossPositions = new HashSet<>();
        Set<Point2D> bossDestinations = new HashSet<>();
        for (int spawn = 0; spawn < 20; spawn++) {
            List<EnemyShip> ships = level.createEnemies(playfield, random);
            List<EnemyShip> repeatedSeed = level.createEnemies(playfield, sameSeed);
            assertEquals(3, ships.size());
            assertEquals(ships.stream().map(Ship::getPosition).toList(),
                    repeatedSeed.stream().map(Ship::getPosition).toList());
            EnemyShip patrol = ships.getFirst();
            assertTrue(patrol instanceof SkiLiftShip);
            assertEquals(7, patrol.getHealth());
            assertTrue(patrol.getPosition().getX() >= 160 && patrol.getPosition().getX() < 320);
            assertTrue(patrol.getPosition().getY() >= 60 && patrol.getPosition().getY() < 120);
            patrolPositions.add(patrol.getPosition());
            EnemyShip boss = ships.get(1);
            assertTrue(boss instanceof SpreadshotShip);
            assertEquals(13, boss.getHealth());
            assertTrue(boss.getPosition().getX() >= 480 && boss.getPosition().getX() < 560);
            assertTrue(boss.getPosition().getY() >= 180 && boss.getPosition().getY() < 240);
            bossPositions.add(boss.getPosition());
            boss.update(Point2D.ZERO, playfield, 15, new java.util.ArrayList<>(), List.of());
            assertTrue(boss.getPosition().getX() >= 80 && boss.getPosition().getX() < 160);
            assertTrue(boss.getPosition().getY() >= 300 && boss.getPosition().getY() < 360);
            bossDestinations.add(boss.getPosition());
            assertEquals(new Point2D(200, 90), ships.getLast().getPosition());
        }
        assertEquals(20, patrolPositions.size());
        assertEquals(20, bossPositions.size());
        assertEquals(20, bossDestinations.size());
    }

    @Test
    void loadsPackagedResourceAndReportsMissingResource() {
        List<Level> levels = LevelLoader.load("/levels/levels.txt");
        assertEquals(6, levels.size());
        assertEquals(1, levels.get(4).enemies().size());
        Level.Enemy boss = levels.get(4).enemies().getFirst();
        assertEquals(Level.EnemyType.SPREADSHOT, boss.type());
        assertEquals(5, boss.health());
        assertEquals(50, boss.speed());
        assertEquals(new Level.Coordinate(0, 1), boss.x());
        assertEquals(new Level.Coordinate(0, 1.0 / 3), boss.y());
        String error = assertThrows(IllegalArgumentException.class,
                () -> LevelLoader.load("/levels/missing.txt")).getMessage();
        assertTrue(error.contains("/levels/missing.txt") && error.contains("src/main/resources"));
    }
}
