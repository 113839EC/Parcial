package org.example.model.enums;

public enum CellType {
    EMPTY(0),
    WALL(1),
    PLAYER(2),
    ITEM(3),
    EXIT(4);

    public final int value;

    CellType(int value) { this.value = value; }

    public static CellType fromValue(int v) {
        for (CellType c : values()) if (c.value == v) return c;
        return EMPTY;
    }
}
