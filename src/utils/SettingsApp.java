package utils;

import org.apache.log4j.Logger;
import sample.Main;
import sample.table.ColumnUserObject;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class SettingsApp {
    private transient static final Logger log = Logger.getLogger(SettingsApp.class);
    private static int pointid;
    public static synchronized int getPointId() {
        if (pointid == -1) {
            String dd = UtilsOmsk.readFile(Pather.pointid);
            if (dd == null) {
                pointid = 0;
            }
            try {
                if(dd!=null){
                    dd = dd.replace("\n", "");
                    pointid = Integer.parseInt(dd);
                }

            } catch (Exception ex) {
                log.error(ex);
                pointid = 0;
            }
        }
        return pointid;

    }

    public static  void setPointId(String id) throws Exception {
        try{
            pointid=Integer.parseInt(id);
            UtilsOmsk.rewriteFile(Pather.pointid,id);
        }catch (Exception es){
            log.error(es);
            throw es;
        }


    }

    private static transient String _version="";
    public static synchronized String getVersion()  {
        if(_version.length()==0){
            try {
                _version=getStringVersion();
            } catch (IOException e) {
                log.error(e);
            }
        }
        return String.valueOf(_version);
    }

    public static String getStringVersion() throws IOException {
//        String classPath = Main.class.getResource(Main.class.getSimpleName() + ".class").toString();
//        String libPath = classPath.substring(0, classPath.lastIndexOf("sample/"));
//        String filePath = libPath + "META-INF/MANIFEST.MF";
//        System.out.println("File:  " + filePath);
//        Manifest manifest = null;
//        manifest = new Manifest(new URL(filePath).openStream());
//        Attributes attr = manifest.getMainAttributes();
        return "0.0.1";//attr.getValue("Implementation-Version");
    }

    private static int profile;
    public static synchronized String getUrl() {

        try {

            List<String> strings=  Files.readAllLines(Paths.get(Pather.patchUrlFile));
            int p=getProfile();
            return strings.get(p).trim ();
        }catch (Exception ex){
            log.error(ex);
            return null;
        }
    }

    public static synchronized int getProfile() {
        if (profile == -1) {
            String dd = UtilsOmsk.readFile(Pather.patchprofile);
            if (dd == null) {
                profile = 0;
            }
            try {
                if(dd!=null){
                    dd = dd.replace("\n", "");
                    profile = Integer.parseInt(dd);
                }

            } catch (Exception ex) {
                log.error(ex);
                profile = 0;
            }
        }
        return profile;
    }

    private static SettingsApp settingsApp;
    public static SettingsApp getInstance() {
        return new SettingsApp();
    }

    //словарь ширины колонак таблицадмина
    public final Map<String, List<ColumnUserObject>> map = new HashMap<>();


    public List<ColumnUserObject> getColumnUserObjects(String tableName) {
        if (map.containsKey(tableName) == false) {
            List<ColumnUserObject> objectList = new ArrayList<>();
            map.put(tableName, objectList);
        }
        return map.get(tableName);
    }

    private static String uuid;
    public static synchronized String getUuid() {
        if (uuid == null) {
            uuid = UtilsOmsk.readFile(Pather.uuidFile);
        }
        return uuid;
    }
}
