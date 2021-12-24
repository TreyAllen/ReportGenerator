package reportbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ReportBuilder extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        stage.setTitle("Report Generator");
      
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/ams-logo-large-bolt_stretched-nobkgrnd.png")));
        
        stage.setScene(scene);
        stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
