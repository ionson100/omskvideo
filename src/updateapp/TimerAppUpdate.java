package updateapp;

import model.MContent;
import model.MPlayList;
import org.apache.log4j.Logger;
import orm.Configure;
import sample.Controller2;
import sample.ExeScript;
import sample.Runner;
import utils.Pather;
import utils.SettingsApp;
import utils.UtilsOmsk;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TimerAppUpdate {


    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private static final Logger log = Logger.getLogger(TimerAppUpdate.class);
    //Timer timer;
    private static final ExecutorService service = Executors.newFixedThreadPool(10);


    public void run(int timerSec) {

        timerSec=60*15;
        executor.scheduleWithFixedDelay(new RemindTask(), 5, timerSec, TimeUnit.SECONDS);
        log.info("Запус таймера обновления файлов: 5-"+timerSec+" second");


    }

    public void stop() {
        //timer.cancel();
        service.shutdown();
        executor.shutdown();
        log.info("Остановка таймера обновления файлов");
    }

    public synchronized void activate() {


        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int h=calendar.get(Calendar.HOUR_OF_DAY);
        int m=calendar.get(Calendar.MINUTE);
        if(h>=21&&m>0){
            log.error("Выключение программы по таймеру: "+new Date().toString());
            ExeScript testScript = new ExeScript();
            try {
                testScript.runScript("shutdown -h now");
            } catch (Exception e) {
                log.error(e);
            }
        }
        InputStreamReader ee = null;
        BufferedReader reader = null;
        HttpsURLConnection conn = null;

        try {


            String uuid=SettingsApp.getUuid().replace("-","");

            String version = SettingsApp.getVersion();
            String urls= SettingsApp.getUrl();
            URL url = new URL(String.format("https://%s/ad_client_playlist?device=%s",//
                    urls,

                    uuid));
            log.info(url);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setReadTimeout(UtilsOmsk.READ_CONNECT_TIMEOUT /*milliseconds*/);
            conn.setConnectTimeout(UtilsOmsk.READ_CONNECT_TIMEOUT /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            conn.connect();
            final int status = conn.getResponseCode();

            if (status != 200) {
                log.error("Запрос файлов ошибка : ответ " + String.valueOf(status) + " " + url.toString());
                return;
            }
            log.info("Запрос файлов: ответ " + String.valueOf(status) + " " + url.toString());

            int ch;

            StringBuilder sb = new StringBuilder();

            ee = new InputStreamReader(conn.getInputStream(), "UTF-8");
            reader = new BufferedReader(ee);
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            String ssres = sb.toString();
            if(ssres.trim().length()==0) return;

            MContent bodyFiles = UtilsOmsk.getGson().fromJson(ssres, MContent.class);




            boolean isNew=false;
            if(bodyFiles.playlist.size()>0){
                List<MPlayList> playLists=Configure.getSession().getList(MPlayList.class,null);
                if(playLists.size()!=bodyFiles.playlist.size()){
                    Configure.getSession().deleteTable(MPlayList.TABLE_NAME);
                    Configure.bulk(MPlayList.class,bodyFiles.playlist);
                    isNew=true;
                }else {
                    for (int i = 0; i < playLists.size(); i++) {
                        if(playLists.get(i).path.equals(bodyFiles.playlist.get(i).path)==false){
                            Configure.getSession().deleteTable(MPlayList.TABLE_NAME);
                            Configure.bulk(MPlayList.class,bodyFiles.playlist);
                            isNew=true;
                            break;
                        }
                    }
                }
            }





            for (MPlayList file : bodyFiles.playlist) {
                String urld = "https://"+SettingsApp.getUrl()+"/"+file.path;
                System.out.println(urld);
                String s=Pather.curdir + file.path;
                if (service.isShutdown() == false) {
                    service.submit(new Downloader().setUrl(urld).setPath(s));//.execute ();
                }
            }
            if(isNew==true){
                Controller2.ShowDownloadesFile();
            }

        } catch (Exception ex) {
            log.error(ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (ee != null) {
                    ee.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                log.error(ex);
            }




            new Runner().run();






        }
    }



private class RemindTask extends TimerTask {
        public void run() {
            activate();
        }
}


}