package updateapp;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import javafx.application.Platform;
import model.MContent;
import model.MPlayList;
import org.apache.log4j.Logger;
import orm.Configure;
import sample.Controller;
import utils.DesktopApi;
import utils.Pather;
import utils.SettingsApp;
import utils.UtilsOmsk;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TimerAppUpdate {


    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static final Logger log = Logger.getLogger(TimerAppUpdate.class);
    //Timer timer;
    private static final ExecutorService service = Executors.newFixedThreadPool(10);


    public void run(int timerSec) {

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

        InputStreamReader ee = null;
        BufferedReader reader = null;
        HttpsURLConnection conn = null;

        try {


            String uuid=SettingsApp.getUuid().replace("-","");
            String idpoint ="100124";// String.valueOf(SettingsApp.getPointId());
            String version = "001";//SettingsApp.getVersion();
            String urls= SettingsApp.getUrl();
            URL url = new URL(String.format("https://%s/ad_client_playlist?point=%s&device=%s",//
                    urls,
                    idpoint,
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

            SettingsApp.setPointId(bodyFiles.point_id);


            if(bodyFiles.playlist.size()>0){
                List<MPlayList> playLists=Configure.getSession().getList(MPlayList.class,null);
                if(playLists.size()!=bodyFiles.playlist.size()){
                    Configure.getSession().deleteTable(MPlayList.TABLE_NAME);
                    Configure.bulk(MPlayList.class,bodyFiles.playlist);
                }else {
                    for (int i = 0; i < playLists.size(); i++) {
                        if(playLists.get(i).path.equals(bodyFiles.playlist.get(i).path)==false){
                            Configure.getSession().deleteTable(MPlayList.TABLE_NAME);
                            Configure.bulk(MPlayList.class,bodyFiles.playlist);
                            break;
                        }
                    }
                }

            }




            for (MPlayList file : bodyFiles.playlist) {
                String urld = "https://"+SettingsApp.getUrl()+"/"+file.path;
                String s=Pather.curdir + file.path;
                if (service.isShutdown() == false) {
                    service.submit(new Downloader().setUrl(urld).setPath(s));//.execute ();
                }
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

        }
    }



private class RemindTask extends TimerTask {
        public void run() {
            activate();
        }
}


}