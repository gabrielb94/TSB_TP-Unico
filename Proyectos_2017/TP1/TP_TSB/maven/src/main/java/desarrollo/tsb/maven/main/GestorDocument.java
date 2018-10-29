package main.java.desarrollo.tsb.maven.main;

/*
* Esta clase se encarga de tener la instancia de la hashtable y de la serialización del fichero
*/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@SuppressWarnings("unchecked")
public class GestorDocument {

    //region SINGLETON
    private static GestorDocument instance;
    public static GestorDocument getInstance() {
        if (instance == null) {
            instance = new GestorDocument();
        }
        return instance;
    }
    //endregion

    private TSB_OAHashtable hashTable;
    private Archivo archivo;

    private GestorDocument() {
        hashTable = new TSB_OAHashtable<String, Integer>(200000);
        archivo = new Archivo();
    }

    public Archivo getArchivo() {
        return archivo;
    }

    public void setHashTable(TSB_OAHashtable newHashtable) {
        this.hashTable = newHashtable;
    }

    public int getCantidadVeces(String palabra) {
        if (hashTable.get(palabra) != null) {
            return (int) hashTable.get(palabra);
        }
        else {
            return 0;
        }
    }

    public String contarRepeticiones() throws FileNotFoundException {
        try {
            FileReader fr = new FileReader(archivo.getFile());
            BufferedReader buff = new BufferedReader(fr);
            Scanner sc = new Scanner(buff);
            while (sc.hasNextLine()) {
                String oracion = sc.nextLine();
                for (String palabra : oracion.toLowerCase().replaceAll("[^A-Za-z]+", " ").split("\\s+")) {
                    if (palabra.equals("")) continue;
                    if (hashTable.containsKey(palabra)) {
                        int repeticiones = (int)hashTable.get(palabra);
                        repeticiones++;
                        hashTable.put(palabra, repeticiones);
                    }
                    else {
                        hashTable.put(palabra, 1);
                    }
                }
            }

        }
        catch(Exception e) {
            throw e;
        }
        return entriesToString();
    }

    public String entriesToString() {
        StringBuilder br = new StringBuilder();
        Set<String> entries = hashTable.keySet();
        if (hashTable.size() > 0) {
            for (String e : entries) {
                if (hashTable.containsKey(e)) {
                    int value = (int) hashTable.get(e);
                    br.append("Palabra: " + e + " Repeticiones: " + value + "\n");
                }
            }
        }
        return br.toString();
    }

    //Metodo que se ejecuta una vez que la stage se comienza a cerrar
    public void onClosingWindow() throws Exception {
        try {
            TSBSimpleListWriter writer = new TSBSimpleListWriter("maven/resources/hashTable.dat");
            writer.write(hashTable);
        }
        catch (TSBSimpleListIOException e) {
            throw e;
        }

    }

}
