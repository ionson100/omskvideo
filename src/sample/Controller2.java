package sample;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.MContent;
import model.MPlayList;
import orm.Configure;
import updateapp.TimerAppUpdate;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller2 implements Initializable {

    public static Controller2 controller2;
    public static TimerAppUpdate timerUpdate;
    public VBox vbox_head;
    public Button bt_action;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timerUpdate = new TimerAppUpdate();
        timerUpdate.run(300);
        controller2=this;
        ShowDownloadesFile();
    }
    public static void timerUpdateStop() {
        timerUpdate.stop();
        try{
            new ExeScript().runScript("killall vlc");
        }catch (Exception ignored){

        }


    }
    public static void ShowDownloadesFile(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller2.vbox_head.getChildren().clear();
                List<MPlayList> playLists=Configure.getSession().getList(MPlayList.class,null);
                for (MPlayList playList : playLists) {
                    ItemFile itemFile=new ItemFile(playList);
                    controller2.vbox_head.getChildren().add(itemFile);
                }
            }
        });


    }

}
