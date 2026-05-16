package org.example.cronistas;

import org.example.cronistas.model.Corriente;
import org.example.cronistas.model.Diario;
import org.example.cronistas.model.Estrofa;
import org.example.cronistas.model.Viento;
import org.example.cronistas.searcher.BuscadorEstrofaMaxima;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        Diario diario = new Diario();
        diario.agregar(new Viento(1, 'A', Corriente.NORTE, 5));
        diario.agregar(new Viento(2, 'B', Corriente.ESTE,  3));
        diario.agregar(new Viento(3, 'C', Corriente.SUR,   7));
        diario.agregar(new Viento(4, 'D', Corriente.ESTE,  2));
        diario.agregar(new Viento(5, 'E', Corriente.SUR,   4));
        diario.agregar(new Viento(6, 'F', Corriente.OESTE, 6));

        int iMax = 20;

        BuscadorEstrofaMaxima buscador = new BuscadorEstrofaMaxima();
        Optional<Estrofa> resultado = buscador.buscar(diario, iMax);

        if (resultado.isEmpty()) {
            System.out.println("Sin estrofa válida. Puntaje: 0");
            return;
        }

        Estrofa estrofa = resultado.get();
        System.out.println("Estrofa seleccionada:");
        estrofa.getVientos().forEach(System.out::println);
        System.out.println("Puntaje: " + estrofa.getPuntajeTotal());

        StringBuilder bonuses = new StringBuilder("Bonus aplicados:");
        boolean alguno = false;
        if (estrofa.getBonusCaligrafia() > 0) {
            bonuses.append(" Caligrafía continua (+5)");
            alguno = true;
        }
        if (estrofa.getBonusTormenta() > 0) {
            bonuses.append(" Tormenta final (+3)");
            alguno = true;
        }
        if (!alguno) bonuses.append(" Ninguno");
        System.out.println(bonuses);
    }
}
