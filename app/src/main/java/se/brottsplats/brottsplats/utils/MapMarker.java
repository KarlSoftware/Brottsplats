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

package se.brottsplats.brottsplats.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Klass som representerar en markör som visar på kartan. Dena markör innehåller geokoder, title och beskrivning.
 * @author Jimmy Maksymiw
 */
public class MapMarker implements ClusterItem, Parcelable {
    private final LatLng mPosition;
    private String title = "Title";
    private String description = "Description";

    public MapMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MapMarker(LatLng latLng, String title, String description) {
        this.mPosition = latLng;
        this.title=title;
        this.description = description;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(mPosition, flags);
    }

    protected MapMarker(Parcel in) {
        mPosition = in.readParcelable(LatLng.class.getClassLoader());
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<MapMarker> CREATOR = new Creator<MapMarker>() {
        @Override
        public MapMarker createFromParcel(Parcel in) {
            return new MapMarker(in);
        }

        @Override
        public MapMarker[] newArray(int size) {
            return new MapMarker[size];
        }
    };
}
