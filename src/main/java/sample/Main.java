package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Application");
        primaryStage.setScene(new Scene(root, 525.0, 360.0));
        primaryStage.setResizable(false);
        final Controller controller = fxmlLoader.getController();
        primaryStage.setOnHidden(e -> controller.shutdown());
        primaryStage.show();

    }
}
