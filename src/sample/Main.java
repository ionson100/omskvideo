package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Starter;

public class Main extends Application {


    static {

        new Starter().start();
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Omsk Video");
        primaryStage.setScene(new Scene(root, 1400, 800));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Controller.timerUpdateStop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
