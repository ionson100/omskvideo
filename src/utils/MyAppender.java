package utils;

import javafx.application.Platform;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public /*static*/ class MyAppender extends AppenderSkeleton {


    private static final Logger log = Logger.getLogger(MyAppender.class);

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        //2018-03-14 09:12:46 ERROR StateKassaMoney:60 - bitnic.kassa.BaseKassa$DriverException: [-3] Порт недоступен






        if (loggingEvent.getLevel() == Level.ERROR) {
            String msg = String.format("#  %s %s  %s  -  %s ",

                    getStringTimeForError(new Date(loggingEvent.timeStamp)),
                    loggingEvent.getLevel(),
                    loggingEvent.getLocationInformation().fullInfo,
                    loggingEvent.getMessage());

            Platform.runLater(() -> {



            });
        }



    }

    public static Object getStringTimeForError(Date date) {
        //2018-03-14 09:12:46
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);

    }
}

