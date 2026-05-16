package org.example.cronistas.validator;

import org.example.cronistas.model.Viento;

import java.util.List;

public class ValidadorEstrofa {

    public boolean validarLongitud(List<Viento> vientos) {
        int n = vientos.size();
        return n >= 3 && n <= 5;
    }

    public boolean validarMarcas(List<Viento> vientos) {
        for (int i = 1; i < vientos.size(); i++) {
            if (vientos.get(i).getMarca() <= vientos.get(i - 1).getMarca()) {
                return false;
            }
        }
        return true;
    }

    public boolean validarRotacion(List<Viento> vientos) {
        for (int i = 1; i < vientos.size(); i++) {
            if (vientos.get(i).getCorriente() != vientos.get(i - 1).getCorriente().siguiente()) {
                return false;
            }
        }
        return true;
    }

    public boolean validarIntensidad(List<Viento> vientos, int iMax) {
        int suma = vientos.stream().mapToInt(Viento::getIntensidad).sum();
        return suma <= iMax;
    }

    public boolean esValida(List<Viento> vientos, int iMax) {
        return validarLongitud(vientos)
                && validarMarcas(vientos)
                && validarRotacion(vientos)
                && validarIntensidad(vientos, iMax);
    }
}
