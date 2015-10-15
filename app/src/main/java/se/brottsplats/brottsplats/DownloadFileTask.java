package se.brottsplats.brottsplats;

import android.os.AsyncTask;

import org.json.JSONArray;

/**
 * Klient-klass som hanterar anslutningar och anrop till server.
 *
 * @author Jimmy Maksymiw
 */
public class DownloadFileTask extends AsyncTask<String, Integer, JSONArray> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONArray doInBackground(String... urls) {
        JSONArray json = new JSONArray();
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
    protected void onPostExecute(JSONArray jsonArray) {
        System.out.println("\n\n\nJSON-Array nerladdad. KLART!!!");

        super.onPostExecute(jsonArray);
    }
}
