package com.tpfinaljava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class CalculadorCiclos {

    protected ArrayList<ArrayList<Short>> ciclos = new ArrayList<>();

    public abstract ArrayList<ArrayList<Short>> getCiclos(HashMap<Short, ArrayList<Short>> grafo, Grafo g) throws IOException;
    public abstract boolean existenCiclos(HashMap<Short, ArrayList<Short>> grafo, Short idnodo1, Short idnodo2);
}
