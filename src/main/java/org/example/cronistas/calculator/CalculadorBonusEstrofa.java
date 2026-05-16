package org.example.cronistas.calculator;

import org.example.cronistas.model.Viento;

import java.util.List;

public class CalculadorBonusEstrofa {

    public int calcularBase(List<Viento> vientos) {
        return vientos.stream().mapToInt(Viento::getIntensidad).sum();
    }

    public int calcularBonusCaligrafia(List<Viento> vientos) {
        for (int i = 1; i < vientos.size(); i++) {
            if (vientos.get(i).getMarca() != vientos.get(i - 1).getMarca() + 1) {
                return 0;
            }
        }
        return 5;
    }

    public int calcularBonusTormenta(List<Viento> vientos) {
        int ultimo = vientos.get(vientos.size() - 1).getIntensidad();
        for (int i = 0; i < vientos.size() - 1; i++) {
            if (ultimo <= vientos.get(i).getIntensidad()) {
                return 0;
            }
        }
        return 3;
    }
}
