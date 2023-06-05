package com.tpfinaljava;

import org.w3c.dom.*;
import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static int[] cantciclos;

    public static void main(String[] args) {

        try {

            //Creo Grafo y lector de archivos
            String nombrearchivo = "resultado.txt";
            LectorODEM lectorodem = new LectorODEM();
            Grafo grafoTarjan = new Grafo(nombrearchivo, new Tarjan());
            Grafo grafoDFS = new Grafo(nombrearchivo, new DFS());

            File file = lectorodem.getArchivo();
            Document document = lectorodem.getDocument(file);
            NodeList paquetes = lectorodem.getPaquetes(document);
            ArrayList<ArrayList<NodeList>> dependencias = lectorodem.getDependencias(paquetes);
            //Profundidad del ciclo máxima y mínima
            System.out.println("¿Desea utilizar Tarjan? (y/n)");
            Scanner entradaEscaner = new Scanner (System.in);
            Grafo grafoElegido;
            String response = entradaEscaner.next();
            if (response.equals("y") || response.equals("Y")){
                grafoElegido = grafoTarjan;
            }
            else{
                grafoElegido = grafoDFS;
            }
            grafoElegido.addNodos(paquetes, dependencias);

            //Profundidad del ciclo máxima y mínima
            System.out.println("Ingrese la profunidad máxima");
            grafoElegido.setMAX_SIZE(entradaEscaner.nextInt());
            System.out.println("Ingrese la profunidad mínima");
            grafoElegido.setMIN_SIZE(entradaEscaner.nextInt());
            System.out.println("Cantidad de paquetes = "+paquetes.getLength());
            int cantDependencias = 0;
            for (int i=0; i<dependencias.size(); i++){
                ArrayList<NodeList> alnl = dependencias.get(i);
                for (int j=0; j<alnl.size(); j++){
                    cantDependencias++;
                }
            }
            System.out.println("Cantidad de dependencias = "+cantDependencias);
            cantciclos = new int[Grafo.MAX_SIZE- Grafo.MIN_SIZE + 1];

            //Se guardan los ciclos en un archivo, el cual su nombre se pasa como parámetro al construir el grafo
            Instant antes = Instant.now();
            grafoElegido.guardarCiclos();
            Instant despues = Instant.now();
            System.out.println("El timepo para encontrar los ciclos es de: " + Duration.between(despues, antes));
            //grafo.guardarCiclosSinRecursion();

            for (int i = 0; i < cantciclos.length; i++)
            {
                System.out.println("Cantidad de ciclos de profundidad " + (i+grafoElegido.getMIN_SIZE()) + " = " + cantciclos[i]);
            }

            System.out.println("Los ciclos obtenidos se encuentran en el archivo resultado.txt en una carpeta dentro del proyecto");

            System.out.println("Desea verificar si existen ciclos entre dos nodos? (y/n)");
            String respuesta = entradaEscaner.next();
            if (respuesta.equals("y") || respuesta.equals("Y"))
            {
                grafoElegido.showNames();
                System.out.println("Ingrese el primer nodo");
                Short idprimernodo = Short.parseShort(entradaEscaner.next());
                System.out.println("Ingrese el segundo nodo");
                Short idsegundonodo = Short.parseShort(entradaEscaner.next());
                boolean existen = grafoElegido.existenCiclos(idprimernodo,idsegundonodo);
                if (existen)
                    System.out.println("Existen ciclos");
                else
                    System.out.println("No existen ciclos");

            }

        }
        catch (Exception e){ e.printStackTrace();}

    }
}