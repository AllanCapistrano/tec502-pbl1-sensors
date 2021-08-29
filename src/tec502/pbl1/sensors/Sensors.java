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
public class Sensors extends Application {
    
    private static Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/SensorsView.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Emulador de Sensores");
        stage.setResizable(false);
        stage.show();
        setStage(stage);
        
        Image icon = new Image("/images/sensor-icon.png");

        stage.getIcons().add(icon);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Sensors.stage = stage;
    }
    
}
