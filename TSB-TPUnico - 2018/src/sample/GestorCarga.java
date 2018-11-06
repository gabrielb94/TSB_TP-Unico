package sample;

import java.io.File;
import java.text.Normalizer;
import java.util.Scanner;
import java.util.StringTokenizer;

public class GestorCarga
{
    private File file;
    private TSBArrayList list;

    public GestorCarga(File f)
    {
        file = f;
        list = new TSBArrayList();
    }

    public TSBArrayList cargar()
    {
        String aux;

        try(Scanner sc = new Scanner(file, "ISO-8859-1"))
        {
            StringTokenizer st; // para separar line en tokens
            String[] cadenas; // para almacenar las divisiones de split

            while (sc.hasNextLine())
            {
                st = new StringTokenizer(sc.nextLine()); // retorna los tokens

                while (st.hasMoreTokens())
                {
                    aux = st.nextToken().toLowerCase(); // pasa los tokens a minusculas
                    cadenas = aux.split("\\p{Punct}|\\p{Digit}|¡|¿|»|«"); // divide por caracteres especiales

                    for(int i=0; i<cadenas.length; i++)
                    {
                        // descompone letra de tilde
                        cadenas[i] = Normalizer.normalize(cadenas[i], Normalizer.Form.NFD);
                        // elimina los tildes y retorna solo la letra
                        cadenas[i] = cadenas[i].replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                        // almacena en un array
                        if(!cadenas[i].isEmpty())
                        {
                            list.add(list.size(), cadenas[i]);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }
}
