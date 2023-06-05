package com.tpfinaljava;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class LectorODEM {

    public File getArchivo () {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showDialog(null, "seleccione el archivo");
        File file = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        return file;
    }

    public Document getDocument(File file) throws ParserConfigurationException, IOException, SAXException {
        //Crear un DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        dBuilder.setEntityResolver((publicId, systemId) -> {
            //Ignoramos las lineas que contengan .dtd en el archivo
            if (systemId.contains(".dtd")) {
                return new InputSource(new StringReader(""));
            } else {
                return null;
            }
        });
        //Crear un documento a partir de un archivo o stream
        File inputFile = new File(file.toString());
        Document document = dBuilder.parse(inputFile);
        return document;
    }

    public NodeList getPaquetes(Document document)
    {
        NodeList nList = document.getElementsByTagName("namespace");
        return nList;
    }

    //Obtenemos las dependencias en una lista en la que cada elemento tiene una lista de las dependencias
    //de un paquete especifico, por ejemplo, el elemento 1, tiene una lista de dependencias del paquete 1
    public ArrayList<ArrayList<NodeList>> getDependencias(NodeList paquetes) {
        ArrayList<ArrayList<NodeList>> resultado = new ArrayList<>();

        ArrayList<NodeList> listaclases = new ArrayList<>();
        NodeList dependencias;
        for(int p = 0;p < paquetes.getLength();p++){
            ArrayList<NodeList> listadependencias = new ArrayList<>();
            NodeList clases = ((Element) paquetes.item(p)).getElementsByTagName("type");
            for (int i = 0; i < clases.getLength(); i++) {
                dependencias = ((Element) clases.item(i)).getElementsByTagName("depends-on");
                listadependencias.add(dependencias);
            }
            resultado.add(listadependencias);
        }
        return resultado;
    }
}