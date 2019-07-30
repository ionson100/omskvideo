package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.MContent;
import model.MPlayList;
import org.apache.log4j.Logger;
import updateapp.Downloader;
import utils.Pather;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.net.URL;
import java.util.ResourceBundle;

public class ItemFile extends GridPane implements Initializable {

    private static final Logger log = Logger.getLogger(ItemFile.class);
    private MPlayList mContent;
    public Label label1,label2,label3;


    public ItemFile(MPlayList mContent) {
        this.mContent = mContent;

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("item_file.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            log.error(exception);
            throw new RuntimeException(exception);
        }

    }

    Timeline timeline;



    @Override
    public void initialize(URL location, ResourceBundle resources) {



       label3.setText(mContent.path+"  "+mContent.description);
       label1.setText(String.valueOf(mContent.size));

       timeline = new Timeline (
                new KeyFrame(
                        Duration.millis(1000 * 3), //1000 мс * 60 сек = 1 мин
                        ae -> {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    printLoad();
                                    System.out.println("reun");

                                }
                            });
                        }
                )
        );


        timeline.setCycleCount(10000);
        timeline.play();
        printLoad();
    }

    void printLoad(){

        String str= Pather.curdir+ File.separator+mContent.path;
        File file=new File(str);
        if(file.exists()){
            label2.setText(String.valueOf(file.length()));
            if(file.length()==mContent.size){
                timeline.stop();
            }
        }else {
            str=str+ Downloader.HLAM;
            File file1=new File(str);
            if(file1.exists()){
                label2.setText(String.valueOf(file1.length()));
                if(file.length()==mContent.size){
                    timeline.stop();
                }
            }
        }
    }

}
