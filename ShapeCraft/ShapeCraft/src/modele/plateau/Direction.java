package modele.plateau;

import java.io.Serializable;

public enum Direction implements Serializable {
    North(0, -1), South(0, 1), East(1, 0), West(-1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction getOpposite() {
        switch (this) {
            case North:
                return South;
            case South:
                return North;
            case East:
                return West;
            case West:
                return East;
            default:
                return North;
        }
    }

    public Direction getClockwise() {
        switch (this) {
            case North:
                return East;
            case East:
                return South;
            case South:
                return West;
            case West:
                return North;
            default:
                return North;
        }
    }

    public Direction getCounterClockwise() {
        switch (this) {
            case North:
                return West;
            case West:
                return South;
            case South:
                return East;
            case East:
                return North;
            default:
                return North;
        }
    }
}