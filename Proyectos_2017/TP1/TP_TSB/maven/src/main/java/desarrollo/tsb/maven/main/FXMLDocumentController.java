package main.java.desarrollo.tsb.maven.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import javax.swing.*;

public class FXMLDocumentController implements Initializable {

    private GestorDocument gestor = GestorDocument.getInstance();

    @FXML
    private Button btnCargar;

    @FXML
    private TextField txtPalabra;

    @FXML
    private Label lblResultado;

    @FXML
    private Label lblArchivo;

    @FXML
    private Button btnLimpiar;

    @FXML
    private TextArea txtArchivo;

    @FXML
    private Button agregarButton;

    @FXML
    private Label lblArchivo1;
    
    public void initialize(URL url, ResourceBundle rb) {
        //Acá cuando se inicializa se carga la hashtable desde el archivo
        try {
            File archivoHashTable = new File("maven/resources/hashTable.dat");
            if (archivoHashTable.exists() && !archivoHashTable.isDirectory()) {
                txtArchivo.setText("Cargando hashtable...");
                TSBSimpleListReader reader = new TSBSimpleListReader(archivoHashTable.getAbsolutePath());
                gestor.setHashTable(reader.read());
                txtArchivo.setText(gestor.entriesToString());
            }
        }
        catch (Exception e) {
            System.err.println("Error en la lectura del archivo de la hashtable!");
        }
    }

    @FXML
    private void btnCargarClick(ActionEvent event) throws Exception {
        txtArchivo.setText("");
        FileChooser fc = new FileChooser();
        File resourcesDirectory = new File("maven/resources/");
        fc.setTitle("Seleccionar archivo de texto");
        fc.setInitialDirectory(resourcesDirectory);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento de texto (*.txt)", "*.txt"));
        File file = fc.showOpenDialog(null);
        if (file != null)
        {
            try {
                lblArchivo.setText(file.getAbsolutePath());
                gestor.getArchivo().setFile(file);
                txtArchivo.setText(gestor.contarRepeticiones());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnBuscarClick(ActionEvent event) {
        if(txtPalabra.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No inserto palabra a buscar");
        }
        else {
            String mensaje = "La palabra " + txtPalabra.getText() + " apareció ";
            mensaje += gestor.getCantidadVeces(txtPalabra.getText()) + " veces!";
            JOptionPane.showMessageDialog(null, mensaje);
        }
    }

    @FXML
    private void btnLimpiarClick(ActionEvent event) {
        txtArchivo.setText("");
        txtPalabra.setText("");
        lblResultado.setText("");
    }

}
