package flowershop;

import flowershop.services.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.switchScene("/fxml/Login.fxml", "Flower Shop");

         stage.setMinWidth(1100);
         stage.setMinHeight(920);
         stage.setMaximized(true);
         stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}