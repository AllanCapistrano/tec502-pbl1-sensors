package tec502.pbl1.sensors;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Allan Capistrano
 */
public class Tec502Pbl1Sensors extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Emulador de Sensores");
        stage.setResizable(false);
        stage.show();
        
        stage.setScene(scene);
        stage.show();
        
        Image icon = new Image("/images/sensor-icon.png");

        stage.getIcons().add(icon);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
