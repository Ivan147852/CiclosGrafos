package com.tpfinaljava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class Tarjan extends CalculadorCiclos {

    private Integer indice;
    private ArrayList<VerticeTarjan> stack;
    private CalculadorCiclos dfs = new DFS();
    private HashMap<Short,VerticeTarjan> idTarjan = new HashMap<>();
    private HashMap<VerticeTarjan,Short> nombresTarjan = new HashMap<>();
    private ArrayList<HashMap<Short, ArrayList<Short>>> componentesFuertementeConectados = new ArrayList<>();

    @Override
    public ArrayList<ArrayList<Short>> getCiclos(HashMap<Short, ArrayList<Short>> grafo, Grafo g) throws IOException {
        ciclos = new ArrayList<>();
        //Para cada nodo del grafo
        for (Short id:grafo.keySet()) {
            //Se añade el id a "idTarjan" y el nombre del nodo a "nombresTarjan"
            VerticeTarjan verticeTarjan = new VerticeTarjan();
            idTarjan.put(id,verticeTarjan);
            nombresTarjan.put(verticeTarjan,id);
        }
        indice = 0;
        //Se inicializa la pila del tarjan en vacio
        stack = new ArrayList<>();
        //Para cada vertice del grafo, si todavía no fue recorrido, busco su componente fuertemente conectado
        for (Short idVertice: grafo.keySet()) {
            VerticeTarjan vertice = idTarjan.get(idVertice);
            if(vertice.getIndice() == -1){
                strongConnect(grafo, vertice);
                //System.out.println("voy a buscar componentes fuertemente conectados");
            }
            for (int i=0; i<componentesFuertementeConectados.size(); i++)
                ciclos.addAll(dfs.getCiclos(componentesFuertementeConectados.get(i), g));
        }

        return ciclos;
    }

    private void strongConnect(HashMap<Short, ArrayList<Short>> grafo, VerticeTarjan vertice){
        vertice.setIndice(indice);
        vertice.setLowLink(indice);
        indice += 1;
        stack.add(vertice);
        vertice.setEnStack(true);
        ArrayList<Short> verticesSucesores = grafo.get(nombresTarjan.get(vertice));
        //Recorro todos los sucesores
        for (Short idVerticeSucesor: verticesSucesores) {
            VerticeTarjan verticeSucesor = idTarjan.get(idVerticeSucesor);
            //Si el vertice no existe, es porque no tenia que recorrer
            if (verticeSucesor != null)
            {
                //Si el sucesor no fue explorado
                if(verticeSucesor.getIndice() == -1){
                    //Recursion
                    strongConnect(grafo, verticeSucesor);
                    //Me quedo con el menor low link entre el vertice actual y el sucesor
                    vertice.setLowLink(Math.min(vertice.getLowLink(), verticeSucesor.getLowLink()));
                }
                //Si el sucesor fue explorado
                else{
                    //Y está en stack
                    if(verticeSucesor.isEnStack()){
                        //Me quedo con el de menor low link
                        if(vertice.getLowLink() >= verticeSucesor.getLowLink()) {
                            vertice.setLowLink(verticeSucesor.getLowLink());
                        }
                    }
                }
            }
        }
        //Una vez que recorre todos los sucesores
        //Si el vertice actual es el que inicia un componente fuertemente conectado
        if(vertice.getLowLink() == vertice.getIndice()){
            //Comenzamos un nuevo componente fuertemente conectado
            HashMap<Short, ArrayList<Short>> componenteFuertementeConectado = new HashMap<>();
            for (int i=stack.size()-1; i>=0; i--) {
                VerticeTarjan verticeStack = stack.get(i);
                stack.remove(i);
                verticeStack.setEnStack(false);
                ArrayList<Short> sucesores = grafo.get(nombresTarjan.get(verticeStack));
                componenteFuertementeConectado.put(nombresTarjan.get(verticeStack),sucesores);
                if (verticeStack.equals(vertice))
                {
                    componentesFuertementeConectados.add(componenteFuertementeConectado);
                    break;
                }
            }
        }
    }

    public boolean existenCiclos(HashMap<Short, ArrayList<Short>> grafo, Short idnodo1, Short idnodo2)
    {
        VerticeTarjan vertice1 = idTarjan.get(idnodo1);
        VerticeTarjan vertice2 = idTarjan.get(idnodo2);
        if (vertice1.getLowLink() == vertice2.getLowLink())
        {
            return true;
        }
        return false;
    }

}
