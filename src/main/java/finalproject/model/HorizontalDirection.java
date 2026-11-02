package finalproject.model;

/** Horizontal movement intent, independent of an input device. */
public enum HorizontalDirection {
    LEFT(-1),
    NONE(0),
    RIGHT(1);

    private final int sign;

    HorizontalDirection(int sign) {
        this.sign = sign;
    }

    int getSign() {
        return sign;
    }
}
