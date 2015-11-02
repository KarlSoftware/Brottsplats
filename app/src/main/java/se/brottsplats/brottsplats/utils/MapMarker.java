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
 *
 * @author Jimmy Maksymiw
 */
public class MapMarker implements ClusterItem, Parcelable {
    private final LatLng mPosition;
    private String title = "Title";
    private String description = "Description";

    /**
     * Konstruktor som tar emot information om en markör.
     *
     * @param latLng      Koordinater för markören.
     * @param title       Rubrik som ska användas i inforutan.
     * @param description Text om vad som har hänt, beskrivning som ska användas i inforutan.
     */
    public MapMarker(LatLng latLng, String title, String description) {
        this.mPosition = latLng;
        this.title = title;
        this.description = description;
    }

    /**
     * Metod som returnerar koordinater för markören.
     *
     * @return Koordinater för markören.
     */
    public LatLng getPosition() {
        return mPosition;
    }

    /**
     * Metod som returnerar rubriken för inforutan.
     *
     * @return rubriken för inforutan.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Metod som returnerar en text om vad som har hänt (används i inforutan).
     * @return Den beskrivande text som ska visas i inforutan.
     */
    public String getDescription() {
        return description;
    }

    // Parcelable används för att kunna spara information om användaren tex vänder på skärmen så att aktiviteten startar om.

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
