package main.java.desarrollo.tsb.maven.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

@SuppressWarnings("unchecked")
public class Main extends Application {

	//@Override
	public void start(Stage stage) throws Exception 
	{
        Parent root = FXMLLoader.load(getClass().getResource("/main/java/desarrollo/tsb/maven/main/FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Trabajo Práctico TSB");
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest( x -> {
            try {
                GestorDocument.getInstance().onClosingWindow();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
	}
	
    public static void main(String[] args) {
        launch(args);
    }
}
