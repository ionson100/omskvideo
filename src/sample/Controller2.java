package sample;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
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
    public GridPane myPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //vbox_head.setStyle("-fx-background-image: url(images/house.png)");
        timerUpdate = new TimerAppUpdate();
        timerUpdate.run(300);
        controller2=this;
        ShowDownloadesFile();
        myPane.setStyle("-fx-background-color: black;-fx-background-image: url(/sample/images/logo.jpg);" +
                "-fx-background-repeat: stretch;" +
                "-fx-background-size: 100% 100%;"+
                "-fx-background-position: center center;");



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
