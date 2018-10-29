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
public class OAHashtableWriter
{
    private String arch = "tabla.dat";
    
    /**
    * Crea un objeto OAHashtableWriter. Supone que el nombre del archivo a grabar 
    * sera "tabla.dat".
    */
    public OAHashtableWriter()
    {
    }
  
    /**
    * Crea un objeto OAHashtableWriter. Fija el nombre del archivo que se graba con 
    * el nombre tomado como parametro.
    * @param nom el nombre del archivo a grabar.
    */
    public OAHashtableWriter(String nom)
    {
        arch = nom;
    }
      
    /**
    * Graba la tabla tomada como parametro.
    * @param ht la tabla a serializar.
    * @throws IOException si se encuentra un error de IO.
    */
    public void write (TSB_OAHashtable ht) throws IOException
    {
        try
        {
            FileOutputStream ostream = new FileOutputStream(arch);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
      
            p.writeObject(ht);
      
            p.flush(); 
            ostream.close();
        }
        catch( Exception e )
        {
            throw new IOException("No se pudo grabar la tabla... " + e.getMessage());
        }
    }
}
