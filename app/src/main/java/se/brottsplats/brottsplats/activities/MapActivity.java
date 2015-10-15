/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.brottsplats.brottsplats.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;

import se.brottsplats.brottsplats.DownloadFileTask;
import se.brottsplats.brottsplats.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import se.brottsplats.brottsplats.utils.County;
import se.brottsplats.brottsplats.utils.MapMarker;
import se.brottsplats.brottsplats.utils.Values;

import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jimmy Maksymiw
 */
public class MapActivity extends FragmentActivity {
    private ClusterManager<MapMarker> mClusterManager;
    private ArrayList<MapMarker> mapMarkers;

    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mMenuList;
    private ListView mAreaList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String header = "Brottsplats - Hela Sverige";
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private HashMap<String, County> counties;
    private String ipAddress = "http://192.168.1.10:4567";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            setTitle(header);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuList = (ListView) findViewById(R.id.navdrawer);
        mAreaList = (ListView) findViewById(R.id.navdrawer);

        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(header);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(header);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setNavMenu();
        readCountyBoundsFromFile();
//        printMap(counties);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (cameraPosition != null) {

            getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            cameraPosition = null;
        } else {

            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(62.99639794287849, 17.56499793380499), 5));
        }

        if (mapMarkers != null) {
            mClusterManager.addItems(mapMarkers);
        } else {
            mapMarkers = TestItems();
            mClusterManager.addItems(mapMarkers);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        cameraPosition = mMap.getCameraPosition();
//        mMap = null;
    }

    /**
     * Metod för att spara data när denna Activity laddas om, som exempelvis vid skärmrotationer.
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("header", actionBar.getTitle().toString());

        savedInstanceState.putDouble("latitud", getMap().getCameraPosition().target.latitude);
        savedInstanceState.putDouble("longitude", getMap().getCameraPosition().target.longitude);
        savedInstanceState.putFloat("zoom", getMap().getCameraPosition().zoom);
        savedInstanceState.putFloat("tilt", getMap().getCameraPosition().tilt);
        savedInstanceState.putFloat("bearing", getMap().getCameraPosition().bearing);

        savedInstanceState.putParcelableArrayList("mapmarkers", mapMarkers);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Metod för att hämta den sparade datan när denna Activity precis startats om.
     *
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        header = savedInstanceState.getString("header");
        actionBar.setTitle(header);
        LatLng latLng = new LatLng(savedInstanceState.getDouble("latitud"), savedInstanceState.getDouble("longitude"));
        cameraPosition = new CameraPosition(latLng, savedInstanceState.getFloat("zoom"), savedInstanceState.getFloat("tilt"), savedInstanceState.getFloat("bearing"));

        mapMarkers = savedInstanceState.getParcelableArrayList("mapmarkers");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mMenuList)) {
                mDrawerLayout.closeDrawer(mMenuList);
            } else {
                mDrawerLayout.openDrawer(mMenuList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {
            mClusterManager = new ClusterManager<MapMarker>(this, getMap());
            mClusterManager.setRenderer(new ItemRenderer());
            getMap().setOnCameraChangeListener(mClusterManager);
            getMap().setOnCameraChangeListener(mClusterManager);
            getMap().setOnMarkerClickListener(mClusterManager);
            getMap().setOnInfoWindowClickListener(mClusterManager);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setRotateGesturesEnabled(false);
            getMap().getUiSettings().setZoomControlsEnabled(true);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                //todo inte visa någon inforuta när man trycker på ett kluster
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getApplicationContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getApplicationContext());
                    snippet.setGravity(Gravity.CENTER);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }
    }

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }

    //TODO Göra på något bra sätt så man slipper switch???
    public void setNavMenu() {

        final ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Values.MENU_VALUES);
        mMenuList.setAdapter(menuAdapter);
        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), menuAdapter.getItem(position), Toast.LENGTH_SHORT).show();


                switch (position) {
                    case 0:
                        County county = counties.get("Sverige");
                        header = "Brottsplats - " + county.getName();
                        LatLngBounds bounds = new LatLngBounds(county.getSouthwest(), county.getNortheast());

                        getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case 1:
                        setNavArea();
                        break;
                    case 2:
                        //Todo, activity startas om...
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        share.putExtra(Intent.EXTRA_TEXT,
                                "GitHub Page :  https://github.com/JimmyMaksymiw/Brottsplats\n" + getPackageName());
                        startActivity(Intent.createChooser(share, getString(R.string.app_name)));
                        break;
                    case 3:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        //TODO visa aboutFragment
                        break;
                }
            }
        });
    }

    public void setNavArea() {

        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Values.MENU_VALUES_AREAS);
        mAreaList.setAdapter(areaAdapter);
        mAreaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    County county = counties.get(areaAdapter.getItem(position));
                    header = "Brottsplats - " + county.getName();
                    LatLngBounds bounds = new LatLngBounds(county.getSouthwest(), county.getNortheast());

//                    AsyncTask<String, Integer, JSONArray> d = new DownloadFileTask().execute(ipAddress + county.getLink());

//                    try {
//                        JSONArray j = d.get();
//                        for (int i = 0; i < j.length(); i++) {
//
//                            JSONObject obj = (JSONObject) j.get(i);
//                            System.out.println(obj.getString("title"));
//                        }
//                    } catch (InterruptedException | JSONException | ExecutionException e) {
//                        e.printStackTrace();
//                    }

                    getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                    Toast.makeText(getApplicationContext(), county.getName() + "\n" + county.getLink(), Toast.LENGTH_SHORT).show();

                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setNavMenu();
                    Toast.makeText(getApplicationContext(), areaAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void printMap(HashMap<String, County> map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            County county = (County) pair.getValue();
            System.out.println(pair.getKey() + " = " + county.getName() + " nw: " + county.getNortheast().toString() + " sw: " + county.getSouthwest().toString());
        }

    }

    private void readCountyBoundsFromFile() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.counties_geocode_bounds);
            HashMap<String, County> items = new HashMap<>();
            String json = new Scanner(inputStream).useDelimiter("\\A").next();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String county = object.getString("county");
                String link = object.getString("link");
                JSONObject tmp = object.getJSONObject("southwest");
                LatLng southwest = new LatLng(tmp.getDouble("lat"), tmp.getDouble("lng"));
                object = object.getJSONObject("northeast");
                LatLng northeast = new LatLng(object.getDouble("lat"), object.getDouble("lng"));
                items.put(county, new County(county, link, southwest, northeast));
            }
            this.counties = items;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * för att testa. lägger till nya Item-objekt(som representerar markörer) i en arraylist.
     */
    private ArrayList<MapMarker> TestItems() {
        ArrayList<MapMarker> mapMarkers = new ArrayList<MapMarker>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.test_skane);
            String json = new Scanner(inputStream).useDelimiter("\\A").next();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);


                JSONObject jsonLocation = object.getJSONObject("Location");
                LatLng position = new LatLng(jsonLocation.getDouble("lat"), jsonLocation.getDouble("lng"));


                String title = object.getString("title");
                String body = object.getString("description");

                mapMarkers.add(new MapMarker(position, title, body));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapMarkers;
    }

    /**
     * för test random geopos i malmö.
     *
     * @return Latlng object med koordinater.
     */
    private LatLng position() {
        return new LatLng(random(55.607870, 55.549942), random(12.914387, 13.134042));
    }

    private double random(double min, double max) {
        return new Random().nextDouble() * (max - min) + min;
    }

    /**
     * Privat klass för att ändra markören på kartan, dvs lägga till en inforuta.
     */
    private class ItemRenderer extends DefaultClusterRenderer<MapMarker> {
        public ItemRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MapMarker mapMarker, MarkerOptions markerOptions) {
            markerOptions.title(mapMarker.getTitle());
            markerOptions.snippet(mapMarker.getDescription());
        }
    }
}