package updateapp;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import javafx.application.Platform;
import org.apache.log4j.Logger;
import utils.DesktopApi;
import utils.Pather;
import utils.SettingsApp;
import utils.UtilsOmsk;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
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


    }

    public void stop() {
        //timer.cancel();
        service.shutdown();
        executor.shutdown();
    }

    public synchronized void activate() {














        InputStreamReader ee = null;
        BufferedReader reader = null;
        HttpsURLConnection conn = null;

        try {


            String idpoint = String.valueOf(SettingsApp.getPointId());
            String version = SettingsApp.getVersion();
            URL url = new URL("https://" + SettingsApp.getUrl() + "/pos_info?point=" + idpoint + "&ver=" + version );
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

            BodyFiles bodyFiles = UtilsOmsk.getGson().fromJson(ssres, BodyFiles.class);

            for (String file : bodyFiles.files) {
                String s = file.replace("https://", "");
                s = s.substring(s.indexOf("/"));
                if (DesktopApi.getOs() == DesktopApi.EnumOS.linux) {
                    s = Pather.curdir + File.separator + s;
                } else {
                    s = Pather.curdir + s;
                }
                if (service.isShutdown() == false) {
                    service.submit(new Downloader().setUrl(file).setPath(s));//.execute ();
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