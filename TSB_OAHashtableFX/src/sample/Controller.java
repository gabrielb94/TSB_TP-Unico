package sample;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Controller implements Initializable {

    private Archivo archivo;

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

    public void btnBuscar_click (ActionEvent evento)
    {
        txtFrec.setDisable(false);
        txtCant.setDisable(false);
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
