package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import sample.workers.ShowmenLogError;
import utils.Starter;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class);


    static {

        new Starter().start();
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Omsk Video");
        primaryStage.setScene(new Scene(root, 1400, 800));
        primaryStage.setMaximized(true);
        primaryStage.show();
        log.info("Старт программы");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.info("Старт программы");
        Controller.timerUpdateStop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
