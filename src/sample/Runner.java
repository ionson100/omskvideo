package sample;

import model.MPlayList;
import orm.Configure;
import updateapp.Downloader;
import utils.Pather;
import utils.SettingsApp;
import utils.UtilsOmsk;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Runner {


    public void run() throws IOException {

        List<MPlayList> playLists= Configure.getSession().getList(MPlayList.class,null);

        StringBuilder  builder=new StringBuilder();

        for (MPlayList file : playLists) {
            builder.append(file.path).append(System.lineSeparator());
        }


        int i=builder.toString().hashCode();
        File file=new File(Pather.playlistdir);
        if(file.exists()){
            File[] files=file.listFiles();
            if(files!=null&&files.length>0){
                String nane=files[0].getName();
                if(nane.equals(i+".txt")==false){
                    for (File file1 : files) {
                        file1.delete();
                    }
                    UtilsOmsk.writeToFile(Pather.playlistdir+File.separator+""+i+".txt",builder.toString());
                }
            }else {
                UtilsOmsk.writeToFile(Pather.playlistdir+File.separator+""+i+".txt",builder.toString());
            }
        }


    }

}
