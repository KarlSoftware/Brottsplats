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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;

import se.brottsplats.brottsplats.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import se.brottsplats.brottsplats.utils.GeoCode;
import se.brottsplats.brottsplats.utils.MapMarker;
import se.brottsplats.brottsplats.utils.Values;

import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

/**
 *
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (cameraPosition != null) {

            getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            cameraPosition = null;
        } else {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(GeoCode.SVERIGE, GeoCode.ZOOM_SVERIGE));
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
                        header = "Brottsplats - Hela Sverige";
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(GeoCode.SVERIGE, GeoCode.ZOOM_SVERIGE));

                        break;
                    case 1:
                        setNavArea();
                        break;
                    case 2:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        share.putExtra(Intent.EXTRA_TEXT,
                                "GitHub Page :  https://github.com/JimmyMaksymiw\n" + getPackageName());
                        startActivity(Intent.createChooser(share,  getString(R.string.app_name)));
                        break;
                    case 3:
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        //TODO visa aboutFragment
                        break;
                }
            }
        });
    }

    //TODO Göra på något bra sätt så man slipper switch???
    public void setNavArea() {

        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Values.MENU_VALUES_AREAS);
        mAreaList.setAdapter(areaAdapter);
        mAreaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    header = "Brottsplats - " + areaAdapter.getItem(position);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }

                Toast.makeText(getApplicationContext(), areaAdapter.getItem(position), Toast.LENGTH_SHORT).show();

                switch (areaAdapter.getItem(position)) {
                    case "< Tillbaka":
                        setNavMenu();
                        break;
                    case "Skåne":
                        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(GeoCode.SKANE, GeoCode.ZOOM_SKANE));
                        break;

                    default:
                }
            }
        });
    }

    /**
     * för att testa. lägger till nya Item-objekt(som representerar markörer) i en arraylist.
     */
    private ArrayList<MapMarker> TestItems() {
        ArrayList<MapMarker> mapMarkers = new ArrayList<MapMarker>();
        mapMarkers.add(new MapMarker(position(), "vart är vi", "här häder saker"));
        mapMarkers.add(new MapMarker(position(), "HUH??", "waddap"));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "vart är vi", "här häder saker"));
        mapMarkers.add(new MapMarker(position(), "HUH??", "waddap"));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "vart är vi", "här häder saker"));
        mapMarkers.add(new MapMarker(position(), "HUH??", "waddap"));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "vart är vi", "här häder saker"));
        mapMarkers.add(new MapMarker(position(), "HUH??", "waddap"));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));
        mapMarkers.add(new MapMarker(position(), "naain", "nej nej nej..."));

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