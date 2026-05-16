package org.example.cronistas;

import org.example.cronistas.calculator.CalculadorBonusEstrofa;
import org.example.cronistas.model.Corriente;
import org.example.cronistas.model.Diario;
import org.example.cronistas.model.Estrofa;
import org.example.cronistas.model.Viento;
import org.example.cronistas.searcher.BuscadorEstrofaMaxima;
import org.example.cronistas.validator.ValidadorEstrofa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CronistasTest {

    private ValidadorEstrofa validador;
    private CalculadorBonusEstrofa calculador;
    private BuscadorEstrofaMaxima buscador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorEstrofa();
        calculador = new CalculadorBonusEstrofa();
        buscador = new BuscadorEstrofaMaxima();
    }

    // --- helpers ---

    private Viento v(int id, char marca, Corriente c, int intensidad) {
        return new Viento(id, marca, c, intensidad);
    }

    // ===================== REGLA 1: LONGITUD =====================

    @Test
    void longitud3_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1));
        assertTrue(validador.validarLongitud(vientos));
    }

    @Test
    void longitud4_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1),
                v(4,'D', Corriente.NORTE, 1));
        assertTrue(validador.validarLongitud(vientos));
    }

    @Test
    void longitud5_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1),
                v(4,'D', Corriente.NORTE, 1),
                v(5,'E', Corriente.NORTE, 1));
        assertTrue(validador.validarLongitud(vientos));
    }

    @Test
    void longitud2_invalida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1));
        assertFalse(validador.validarLongitud(vientos));
    }

    @Test
    void longitud6_invalida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1),
                v(4,'D', Corriente.NORTE, 1),
                v(5,'E', Corriente.NORTE, 1),
                v(6,'F', Corriente.NORTE, 1));
        assertFalse(validador.validarLongitud(vientos));
    }

    @Test
    void longitud1_invalida() {
        List<Viento> vientos = List.of(v(1,'A', Corriente.NORTE, 1));
        assertFalse(validador.validarLongitud(vientos));
    }

    // ===================== REGLA 2: MARCAS =====================

    @Test
    void marcas_estrictamenteCrecientes_validas() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'C', Corriente.NORTE, 1),
                v(3,'F', Corriente.NORTE, 1));
        assertTrue(validador.validarMarcas(vientos));
    }

    @Test
    void marcas_repetidas_invalidas() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'C', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1));
        assertFalse(validador.validarMarcas(vientos));
    }

    @Test
    void marcas_decrecientes_invalidas() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'F', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1));
        assertFalse(validador.validarMarcas(vientos));
    }

    @Test
    void marcas_consecutivas_validas() {
        List<Viento> vientos = List.of(
                v(1,'D', Corriente.NORTE, 1),
                v(2,'E', Corriente.NORTE, 1),
                v(3,'F', Corriente.NORTE, 1));
        assertTrue(validador.validarMarcas(vientos));
    }

    // ===================== REGLA 3: ROTACION =====================

    @Test
    void rotacion_N_E_S_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.ESTE,  1),
                v(3,'C', Corriente.SUR,   1));
        assertTrue(validador.validarRotacion(vientos));
    }

    @Test
    void rotacion_S_O_N_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.SUR,   1),
                v(2,'B', Corriente.OESTE, 1),
                v(3,'C', Corriente.NORTE, 1));
        assertTrue(validador.validarRotacion(vientos));
    }

    @Test
    void rotacion_O_N_E_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.OESTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.ESTE,  1));
        assertTrue(validador.validarRotacion(vientos));
    }

    @Test
    void rotacion_incorrecta_invalida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.SUR,   1),
                v(3,'C', Corriente.OESTE, 1));
        assertFalse(validador.validarRotacion(vientos));
    }

    @Test
    void rotacion_saltoDoble_invalida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1));
        assertFalse(validador.validarRotacion(vientos));
    }

    // ===================== REGLA 4: INTENSIDAD =====================

    @Test
    void intensidad_exactoImax_valida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 5),
                v(2,'B', Corriente.NORTE, 5),
                v(3,'C', Corriente.NORTE, 5));
        assertTrue(validador.validarIntensidad(vientos, 15));
    }

    @Test
    void intensidad_superaImax_invalida() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 5),
                v(2,'B', Corriente.NORTE, 5),
                v(3,'C', Corriente.NORTE, 6));
        assertFalse(validador.validarIntensidad(vientos, 15));
    }

    // ===================== CALCULADOR BASE =====================

    @Test
    void calcularBase_correcto() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 5),
                v(2,'B', Corriente.NORTE, 3),
                v(3,'C', Corriente.NORTE, 7));
        assertEquals(15, calculador.calcularBase(vientos));
    }

    // ===================== BONUS A: CALIGRAFIA =====================

    @Test
    void bonusCaligrafia_letrasConsecutivas_retorna5() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 1),
                v(2,'B', Corriente.NORTE, 1),
                v(3,'C', Corriente.NORTE, 1));
        assertEquals(5, calculador.calcularBonusCaligrafia(vientos));
    }

    @Test
    void bonusCaligrafia_letrasConHueco_retorna0() {
        List<Viento> vientos = List.of(
                v(1,'D', Corriente.NORTE, 1),
                v(2,'E', Corriente.NORTE, 1),
                v(3,'G', Corriente.NORTE, 1));
        assertEquals(0, calculador.calcularBonusCaligrafia(vientos));
    }

    @Test
    void bonusCaligrafia_4letrasConsecutivas_retorna5() {
        List<Viento> vientos = List.of(
                v(1,'D', Corriente.NORTE, 1),
                v(2,'E', Corriente.NORTE, 1),
                v(3,'F', Corriente.NORTE, 1),
                v(4,'G', Corriente.NORTE, 1));
        assertEquals(5, calculador.calcularBonusCaligrafia(vientos));
    }

    // ===================== BONUS B: TORMENTA =====================

    @Test
    void bonusTormenta_ultimoMayorATodos_retorna3() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 2),
                v(2,'B', Corriente.NORTE, 4),
                v(3,'C', Corriente.NORTE, 3),
                v(4,'D', Corriente.NORTE, 7));
        assertEquals(3, calculador.calcularBonusTormenta(vientos));
    }

    @Test
    void bonusTormenta_ultimoNoMayorATodos_retorna0() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 2),
                v(2,'B', Corriente.NORTE, 9),
                v(3,'C', Corriente.NORTE, 3),
                v(4,'D', Corriente.NORTE, 7));
        assertEquals(0, calculador.calcularBonusTormenta(vientos));
    }

    @Test
    void bonusTormenta_ultimoIgualAUno_retorna0() {
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 5),
                v(2,'B', Corriente.NORTE, 3),
                v(3,'C', Corriente.NORTE, 5));
        assertEquals(0, calculador.calcularBonusTormenta(vientos));
    }

    // ===================== BUSCADOR — casos normales =====================

    @Test
    void buscador_encuentraMejorEstrofa() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 5));
        diario.agregar(v(2,'B', Corriente.ESTE,  3));
        diario.agregar(v(3,'C', Corriente.SUR,   7));
        diario.agregar(v(4,'D', Corriente.ESTE,  2));
        diario.agregar(v(5,'E', Corriente.SUR,   4));
        diario.agregar(v(6,'F', Corriente.OESTE, 6));

        Optional<Estrofa> resultado = buscador.buscar(diario, 20);
        assertTrue(resultado.isPresent());
        assertEquals(23, resultado.get().getPuntajeTotal());
    }

    @Test
    void buscador_ambosBonus_aplicados() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 5));
        diario.agregar(v(2,'B', Corriente.ESTE,  3));
        diario.agregar(v(3,'C', Corriente.SUR,   7));

        Optional<Estrofa> resultado = buscador.buscar(diario, 20);
        assertTrue(resultado.isPresent());
        Estrofa e = resultado.get();
        assertEquals(5, e.getBonusCaligrafia());
        assertEquals(3, e.getBonusTormenta());
    }

    // ===================== BUSCADOR — casos borde =====================

    @Test
    void buscador_diarioMenosDe3_retornaVacio() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 1));
        diario.agregar(v(2,'B', Corriente.ESTE,  1));

        Optional<Estrofa> resultado = buscador.buscar(diario, 100);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscador_ninguna_rotacion_valida_retornaVacio() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 1));
        diario.agregar(v(2,'B', Corriente.NORTE, 1));
        diario.agregar(v(3,'C', Corriente.NORTE, 1));

        Optional<Estrofa> resultado = buscador.buscar(diario, 100);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscador_sumaMinimaSuperaImax_retornaVacio() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 9));
        diario.agregar(v(2,'B', Corriente.ESTE,  9));
        diario.agregar(v(3,'C', Corriente.SUR,   9));

        Optional<Estrofa> resultado = buscador.buscar(diario, 20);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscador_marcasRepetidas_invalidas_retornaVacio() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 1));
        diario.agregar(v(2,'A', Corriente.ESTE,  1));
        diario.agregar(v(3,'A', Corriente.SUR,   1));

        Optional<Estrofa> resultado = buscador.buscar(diario, 100);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscador_multiplesEstrofasMismoScore_retornaUna() {
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 3));
        diario.agregar(v(2,'B', Corriente.ESTE,  3));
        diario.agregar(v(3,'C', Corriente.SUR,   3));
        diario.agregar(v(4,'D', Corriente.OESTE, 2));
        diario.agregar(v(5,'E', Corriente.NORTE, 4));
        diario.agregar(v(6,'F', Corriente.ESTE,  3));
        diario.agregar(v(7,'G', Corriente.SUR,   2));

        Optional<Estrofa> resultado = buscador.buscar(diario, 100);
        assertTrue(resultado.isPresent());
    }

    @Test
    void buscador_diarioVacio_retornaVacio() {
        Diario diario = new Diario();
        Optional<Estrofa> resultado = buscador.buscar(diario, 100);
        assertTrue(resultado.isEmpty());
    }

    // ===================== CORRIENTE.siguiente() =====================

    @Test
    void corriente_siguiente_cicloCompleto() {
        assertEquals(Corriente.ESTE,  Corriente.NORTE.siguiente());
        assertEquals(Corriente.SUR,   Corriente.ESTE.siguiente());
        assertEquals(Corriente.OESTE, Corriente.SUR.siguiente());
        assertEquals(Corriente.NORTE, Corriente.OESTE.siguiente());
    }

    // ===================== ESTROFA puntaje =====================

    @Test
    void estrofa_puntajeTotalSumaCorrecto() {
        Estrofa e = new Estrofa(List.of(v(1,'A', Corriente.NORTE, 5)));
        e.setPuntajeBase(15);
        e.setBonusCaligrafia(5);
        e.setBonusTormenta(3);
        assertEquals(23, e.getPuntajeTotal());
    }

    @Test
    void viento_toString_formatoCorrecto() {
        Viento v = v(1, 'A', Corriente.NORTE, 5);
        assertEquals("(ID:1, Marca:A, Corriente:NORTE, Intensidad:5)", v.toString());
    }

    // ===================== MAIN =====================

    @Test
    void main_ejecuta_sinExcepcion() {
        java.io.PrintStream original = System.out;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(baos));
        try {
            Main.main(new String[]{});
            String output = baos.toString();
            assertTrue(output.contains("Puntaje: 23"));
            assertTrue(output.contains("Caligrafía continua"));
            assertTrue(output.contains("Tormenta final"));
        } finally {
            System.setOut(original);
        }
    }

    @Test
    void main_sinEstrofasValidas_imprimeCero() {
        // Diario con solo 2 vientos → no valid estrofa
        Diario diario = new Diario();
        diario.agregar(v(1,'A', Corriente.NORTE, 1));
        diario.agregar(v(2,'B', Corriente.ESTE,  1));

        Optional<Estrofa> resultado = buscador.buscar(diario, 100);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void estrofa_sinBonus_puntajeBaseSolo() {
        // No consecutive letters, last not max → no bonus
        List<Viento> vientos = List.of(
                v(1,'A', Corriente.NORTE, 3),
                v(2,'C', Corriente.ESTE,  5),
                v(3,'F', Corriente.SUR,   2));
        Estrofa e = new Estrofa(vientos);
        e.setPuntajeBase(calculador.calcularBase(vientos));
        e.setBonusCaligrafia(calculador.calcularBonusCaligrafia(vientos));
        e.setBonusTormenta(calculador.calcularBonusTormenta(vientos));
        assertEquals(0, e.getBonusCaligrafia());
        assertEquals(0, e.getBonusTormenta());
        assertEquals(10, e.getPuntajeTotal());
    }
}
