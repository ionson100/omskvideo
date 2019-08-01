package utils;

import org.apache.log4j.Logger;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class SendError {

    private static final Logger log = Logger.getLogger(SendError.class);
    private String error;

    public void  send(String error){

        if(error==null) return;
        this.error = error;
        new Inneraction().execute(null);
    }

    class Inneraction extends AsyncTask<Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            String url = String.format("https://%s/ad_client_error",
                    SettingsApp.getUrl());

            log.info(url);

            InputStream stream = null;
            BufferedWriter httpRequestBodyWriter = null;
            OutputStream outputStreamToRequestBody = null;
            OutputStreamWriter dd = null;
            HttpsURLConnection connection = null;
            try {
                URL serverUrl = new URL(url);
                connection = (HttpsURLConnection) serverUrl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setReadTimeout(35000);
                connection.setConnectTimeout(35000);
                outputStreamToRequestBody = connection.getOutputStream();
                dd = new OutputStreamWriter(outputStreamToRequestBody);
                httpRequestBodyWriter = new BufferedWriter(dd);

                httpRequestBodyWriter.flush();

                stream = new ByteArrayInputStream(error.getBytes());
                int bytesRead;
                byte[] dataBuffer = new byte[1024];
                while ((bytesRead = stream.read(dataBuffer)) != -1) {
                    outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
                }
                outputStreamToRequestBody.flush();
                httpRequestBodyWriter.flush();
                outputStreamToRequestBody.close();
                httpRequestBodyWriter.close();
                connection.connect();
                int status = connection.getResponseCode();

                if (status == 200) {
                    log.info("Ошибка доставлене на сервер , ответ сервера:  200");
                }else {
                    String sd=new String(dataBuffer);
                    String s = String.format(" Отправка данных на сервер %d %s", status,sd);
                    throw new RuntimeException(s);
                }

            } catch (Exception ex) {


                log.info(ex);


            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    closerableAll(httpRequestBodyWriter, outputStreamToRequestBody, stream, dd);
                } catch (Exception ex) {
                    log.info(ex);
                }

            }


            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void params) {

        }

        @Override
        protected void onErrorInner(Throwable ex) {

        }
    }

    public static void closerableAll(Closeable... d) throws IOException {
        for (Closeable closeable : d) {
            if (closeable != null) {
                closeable.close();
            }
        }

    }
}
