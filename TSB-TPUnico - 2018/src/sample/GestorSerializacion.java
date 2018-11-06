package sample;

import java.io.*;

public class GestorSerializacion
{
    public GestorSerializacion()
    {

    }

    public void guardar(TSB_OAHashtable obj, String ruta)
    {
        try
        {
            ruta = ruta.replace("\\", "/");
            File file = new File(ruta);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(obj);

            oos.flush();
            fos.close();
        }
        catch(Exception e)
        {
            System.out.println("Error en la grabacion: " + e.getMessage());
        }
    }

    public TSB_OAHashtable recuperar(String ruta) throws Exception
    {
        TSB_OAHashtable obj = null;

        ruta = ruta.replace("\\", "/");

        File file = new File(ruta);
        FileInputStream fos = new FileInputStream(file);
        ObjectInputStream oos = new ObjectInputStream(fos);
        obj = (TSB_OAHashtable) oos.readObject();

        oos.close();
        fos.close();

        return obj;
    }
}
