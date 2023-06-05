package com.tpfinaljava;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.*;

public class Grafo {

    public static final int CANT_MAX_CICLOS_EN_MEMORIA = 1000000 ;
    public static int MIN_SIZE = 3;
    public static int MAX_SIZE = 3;
    private HashMap<Short, ArrayList<Short>> grafo;
    private HashMap<String,Short> idpaquetes;
    private HashMap<Short,String> nombrepaquetes;
    private OutputStreamWriter archivo;
    private String nombrearchivo;
    private CalculadorCiclos calculadorCiclos;

    public Grafo(String nombrearchivo, CalculadorCiclos calculadorCiclos) throws FileNotFoundException {
        this.grafo = new HashMap<>();
        this.idpaquetes = new HashMap<>();
        this.nombrepaquetes = new HashMap<>();
        this.nombrearchivo = nombrearchivo;
        this.archivo = new OutputStreamWriter(
                new FileOutputStream(nombrearchivo), Charset.forName("UTF-8").newEncoder());
        this.calculadorCiclos = calculadorCiclos;
    }

    public Grafo(CalculadorCiclos calculadorCiclos){
        this.grafo = new HashMap<>();
        this.idpaquetes = new HashMap<>();
        this.nombrepaquetes = new HashMap<>();
        this.calculadorCiclos = calculadorCiclos;
    }

    public void addNodo(Short paquete, ArrayList<Short> dependencias) {
        grafo.put(paquete, dependencias);
    }

    public void addNodos(NodeList paquetes, ArrayList<ArrayList<NodeList>> nldependencias) {
        for (int i = 0; i < paquetes.getLength(); i++) {
            Element ePaquete = (Element) paquetes.item(i);
            String paquete = ePaquete.getAttribute("name");
            Short idpaquete;
            //Le generamos un id al paquete si todavia no tiene
            generarId(paquete);
            idpaquete = idpaquetes.get(paquete);
            ArrayList<Short> dependencias = getDependencias(nldependencias.get(i),paquete);
            this.addNodo(idpaquete, dependencias);
        }
    }

    public ArrayList<Short> getDependencias(ArrayList<NodeList> nlDependencias, String paquete) {
        ArrayList<Short> dependencias = new ArrayList<>();
        for(int d = 0 ; d <nlDependencias.size(); d++) {
            NodeList aux = nlDependencias.get(d);
            for (int i = 0; i < aux.getLength(); i++) {
                Element eDependencia = (Element) aux.item(i);
                String dependencia = eDependencia.getAttribute("name");
                //Quitamos las inner classes
                if (!dependencia.contains("$"))
                {
                    //Nos quedamos con el paquete de la dependencia
                    dependencia = dependencia.substring(0, dependencia.lastIndexOf("."));
                    //Verificamos que el paquete no sea de java y que no sea una dependencia cíclica
                    if (!dependencia.startsWith("java") && !dependencia.equals(paquete)) {
                        //Le generamos un id al paquete si todavía no tiene
                        generarId(dependencia);
                        Short iddependencia = idpaquetes.get(dependencia);
                        if (!dependencias.contains(iddependencia)) {
                            dependencias.add(idpaquetes.get(dependencia));
                        }
                    }
                }
            }
        }
        return dependencias;
    }

    public void generarId(String paquete)
    {
        if (!idpaquetes.containsKey(paquete)){
            nombrepaquetes.put((short) idpaquetes.size(), paquete);
            idpaquetes.put(paquete, (short) idpaquetes.size());
        }
    }

    public void guardarCiclos() throws IOException
    {
        ArrayList<ArrayList<Short>> ciclos = new ArrayList<>();
        ciclos = calculadorCiclos.getCiclos(grafo, this);
        //guardar los ciclos en los archivos
        copiarCiclosAArchivo(ciclos);
        archivo.close();
    }

