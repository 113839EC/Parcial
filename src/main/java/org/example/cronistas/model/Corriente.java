package org.example.cronistas.model;

public enum Corriente {
    NORTE, ESTE, SUR, OESTE;

    public Corriente siguiente() {
        return values()[(ordinal() + 1) % 4];
    }
}
