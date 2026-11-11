package finalproject.model;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Reads classpath wave files and reports invalid data with source line numbers. */
public final class LevelLoader {
    private LevelLoader() { }

    public static List<Level> load(String resourcePath) {
        InputStream input = LevelLoader.class.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalArgumentException("Cannot find " + resourcePath
                    + ". Is it inside src/main/resources?");
        }
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            if (scanner.ioException() != null) {
                throw new IllegalArgumentException("Cannot read " + resourcePath, scanner.ioException());
            }
        }
        try {
            return parse(lines);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(resourcePath + ": " + exception.getMessage(), exception);
        }
    }

    public static List<Level> parse(List<String> lines) {
        List<Level> levels = new ArrayList<>();
        List<Level.Enemy> enemies = new ArrayList<>();
        String name = null;
        int waveLine = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String keyword = parts[0].toUpperCase(Locale.ROOT);
            try {
                if (keyword.equals("WAVE")) {
                    if (name != null) {
                        addLevel(levels, name, enemies, waveLine);
                    }
                    name = line.substring(parts[0].length()).trim();
                    if (name.isEmpty()) {
                        throw new IllegalArgumentException("expected WAVE name");
                    }
                    enemies.clear();
                    waveLine = i + 1;
                    continue;
                }
                Level.EnemyType type;
                try {
                    type = Level.EnemyType.valueOf(keyword);
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException("unknown keyword \"" + parts[0] + "\"");
                }
                if (name == null) {
                    throw new IllegalArgumentException(keyword + " appears before any WAVE line");
                }
                boolean spreadshot = type == Level.EnemyType.SPREADSHOT;
                if (parts.length != (spreadshot ? 7 : 6)) {
                    throw new IllegalArgumentException(spreadshot
                            ? "expected SPREADSHOT x y targetX targetY health speed"
                            : "expected SKILIFT x y targetX health speed");
                }
                Level.Coordinate x = coordinate(parts[1]);
                Level.Coordinate y = coordinate(parts[2]);
                Level.Coordinate targetX = coordinate(parts[3]);
                Level.Coordinate targetY = spreadshot ? coordinate(parts[4]) : y;
                int health = Integer.parseInt(parts[spreadshot ? 5 : 4]);
                double speed = Double.parseDouble(parts[spreadshot ? 6 : 5]);
                if (health <= 0 || !Double.isFinite(speed) || speed < 0) {
                    throw new IllegalArgumentException("health must be positive and speed finite and nonnegative");
                }
                enemies.add(new Level.Enemy(type, x, y, targetX, targetY, health, speed));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Line " + (i + 1) + ": not a number in \"" + line + "\"", exception);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Line " + (i + 1) + ": " + exception.getMessage(), exception);
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No waves in level file");
        }
        addLevel(levels, name, enemies, waveLine);
        return List.copyOf(levels);
    }

    private static Level.Coordinate coordinate(String text) {
        String[] bounds = text.split("\\.\\.", -1);
        if (bounds.length > 2) {
            throw new IllegalArgumentException("expected a coordinate or minimum..maximum range");
        }
        double minimum = fraction(bounds[0]);
        double maximum = bounds.length == 1 ? minimum : fraction(bounds[1]);
        if (minimum > maximum) {
            throw new IllegalArgumentException("coordinate range minimum must not exceed maximum");
        }
        return new Level.Coordinate(minimum, maximum);
    }

    private static double fraction(String text) {
        double value = Double.parseDouble(text);
        if (!Double.isFinite(value) || value < 0 || value > 1) {
            throw new IllegalArgumentException("coordinates must be finite fractions from 0 to 1");
        }
        return value;
    }

    private static void addLevel(List<Level> levels, String name, List<Level.Enemy> enemies, int line) {
        if (enemies.isEmpty()) {
            throw new IllegalArgumentException("Wave \"" + name + "\" (Line " + line + ") has no enemies");
        }
        levels.add(new Level(name, enemies));
    }
}