    //Método que copia los ciclos al archivo del grafo
    public void copiarCiclosAArchivo(ArrayList<ArrayList<Short>> ciclos) throws IOException {
        for (ArrayList<Short> ciclo : ciclos) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ciclo.size(); i++) {
                builder.append(nombrepaquetes.get(ciclo.get(i)));
                builder.append(";");
            }
            builder.append("\n");
            archivo.write(builder.toString());
        }
        ciclos.clear();
    }

    public boolean existenCiclos(Short idnodo1, Short idnodo2)
    {
        return calculadorCiclos.existenCiclos(grafo, idnodo1, idnodo2);
    }

    //Guarda los ciclos haciendo un DFS de forma no recursiva

    /*public void guardarCiclosSinRecursion() throws IOException {
        ArrayList<ArrayList<Short>> idciclos = new ArrayList<>();
        HashSet<Short> nodosrecorridos = new HashSet();
        HashSet<Short> nodosdelciclo = new HashSet();
        for (Map.Entry<Short, ArrayList<Short>> nodo: grafo.entrySet()) {
            ArrayList<Short> ciclo = new ArrayList<>();
            DFSSinRecursion(nodo.getKey(), idciclos, ciclo, nodosrecorridos, nodosdelciclo);
            nodosrecorridos.add(nodo.getKey());
        }
        copiarCiclosAArchivo(idciclos);
        archivo.close();
    }

    private void DFSSinRecursion(Short key, ArrayList<ArrayList<Short>> ciclos, ArrayList<Short> ciclo, HashSet<Short> nodosrecorridos, HashSet<Short> nodosdelciclo) throws IOException {
        int[] cantdependencias = new int[MAX_SIZE];
        Arrays.fill(cantdependencias,0);
        int posicion = 0;
        cantdependencias[posicion] = grafo.get(key).size();
        ArrayList<Short> dependencias = grafo.get(key);
        ciclo.add(key);
        nodosdelciclo.add(key);
        while (!dependencias.isEmpty()){
            if (ciclos.size() >= CANT_MAX_CICLOS_EN_MEMORIA)
                copiarCiclosAArchivo(ciclos);
            while(posicion >= 0 && cantdependencias[posicion]==0)
            {
                nodosdelciclo.remove(ciclo.get(ciclo.size()-1));
                ciclo.remove(ciclo.size()-1);
                posicion--;
            }
            Short dependenciaacutal = dependencias.remove(dependencias.size()-1);
            cantdependencias[posicion]--;
            if(nodosdelciclo.contains(dependenciaacutal)){
                if (ciclo.get(0) == dependenciaacutal){
                    if((nodosdelciclo.size() >= MIN_SIZE) ) {
                        Main.cantciclos[nodosdelciclo.size() - MIN_SIZE]++;
                        ciclos.add(((ArrayList<Short>) ciclo.clone()));
                    }
                }
            }
            else {
                if (nodosdelciclo.size() < MAX_SIZE) {
                    if (grafo.containsKey(dependenciaacutal)) {
                        posicion++;
                        ciclo.add(dependenciaacutal);
                        nodosdelciclo.add(dependenciaacutal);
                        ArrayList<Short> aux = new ArrayList<>();
                        for (Short dependencia : this.grafo.get(dependenciaacutal)) {
                            if (!nodosrecorridos.contains(dependencia)) {
                                aux.add(dependencia);
                                cantdependencias[posicion]++;
                                //dependencias.add(dependencia);
                            }
                        }
                        dependencias.addAll(aux);
                    }
                }
            }
        }
        nodosdelciclo.clear();
        ciclo.clear();
    }*/

    public void showNames()
    {
        for (Map.Entry<String, Short> paquetes: idpaquetes.entrySet()) {
            System.out.println("El paquete: "+paquetes.getKey()+" es el nro: "+paquetes.getValue());
        }
    }

    public void setMIN_SIZE(int MIN_SIZE) {
        this.MIN_SIZE = MIN_SIZE;
    }

    public void setMAX_SIZE(int MAX_SIZE)
    {
        this.MAX_SIZE = MAX_SIZE;
    }

    public int getMIN_SIZE()
    {
        return this.MIN_SIZE;
    }

}