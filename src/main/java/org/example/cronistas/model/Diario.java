package org.example.cronistas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Diario {
    private final List<Viento> vientos = new ArrayList<>();

    public void agregar(Viento v) {
        vientos.add(v);
    }

    public List<Viento> getVientos() {
        return Collections.unmodifiableList(vientos);
    }

    public int size() {
        return vientos.size();
    }
}
