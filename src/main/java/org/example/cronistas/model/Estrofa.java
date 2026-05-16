package org.example.cronistas.model;

import java.util.List;

public class Estrofa {
    private final List<Viento> vientos;
    private int puntajeBase;
    private int bonusCaligrafia;
    private int bonusTormenta;

    public Estrofa(List<Viento> vientos) {
        this.vientos = List.copyOf(vientos);
    }

    public List<Viento> getVientos() { return vientos; }

    public void setPuntajeBase(int base) { this.puntajeBase = base; }
    public void setBonusCaligrafia(int bonus) { this.bonusCaligrafia = bonus; }
    public void setBonusTormenta(int bonus) { this.bonusTormenta = bonus; }

    public int getPuntajeBase() { return puntajeBase; }
    public int getBonusCaligrafia() { return bonusCaligrafia; }
    public int getBonusTormenta() { return bonusTormenta; }
    public int getPuntajeTotal() { return puntajeBase + bonusCaligrafia + bonusTormenta; }
}
