/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaz;

import Logica.ArchivoPalabras;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;
import javafx.stage.FileChooser;
import Logica.TSB_OAHashtable;
import java.io.IOException;
import javafx.scene.control.TextArea;

/**
 *
 * @author Lihuen
 */
public class FXMLDocumentController implements Initializable
{
    private ArchivoPalabras archivo;
    
    private TSB_OAHashtable<String,Integer> hash;
    @FXML
    private Button btn_agregar;
    @FXML
    private Label lbl_buscar;
    @FXML
    private TextField txt_buscar;
    @FXML
    private Label lbl_detalle;
    @FXML
    private Menu item_arch;
    @FXML
    private MenuItem item_cargar;
    @FXML
    private MenuItem item_salir;
    @FXML
    private Menu item_ayuda;
    @FXML
    private MenuItem item_acerca;    
    @FXML
    private TextArea tabla;
    @FXML
    private Button btn_buscar;
    @FXML
    private Label lbl_DatosPalabra;
    @FXML
    private Label lbl_cant;
            
    @FXML
    private void btn_agregarOnAction(ActionEvent event) throws IOException
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Abrir archivo de texto");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            archivo.setFile(file);
            tabla.setText(archivo.toString());
            hash = archivo.getHash();
            lbl_cant.setText(" " + hash.size());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        archivo = new ArchivoPalabras();
        hash = archivo.getHash();
        lbl_cant.setText(" " + hash.size());
        tabla.setText(archivo.toString());
        //TextFields.bindAutoCompletion(txt_buscar, archivo);        
    }

    @FXML
    private void btn_buscarOnAction(ActionEvent event)
    {
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
                if(valor == 1)
                    lbl_DatosPalabra.setText("La palabra " + palabra + " aparece " + valor + " vez.");
                else
                    lbl_DatosPalabra.setText("La palabra " + palabra + " aparece " + valor + " veces.");
            }
            else 
            {
                lbl_DatosPalabra.setText("Palabra NO encontrada");
            }
        }
    }
}

