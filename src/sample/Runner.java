package sample;

import model.MPlayList;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;
import orm.Configure;
import updateapp.Downloader;
import utils.Pather;
import utils.SettingsApp;
import utils.UtilsOmsk;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Runner {

    private static final Logger log = Logger.getLogger(Runner.class);

    private static String RUNNER;
    private StringBuilder  builder=new StringBuilder();
    List<MPlayList> playLists= Configure.getSession().getList(MPlayList.class,null);
    public void run() {
        if(playLists.size()==0) return;

        for (MPlayList file : playLists) {
            builder.append(file.description).append(System.lineSeparator());
        }





        boolean isRun=true;
        for (MPlayList playList : playLists) {
            File file=new File(Pather.curdir+File.separator+playList.path);
            if(file.exists()==false){
                isRun=false;
                break;
            }
            if (file.length()!=playList.size){
                isRun=false;
            }
        }
        if(isRun==true){

            if(RUNNER==null){
                coreRun();
            }else {
                if(RUNNER.equals(builder.toString())==false){
                    coreRun();
                }
            }



        }


    }

    private void coreRun()  {


            new Thread(() -> {

                try{
                    CommandLine oCmdLine = new CommandLine("vlc");
                    //--reset-config --reset-plugins-cache
                    //oCmdLine.addArgument("--reset-config");
                    //oCmdLine.addArgument("--reset-plugins-cache");
                    //-- name
                    //oCmdLine.addArgument("-v");
                    //oCmdLine.addArgument("qt");

                    oCmdLine.addArgument("--loop ");
                    oCmdLine.addArgument("--fullscreen");
                    oCmdLine.addArgument("--video-on-top");
                    for (MPlayList playList : playLists) {
                        oCmdLine.addArgument(Pather.curdir+ File.separator+playList.path);
                    }


                    DefaultExecutor oDefaultExecutor = new DefaultExecutor();
                    oDefaultExecutor.setExitValue(0);


                    try{
                        new ExeScript().runScript("killall vlc");
                        log.info("killall vlc");
                    }catch (Exception ignored){

                    }



                    try {
                        log.info("Старт плеера: "+oCmdLine.toString());
                        RUNNER=builder.toString();
                        oDefaultExecutor.execute(oCmdLine);
                    }catch (Exception ex){

                    }

                    //new ExeScript().runScript("vlc --video-on-top and --fullscreen");



                }catch (Exception ex){

                }
            }).start();




    }


}

