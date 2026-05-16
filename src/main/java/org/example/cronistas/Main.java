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
            System.out.println("No valid strophe found. Score: 0");
            return;
        }

        Estrofa estrofa = resultado.get();
        System.out.println("Selected strophe:");
        estrofa.getVientos().forEach(System.out::println);
        System.out.println("Score: " + estrofa.getPuntajeTotal());

        StringBuilder bonuses = new StringBuilder("Bonuses applied:");
        boolean anyBonus = false;
        if (estrofa.getBonusCaligrafia() > 0) {
            bonuses.append(" Continuous calligraphy (+5)");
            anyBonus = true;
        }
        if (estrofa.getBonusTormenta() > 0) {
            bonuses.append(" Final storm (+3)");
            anyBonus = true;
        }
        if (!anyBonus) {
            bonuses.append(" None");
        }
        System.out.println(bonuses);
    }
}
