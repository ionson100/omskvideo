package updateapp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;
import utils.Starter;
import utils.UtilsOmsk;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Downloader extends Task {


    public final static String HLAM = ".nocomplete";
    private static final Logger log = Logger.getLogger(Downloader.class);
    private static Set<String> dowMap = Collections.synchronizedSet(new HashSet<String>());
    private int code;
    private HttpURLConnection con = null;
    private String urlCore, path;


    private static void validateClientFile(String path, long httplast, String etag) throws IOException {


        File targetFile = new File(path + HLAM);
        if (targetFile.exists())
            return;
        // создаем новый файл для закачки
        File parent = targetFile.getParentFile();
        if (parent != null) {
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
        }
        // навешиваем атрибут создания файла по  времение с сервера
        targetFile.createNewFile();
        FileTime fileTime = FileTime.fromMillis(httplast);
        Files.setAttribute(targetFile.toPath(), "basic:creationTime", fileTime);
        Files.setAttribute(targetFile.toPath(), "user:tags", etag.getBytes());


    }

    // отключить проверку для ssl
    private static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {

        Starter.divableSslVrification();

    }

    @Override
    protected Object call() throws Exception {
        if (dowMap.contains(urlCore) == false) {
            dowMap.add(urlCore);
            doInBackground();
            dowMap.remove(urlCore);
        }

        return null;
    }

    protected void doInBackground() throws Exception {
        InputStream input = null;
        OutputStream output = null;
        try {

            int count;
            URL url = new URL(urlCore);
            if (urlCore.toLowerCase().startsWith("https:")) {
                con = (HttpsURLConnection) url.openConnection();
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.setInstanceFollowRedirects(false);
            con.setReadTimeout(UtilsOmsk.READ_CONNECT_TIMEOUT /*milliseconds*/);
            con.setConnectTimeout(UtilsOmsk.READ_CONNECT_TIMEOUT /* milliseconds */);
            con.setRequestMethod("GET");

            // проверка   закончена ли загрузка
            File f1 = new File(path + HLAM);
            if (f1.exists()) {
                byte[] o = (byte[]) Files.getAttribute(f1.toPath(), "user:tags");
                String attr = new String(o);
                con.setRequestProperty("Range", String.format("Bytes=%s - ", f1.length()));
                con.setRequestProperty("If-Range", attr);
            } else {
                File f2 = new File(path);
                if (f2.exists()) {
                    byte[] o = (byte[]) Files.getAttribute(f2.toPath(), "user:tags");
                    String attr = new String(o);
                    String res = String.format("W/%s", attr);
                    con.setRequestProperty("If-None-Match", res);
                }
            }


            con.connect();
            Map<String, List<String>> map = con.getHeaderFields();
            code = con.getResponseCode();// 206 add download
            if (code == 304 || code == 404||code == 403||code==500||code==205) return;
            if (code == 200) {// файл измененет или  не начал качаться на клиенте
                {
                    File f = new File(path);
                    if (f.exists()) {
                        if(f.delete()==false){
                            log.info("not deleted "+path);
                        }
                    }

                }
                {
                    File f = new File(path + HLAM);
                    if (f.exists()) {
                        if(f.delete()==false){
                            log.info("not deleted "+path+HLAM);
                        }
                    }

                }

                long httplast = con.getLastModified();
                String etag = con.getHeaderField("ETag");
                validateClientFile(path, httplast, etag);

            }


            //"5ae31600-cb59cf"
            int lenghtOfFile = con.getContentLength() + 100;
            input = con.getInputStream();
            output = new FileOutputStream(path + HLAM, true);
            byte datas[] = new byte[lenghtOfFile];
            while ((count = input.read(datas)) != -1) {
                output.write(datas, 0, count);
            }
            output.flush();


        } finally {
            if (con != null) {
                con.disconnect();
            }

            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            File f = new File(path + HLAM);
            if (!f.exists()) return;
            try{
                f.renameTo(new File(path));
            }catch (Exception e){
                log.error(e);
            }


            if (code == 304 || code == 404||code == 403||code==500||code==205) {
                log.error(String.format("Error download: %s code: %d", path, code));
                return;
            }

//
        }
    }


    public Downloader setUrl(String urlCore) {
        this.urlCore = urlCore.trim();
        return this;
    }

    public Downloader setPath(String path) {
        this.path = path.trim();
        return this;
    }
}

