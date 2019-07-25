package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import sample.dialog.MyDialog;
import sample.workers.ShowmenListVideo;
import sample.workers.ShowmenLogError;
import updateapp.TimerAppUpdate;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public static TimerAppUpdate timerUpdate;
    public Button bt_files;
    public Button bt_list_video;
    public Button bt_file_error;
    public Button bt_exit;
    public Pane panel_host;
    public TableView tableview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        timerUpdate = new TimerAppUpdate();
        timerUpdate.run(300);
        bt_exit.setOnAction(event -> Platform.exit());
        //bt_files.setOnAction(event -> new ShowmenFiles().show(tableview));
        bt_list_video.setOnAction(event -> new ShowmenListVideo().show(tableview));
        bt_file_error.setOnAction(event -> new ShowmenLogError().show(tableview));
        bt_files.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MyDialog.showError("asdasdads");
            }
        });
    }
    public static void timerUpdateStop() {
        timerUpdate.stop();
    }


}
