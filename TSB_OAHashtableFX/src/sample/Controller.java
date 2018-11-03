package sample;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;

import java.io.File;

public class Controller implements Initializable {

    private Archivo archivo;
    private TSBArrayList lista;
    private  TSBHashtable tablaHash;


    public Button btnNuevaTabla;
    public Button btnAbrirTabla;
    public Button btnBuscar;
    public Button btnAgregarArchivo;
    public Button btnGuardar;

    public TextField txtNombre;
    public TextField txtIngrese;
    public TextField txtFrec;
    public TextField txtCant;
    public label lbl_DatosPalabra;

    public void btnBuscarClic (ActionEvent evento)
    {
        //habilita
        txtFrec.setDisable(false);
        txtFrec.setEditable(true);
        if (txt_buscar.getText().isEmpty())
        {
            lbl_DatosPalabra.setText("No se ingreso ninguna palabra");
        }
        else
        {
            String palabra = txt_buscar.getText();
            if (archivo.buscarPalabra(palabra))
            {
                int valor = hash.get(palabra);
               txtFrec.setText(valor)
             }
         }
    }
    public void btnCrearTablaVaciaClic (Action evento)
    {
        //habilita
    txtNombre.setDisable(false);
    txtCant.setDisable(false);
    txtCant.setEditable(true);
    btnAgregarArchivo.setDisable(false);
    btnGuardar.setDisable(false);
    btnBuscar.setDisable(false);

    //crear tashtable vacia


    }

    public void btnAbrirTablaClic (ActionEven e)
    {
        //habilita
        txtNombre.setDisable(false);
        txtNombre.setEditable(true);
        txtCant.setDisable(false);
        txtCant.setEditable(true);
        btnAgregarArchivo.setDisable(false);
        btnGuardar.setDisable(false);
        btnBuscar.setDisable(false);

        //abrir subventana para buscar tabla serializada es archivo
        //txtBuscar.setEditable(true);
        FileChooser fc= new FileChooser();
        FileChooser.ExtensionFilter fe= new FileChooser.ExtensionFilter("doc",".txt");
// lo veremos en otra clase :)
        fc.setSelectedExtensionFilter(fe);  //FileChooser es la ventanita o algo asi
        fc.setTitle("Seleccione un archivo");
        File file= fc.showOpenDialog(null);
        archivo= new Archivo(file);
        String nombre= file.getName();
      //  lblArchivo.setText(nombre);
        lista=archivo.leer();
        //txtArchivo.setText(lista.toString());
    }

    public void  btnAgregarArchivoClic (ActionEven e)
    {
        //txtBuscar.setEditable(true);
        FileChooser fc= new FileChooser();
        FileChooser.ExtensionFilter fe= new FileChooser.ExtensionFilter("doc",".txt");
// lo veremos en otra clase :)
        fc.setSelectedExtensionFilter(fe);  //FileChooser es la ventanita o algo asi
        fc.setTitle("Seleccione un archivo");
        File file= fc.showOpenDialog(null);
        archivo= new Archivo(file);
        String nombre= file.getName();
        //falta actualizar cantidad de palabras
        //falta validar


    }

    public void btnGurdarCambiosClic (ActionEven e)
    {
//falta validar

        //si todo es correcto muestra el mensaje de exito

        Alert a= new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Guardado");
        a.setHeaderText(null);
        a.setContentText("El archivo ha sido agregado con éxito!!");
        a.showAndWait();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}
