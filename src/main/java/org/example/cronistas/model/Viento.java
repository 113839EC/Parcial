package org.example.cronistas.model;

public class Viento {
    private final int id;
    private final char marca;
    private final Corriente corriente;
    private final int intensidad;

    public Viento(int id, char marca, Corriente corriente, int intensidad) {
        this.id = id;
        this.marca = marca;
        this.corriente = corriente;
        this.intensidad = intensidad;
    }

    public int getId() { return id; }
    public char getMarca() { return marca; }
    public Corriente getCorriente() { return corriente; }
    public int getIntensidad() { return intensidad; }

    @Override
    public String toString() {
        return "(ID:" + id + ", Marca:" + marca + ", Corriente:" + corriente + ", Intensidad:" + intensidad + ")";
    }
}
