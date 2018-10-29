package main.java.desarrollo.tsb.maven.main;

/**
 * Una clase usada para grabar objetos de la clase TSBSimpleList 
 * mediante Serializacion.
 * 
 * @author Ing. Valerio Frittelli.
 * @version Septiembre de 2017.
 */
import java.io.*;
import java.util.Hashtable;

public class TSBSimpleListWriter
{
      // nombre del archivo serializado...
      private String arch = "lista.dat";
    
      /**
       * Crea un objeto SimpleListWriter. Supone que el nombre del archivo a grabar 
       * sera "lista.dat".
       */
      public TSBSimpleListWriter()
      {
      }
      
      /**
       * Crea un objeto SimpleListWriter. Fija el nombre del archivo que se graba con 
       * el nombre tomado como parametro.
       * @param nom el nombre del archivo a grabar.
       */
      public TSBSimpleListWriter(String nom)
      {
            arch = nom;
      }
      
      /**
       * Graba la lista tomada como parametro.
       * @param ht la lista a serializar.
       * @throws TSBSimpleListIOException si se encuentra un error de IO.
       */
      public void write (TSB_OAHashtable ht) throws TSBSimpleListIOException
      {
           try
           {
                FileOutputStream ostream = new FileOutputStream(arch);
                ObjectOutputStream p = new ObjectOutputStream(ostream);

                p.writeObject(ht);

                p.flush();
                ostream.close();
           }
           catch ( Exception e )
           {
                e.printStackTrace();
                throw new TSBSimpleListIOException("No se pudo grabar la lista: " + e.getMessage());
           }
      }
}
