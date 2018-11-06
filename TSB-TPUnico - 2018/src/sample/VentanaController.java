package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class VentanaController
{
    public Pane pane;
    public Button btnNueva;
    public Button btnAbrir;
    public Button btnAgregar;
    public Button btnGuardar;
    public Button btnBuscar;
    public TextField txtNombreTabla;
    public TextField txtIngresar;
    public Button btnSalir;
    public Label lblCantidadPalabras;
    public Label lblFrecuencia;

    private GestorSerializacion ser = new GestorSerializacion();
    private GestorCarga ges;
    private TSB_OAHashtable tablahash = null;
    private String nameTable = null;
    private File archivoOriginal = null;

    private boolean activoAgregar = false;
    private boolean activoAbrir = false;
    private boolean activoNueva = false;

    public void btnNuevaAction(ActionEvent actionEvent)
    {
        txtNombreTabla.setDisable(false);
        txtNombreTabla.setText("");
        lblCantidadPalabras.setDisable(false);
        lblCantidadPalabras.setText("0");

        btnAgregar.setDisable(false);
        btnGuardar.setDisable(false);

        lblFrecuencia.setText("");
        lblFrecuencia.setDisable(true);
        txtIngresar.setText("");
        txtIngresar.setDisable(true);

        btnBuscar.setDisable(true);

        tablahash = null;
        nameTable = null;
        archivoOriginal = null;

        activoNueva = true;
        activoAbrir = false;
    }

    public void btnAbrirAction(ActionEvent actionEvent)
    {
        FileChooser cho = new FileChooser();
        File file = cho.showOpenDialog(null);

        if(!(file == null))
        {
            try
            {
                tablahash = ser.recuperar(file.getPath());

                if(tablahash != null)
                {
                    archivoOriginal = file;

                    txtNombreTabla.setDisable(false);
                    txtNombreTabla.setText(file.getName());
                    lblCantidadPalabras.setDisable(false);
                    lblCantidadPalabras.setText(Integer.toString(tablahash.size()));

                    btnAgregar.setDisable(false);
                    btnGuardar.setDisable(false);
                    btnBuscar.setDisable(false);
                    txtIngresar.setDisable(false);
                    lblFrecuencia.setDisable(false);

                    nameTable = file.getName();

                    activoAbrir = true;
                    activoNueva = false;
                }
            }
            catch(Exception e)
            {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error en la recuperación");
                a.setHeaderText(null);
                a.setContentText("El archivo seleccionado no es una tabla hash.");
                a.showAndWait();
            }
        }
    }

    public void btnAgregarAction(ActionEvent actionEvent)
    {
        if(activoNueva)
        {
            FileChooser cho = new FileChooser();
            File file = cho.showOpenDialog(null);

            if (file != null)
            {
                if (file.getName().endsWith(".txt"))
                {
                    ges = new GestorCarga(file);

                    TSBArrayList lis = ges.cargar();

                    if(tablahash == null) tablahash = new TSB_OAHashtable();

                    for(int i = 0; i < lis.size(); i++)
                    {
                        if(tablahash.containsKey(lis.get(i)))
                        {
                            int frec = (int) tablahash.get(lis.get(i));
                            frec = frec + 1;
                            tablahash.put(lis.get(i), frec);
                        }
                        else
                        {
                            tablahash.put(lis.get(i), 1);
                        }
                    }

                    lblCantidadPalabras.setText(Integer.toString(tablahash.size()));

                    activoAgregar = true;
                }
                else
                {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error en la selección");
                    a.setHeaderText(null);
                    a.setContentText("El archivo seleccionado no es un archivo de texto plano.");
                    a.showAndWait();
                }
            }
        }

        if(activoAbrir)
        {
            FileChooser cho = new FileChooser();
            File file = cho.showOpenDialog(null);

            if(file != null)
            {
                if(file.getName().endsWith(".txt"))
                {
                    ges = new GestorCarga(file);

                    TSBArrayList lis = ges.cargar();

                    for(int i=0; i<lis.size(); i++)
                    {
                        if(tablahash.containsKey(lis.get(i)))
                        {
                            int frec = (int) tablahash.get(lis.get(i));
                            frec = frec + 1;
                            tablahash.put(lis.get(i), frec);
                        }
                        else
                        {
                            tablahash.put(lis.get(i), 1);
                        }
                    }

                    lblCantidadPalabras.setText(Integer.toString(tablahash.size()));

                    activoAgregar = true;
                }
                else
                {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error en la selección");
                    a.setHeaderText(null);
                    a.setContentText("El archivo seleccionado no es un archivo de texto plano.");
                    a.showAndWait();
                }
            }
        }
    }

    public void btnGuardarAction(ActionEvent actionEvent)
    {
        String nom = txtNombreTabla.getText();

        if(activoNueva)
        {
            if(tablahash != null)
            {
                if(!nom.isEmpty())
                {
                    if(!nom.contains(".")) nom = nom + ".dat";

                    ser.guardar(tablahash, nom);

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Nueva tabla hash");
                    a.setHeaderText(null);
                    a.setContentText("Tabla hash " + nom + " creada con éxito.");
                    a.showAndWait();

                    txtIngresar.setDisable(false);
                    btnBuscar.setDisable(false);
                    lblFrecuencia.setDisable(false);
                }
                else
                {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Nueva tabla hash");
                    a.setHeaderText(null);
                    a.setContentText("Ingresar nombre de la tabla.");
                    a.showAndWait();
                }
            }
            else
            {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Nueva tabla hash");
                a.setHeaderText(null);
                a.setContentText("No se puede guardar una tabla vacía.");
                a.showAndWait();
            }
        }

        if(activoAbrir)
        {
            if(!nom.isEmpty())
            {
                if(activoAgregar)
                {
                    ser.guardar(tablahash, archivoOriginal.getPath());

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Agregar archivos");
                    a.setHeaderText(null);
                    a.setContentText("Archivos agregados con éxito.");
                    a.showAndWait();

                    activoAgregar = false;
                }

                if(!nom.contains(".")) nom = nom + ".dat";

                if(!nameTable.equals(nom))
                {
                    File renombrar = new File(nom);

                    if(!renombrar.exists())
                    {
                        archivoOriginal.renameTo(renombrar);

                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setTitle("Guardar cambios");
                        a.setHeaderText(null);
                        a.setContentText("Tabla hash renombrada con éxito.");
                        a.showAndWait();
                    }
                    else
                    {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Guardar cambios");
                        a.setHeaderText(null);
                        a.setContentText("Ya existe un archivo con ese nombre.");
                        a.showAndWait();
                    }
                }
            }
            else
            {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Guardar cambios");
                a.setHeaderText(null);
                a.setContentText("Ingresar nombre de la tabla.");
                a.showAndWait();
            }
        }
    }

    public void btnBuscarAction(ActionEvent actionEvent)
    {
        String palabra = txtIngresar.getText().toLowerCase();

        if(!palabra.isEmpty())
        {
            if(tablahash.containsKey(palabra))
            {
                int frec = (int) tablahash.get(palabra);
                lblFrecuencia.setText(Integer.toString(frec));
            }
            else
            {
                lblFrecuencia.setText(Integer.toString(0));
            }
        }
        else
        {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Buscar palabra");
            a.setHeaderText(null);
            a.setContentText("Ingresar palabra.");
            a.showAndWait();
        }
    }

    public void btnSalirAction(ActionEvent actionEvent)
    {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
}
