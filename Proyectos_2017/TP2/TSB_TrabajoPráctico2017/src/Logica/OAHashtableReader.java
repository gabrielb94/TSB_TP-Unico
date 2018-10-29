/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.io.*;

/**
 *
 * @author Emanuel Laurent
 */
public class OAHashtableReader
{
    private String arch = "tabla.dat";
    
    /**
    * Crea un objeto OAHashtableReader. Asume que el nombre del archivo desde el 
    * cual se recupera es "tabla.dat".
    */
    public OAHashtableReader()
    {
    }
  
    /**
    * Crea un objeto OAHashtableReader. Fija el nombre del archivo desde el cual 
    * se recupera con el nombre tomado como parametro.
    * @param nom el nombre del archivo a abrir para iniciar la recuperacion.
    */
    public OAHashtableReader(String nom)
    {
        arch = nom;
    }
      
      
    /**
    * Recupera una OAHashtable desde un archivo serializado.
    * @throws IOException si se encuentra un error de IO.
    * @return una referencia al objeto recuperado.
    */
    public TSB_OAHashtable read() throws IOException
    {
        TSB_OAHashtable ht = null;
       
        try
        {
            FileInputStream istream = new FileInputStream(arch);
            ObjectInputStream p = new ObjectInputStream(istream);
        
            ht = ( TSB_OAHashtable ) p.readObject();
             
            p.close();
            istream.close();
        }
        catch (Exception e)
        {
            throw new IOException("No se pudo recuperar la tabla: " + e.getMessage());
        }
        return ht;
    }
}
