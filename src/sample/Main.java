package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import utils.Starter;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class);


    static {

        new Starter().start();
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        primaryStage.setTitle("Omsk Video");
        primaryStage.setScene(new Scene(root, 1000, 400));
        primaryStage.setFullScreen(true);
        primaryStage.show();
        log.error("Старт программы");

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.error("Стоп программа");
        Controller2.timerUpdateStop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
