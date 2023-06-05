package com.tpfinaljava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DFS extends CalculadorCiclos{

    private HashSet<Short> nodosrecorridos = new HashSet();
    private HashSet<Short> nodosdelciclo = new HashSet();

    @Override
    public ArrayList<ArrayList<Short>> getCiclos(HashMap<Short, ArrayList<Short>> grafo, Grafo g) throws IOException {
        ciclos = new ArrayList<>();
        for (Map.Entry<Short, ArrayList<Short>> idnodo: grafo.entrySet()) {
            ArrayList<Short> ciclo = new ArrayList<>();
            //System.out.println("llega");
            DFS(idnodo.getKey(), ciclos, ciclo, nodosrecorridos, nodosdelciclo,grafo, g);
            nodosrecorridos.add(idnodo.getKey());
        }

        return ciclos;
    }

    private void DFS(Short idkey, ArrayList<ArrayList<Short>> ciclos, ArrayList<Short> ciclo, HashSet<Short> nodosrecorridos, HashSet<Short> nodosdelciclo, HashMap<Short,ArrayList<Short>> grafo, Grafo g) throws IOException {
        //Se guardan los ciclos en el archivo
        if (ciclos.size() >= Grafo.CANT_MAX_CICLOS_EN_MEMORIA)
            g.copiarCiclosAArchivo(ciclos);
        //Se verifica si el nodo ya pertenece al ciclo actual y si el nodo es el mismo al inicial
        if(nodosdelciclo.contains(idkey)) {
            if(ciclo.get(0).equals(idkey)){
                if((nodosdelciclo.size() >= Grafo.MIN_SIZE) ){
                    Main.cantciclos[nodosdelciclo.size() - Grafo.MIN_SIZE]++;
                    ciclos.add(((ArrayList<Short>)ciclo.clone()));
                }
            }
        }
        else{
            if (nodosdelciclo.size() < Grafo.MAX_SIZE) {
                //Se verifica que el nodo sea un paquete
                if (grafo.containsKey(idkey)) {
                    ciclo.add(idkey);
                    nodosdelciclo.add(idkey);
                    for (Short iddependencia : grafo.get(idkey)) {
                        //Se verifica si la dependencia ya fue recorrida como nodo inicial
                        if (!nodosrecorridos.contains(iddependencia)) {
                            DFS(iddependencia, ciclos, ciclo, nodosrecorridos, nodosdelciclo,grafo, g);
                        }
                    }
                    ciclo.remove(ciclo.size()-1);
                    nodosdelciclo.remove(idkey);
                }
            }
        }
    }

    public boolean existenCiclos(HashMap<Short, ArrayList<Short>> grafo, Short idnodo1, Short idnodo2)
    {
        boolean existen;
        ArrayList<Short> listanodo1 = grafo.get(idnodo1);
        ArrayList<Short> listanodo2 = grafo.get(idnodo2);
        HashSet<Short> nodosdelciclo = new HashSet<>();
        boolean contiene = false;
        //Recorremos el nodo que tenga la menor cantidad de dependencias
        if (listanodo1.size() > listanodo2.size())
        {
            existen = DFSExisteCiclo(grafo, idnodo2, new ArrayList<Short>(), idnodo1, contiene, nodosdelciclo);
        }
        else
        {
            existen = DFSExisteCiclo(grafo, idnodo1, new  ArrayList<Short>(), idnodo2, contiene, nodosdelciclo);
        }
        return existen;
    }

    //DFS que verifica si existe un ciclo entre dos nodos
    //Es el mismo metodo que el otro DFS, solo que verificamos con un booleano
    //si el ciclo contiene a los dos nodos requeridos
    private boolean DFSExisteCiclo(HashMap<Short, ArrayList<Short>> grafo, Short idnodo1, ArrayList<Short> ciclo, Short idnodo2, boolean contiene, HashSet<Short> nodosdelciclo) {
        if (idnodo1.equals(idnodo2))
            contiene = true;
        if (nodosdelciclo.contains(idnodo1)) {
            if (ciclo.get(0).equals(idnodo1)) {
                if ((nodosdelciclo.size() >= Grafo.MIN_SIZE)) {
                    if (contiene)
                        return true;
                }
            }
        } else {
            if (nodosdelciclo.size() < Grafo.MAX_SIZE) {
                if (grafo.containsKey(idnodo1)) {
                    ciclo.add(idnodo1);
                    nodosdelciclo.add(idnodo1);
                    for (Short iddependencia : grafo.get(idnodo1)) {
                        if (DFSExisteCiclo(grafo, iddependencia, ciclo, idnodo2, contiene, nodosdelciclo))
                            return true;
                    }
                    if (idnodo1.equals(idnodo2))
                        contiene = false;
                    ciclo.remove(ciclo.size() - 1);
                    nodosdelciclo.remove(idnodo1);
                }
            }
        }
        return false;
    }

}
