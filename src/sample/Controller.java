package sample;

import javafx.fxml.Initializable;
import updateapp.TimerAppUpdate;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public static TimerAppUpdate timerUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        timerUpdate = new TimerAppUpdate();
        timerUpdate.run(300);
    }
    public static void timerUpdateStop() {
        timerUpdate.stop();
    }


}
