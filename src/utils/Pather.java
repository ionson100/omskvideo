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



        public static final String settingsFolder = curdir + File.separator + "settingsvideo";
        public static final String base_sqlite = settingsFolder + File.separator + "omsk_22.sqlite";
        public static final String fileShipReserved = settingsFolder + File.separator + "FileshipReservedVideo.txt";
        public static final String pointid = settingsFolder + File.separator + "pointidvideo.txt";
        public static final String patchprofile = settingsFolder + File.separator + "profilevodeo.txt";
        public static final String patchUrlFile = settingsFolder + File.separator + ".urlvideo_2.txt";
        public static final String directoryBuilder2 = settingsFolder + File.separator+"assa2";
        public static final String playlistdir=settingsFolder+File.separator+"list";
        public static final String uuidFile=settingsFolder + File.separator + ".uuidomskvideo.txt";




}
