package utils;

import org.apache.log4j.Logger;

import java.io.File;



    public class Pather {

        private static final Logger log = Logger.getLogger(Pather.class);
        public static final String curdir;
        public static final String curdirdata;
        //public final static String basenameArchive = "checkarchive";


        static {
            if ( DesktopApi.getOs ().isLinux () ) {
                curdir = System.getProperty ( "user.home" );


            } else {
                curdir = System.getProperty ( "user.dir" );

            }
            curdirdata = curdir + File.separator + "omskdata";
            File file = new File ( curdirdata );
            if ( !file.exists () ) {
                if(file.mkdir ()==false){
                    log.info("not mkdir "+curdirdata);
                }
            }
        }


        public static final String uuidFile=curdir + File.separator + ".uuidomskvideo.txt";
        public static final String settingsFolder = curdir + File.separator + "settingsvideo";
        public static final String base_sqlite = settingsFolder + File.separator + "omsk_22.sqlite";
        public static final String fileShipReserved = settingsFolder + File.separator + "FileshipReservedVideo.txt";
        public static final String pointid = curdirdata + File.separator + "pointidvideo.txt";
        public static final String patchprofile = curdirdata + File.separator + "profilevodeo.txt";
        public static final String patchUrlFile = curdir + File.separator + ".urlvideo.txt";





}
