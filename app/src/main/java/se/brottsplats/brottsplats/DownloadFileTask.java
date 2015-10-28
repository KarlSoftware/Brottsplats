package se.brottsplats.brottsplats;

import android.os.AsyncTask;


import org.json.JSONObject;

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
        JSONObject json = new JSONObject();
        for (String url : urls) {
            json = JSONReader.getJSONFromUrl(url);
            if (isCancelled()) break;
        }
        return json;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        System.out.println("\n\n\nJSON-Array nerladdad. KLART!!!");

        super.onPostExecute(jsonObject);
    }
}
