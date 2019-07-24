package utils;

import com.google.gson.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

public class UtilsOmsk {

    private transient static final Logger log = Logger.getLogger(UtilsOmsk.class);

    public static final int READ_CONNECT_TIMEOUT =25000;

    public static Gson getGson() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {

            String ss = json.getAsJsonPrimitive().getAsString();
            if (ss.equals("null")) {
                return null;
            }else {
                long dd = json.getAsJsonPrimitive().getAsLong();
                return new Date(dd);
            }
        });

        builder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {

                if (date == null) {
                    return new JsonPrimitive("null");
                }
                int dd = (int) (date.getTime() / 1000);
                return new JsonPrimitive(dd);
            }
        });

        return builder.serializeNulls().create();
    }

    public static String readFile(String filepatch) {

        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filepatch), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
            return contentBuilder.toString();
        }
        catch (IOException e)
        {
            log.error ( e );
            return null;
        }

    }

    public static void rewriteFile(String fileName, String s) throws Exception {
        File myFoo = new File(fileName);
        try(FileOutputStream fooStream = new FileOutputStream(myFoo, false)){ // true to append
            byte[] myBytes = s.getBytes();
            fooStream.write(myBytes);
        }
    }

    public static void writeToFile(String patch, String content) throws IOException {


        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(patch);
            bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush ();
            fw.flush ();

        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                log.error ( ex );
            }

        }
    }
}
