package se.brottsplats.brottsplats;

import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Klient-klass som hanterar anslutningar och anrop till server.
 *
 * @author Jimmy Maksymiw
 */
public class DownloadFileTask extends AsyncTask<String, Integer, JSONObject> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... urls) {
        JSONObject jsonObject = new JSONObject();
        for (String url : urls) {

            InputStream is = null;
            String json = "";

            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
                jsonObject = new JSONObject(json);
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            if (isCancelled()) break;
        }
        return jsonObject;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
//        System.out.println("\n\n\nJSON-Array nerladdad. KLART!!!");
        super.onPostExecute(jsonObject);
    }
}
