package flowershop.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    private SceneManager() {
    }

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath, String title) {
        try {
            boolean wasMaximized = primaryStage.isMaximized();
            boolean wasFullScreen = primaryStage.isFullScreen();
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

            if (wasMaximized) {
                primaryStage.setMaximized(true);
            } else if (wasFullScreen) {
                primaryStage.setFullScreen(true);
            } else {
                primaryStage.setWidth(width);
                primaryStage.setHeight(height);
                primaryStage.centerOnScreen();
            }

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không load được FXML: " + fxmlPath, e);
        }
    }
}