package se.brottsplats.brottsplats.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Jimmy Maksymiw
 */
public class County implements Parcelable {
    private final String name;
    private final String link;
    private final LatLng southwest;
    private final LatLng northeast;

    public County(String name, String link, LatLng southwest, LatLng northeast) {
        this.name = name;
        this.link = link;
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public LatLng getSouthwest() {
        return southwest;
    }

    public LatLng getNortheast() {
        return northeast;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(link);
        dest.writeParcelable(southwest, flags);
        dest.writeParcelable(northeast, flags);
    }

    protected County(Parcel in) {
        name = in.readString();
        link = in.readString();
        southwest = in.readParcelable(LatLng.class.getClassLoader());
        northeast = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<County> CREATOR = new Creator<County>() {
        @Override
        public County createFromParcel(Parcel in) {
            return new County(in);
        }

        @Override
        public County[] newArray(int size) {
            return new County[size];
        }
    };
}
