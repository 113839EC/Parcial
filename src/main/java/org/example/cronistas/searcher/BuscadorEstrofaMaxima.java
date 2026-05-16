package org.example.cronistas.searcher;

import org.example.cronistas.calculator.CalculadorBonusEstrofa;
import org.example.cronistas.model.Diario;
import org.example.cronistas.model.Estrofa;
import org.example.cronistas.model.Viento;
import org.example.cronistas.validator.ValidadorEstrofa;

import java.util.List;
import java.util.Optional;

public class BuscadorEstrofaMaxima {

    private final ValidadorEstrofa validador = new ValidadorEstrofa();
    private final CalculadorBonusEstrofa calculador = new CalculadorBonusEstrofa();

    public Optional<Estrofa> buscar(Diario diario, int iMax) {
        List<Viento> vientos = diario.getVientos();
        int n = vientos.size();
        Estrofa mejor = null;
        int maxPuntaje = -1;

        for (int i = 0; i < n; i++) {
            for (int len = 3; len <= 5 && i + len <= n; len++) {
                List<Viento> candidata = vientos.subList(i, i + len);
                if (validador.esValida(candidata, iMax)) {
                    Estrofa e = new Estrofa(candidata);
                    e.setPuntajeBase(calculador.calcularBase(candidata));
                    e.setBonusCaligrafia(calculador.calcularBonusCaligrafia(candidata));
                    e.setBonusTormenta(calculador.calcularBonusTormenta(candidata));
                    if (e.getPuntajeTotal() > maxPuntaje) {
                        maxPuntaje = e.getPuntajeTotal();
                        mejor = e;
                    }
                }
            }
        }

        return Optional.ofNullable(mejor);
    }
}
