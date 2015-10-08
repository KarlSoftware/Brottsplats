package se.brottsplats.brottsplats.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import se.brottsplats.brottsplats.R;

/**
 * Klass för att starta appen.
 *
 * @author Jimmy Maksymiw
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, MapActivity.class));
    }
}