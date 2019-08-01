package utils;

import javafx.application.Platform;
import org.apache.log4j.Logger;
import orm.Configure;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class Starter {
    private StringBuilder sb = new StringBuilder();
    private static final Logger log = Logger.getLogger(Starter.class);

    public Starter() {
        System.out.println(System.getProperty("java.library.path"));
    }

    public void start() {
        disableSslVerification();

        {
            String path = Pather.settingsFolder;
            File f = new File(path);
            if (f.exists()) {
            } else {
                if (f.mkdir()) {
                    sb.append(path + " settings - create").append(System.lineSeparator());
                } else {
                    sb.append(path + " Не могу созать").append(System.lineSeparator());
                }
            }
        }

        {
            String path = Pather.playlistdir;
            File f = new File(path);
            if (f.exists()) {
            } else {
                if (f.mkdir()) {
                    sb.append(path + " list - create").append(System.lineSeparator());
                } else {
                    sb.append(path + " Не могу созать").append(System.lineSeparator());
                }
            }
        }


        boolean isrunung = isFileshipAlreadyRunning();// если false то запущена вторая программа и мы закрываем приложение
        if (isrunung == false) {
            Platform.exit();
        }

        {
            File f = new File(Pather.pointid);
            if (f.exists() == false) {
                try {
                    if (f.createNewFile()) {
                        UtilsOmsk.writeToFile(Pather.pointid, "0");
                    }

                } catch (IOException e) {
                    sb.append(e.getMessage()).append(System.lineSeparator());
                }
            }
        }


        {
            File file = new File(Pather.patchUrlFile);
            if (file.exists() == false) {
                try {
                    file.createNewFile();
                    String urls = "bsr000.net\nbsr000.net";
                    UtilsOmsk.writeToFile(Pather.patchUrlFile, urls);

                } catch (Exception e) {
                    sb.append(e.getMessage()).append(System.lineSeparator());
                }
            }
        }

        {
            File file = new File(Pather.uuidFile);
            if (file.exists() == false) {
                try {
                    file.createNewFile();
                    UtilsOmsk.writeToFile(Pather.uuidFile, UUID.randomUUID().toString());
                } catch (Exception e) {
                    sb.append(e.getMessage()).append(System.lineSeparator());
                }
            }
        }

        if (sb.length() > 0) {
            log.error(sb.toString());
        }

        new Configure(Pather.base_sqlite);

         // удаление плайлистов, плеер не запущен
        File file=new File(Pather.playlistdir);
        if(file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    file1.delete();
                }
            }
        }
    }

    private static boolean isFileshipAlreadyRunning() {

        try {
            File file = new File(Pather.fileShipReserved);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            if (file.delete() == false) {
                                log.info("not delete file " + file.getPath());
                            }
                        } catch (Exception e) {

                            log.error("Unable to remove lock file: " + Pather.fileShipReserved);
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {

            log.error("Запуск второго экземпляра программы  lock file: " + Pather.fileShipReserved);
        }
        return false;
    }

    private static void disableSslVerification() {
        try {
            divableSslVrification();
        } catch (Exception e) {
            log.error(e);

        }
    }

    public static void divableSslVrification() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
