package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ShowApplication extends Application {
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/main.fxml"));
        final Parent root = loader.load();
        final MainView controller = loader.getController();
        controller.setStageAndSetupListeners(stage); // or what you want to do
        stage.setScene(new Scene(root, 600, 300));
        stage.setTitle("Smart Garden Service");
        stage.show();
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.getIcons()
                .add(new Image(Objects.requireNonNull(this.getClass()
                        .getResourceAsStream("/icon.png"))));
    }
}
