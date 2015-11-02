package se.brottsplats.brottsplats.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Klass som representerar de olika Länen i Sverige.
 *
 * @author Jimmy Maksymiw
 */
public class County implements Parcelable {
    private final String name;
    private final String link;
    private final LatLng southwest;
    private final LatLng northeast;

    /**
     * Konstruktor som tar emot information om länet.
     *
     * @param name      Namnet på länet
     * @param link      Länk (URI) som ska användas för att hämta händelser.
     * @param southwest Koordiater för den sydligaste och västligaste punkten i länet.
     * @param northeast Koordiater för den nordligaste och östligaste punkten i länet.
     */
    public County(String name, String link, LatLng southwest, LatLng northeast) {
        this.name = name;
        this.link = link;
        this.southwest = southwest;
        this.northeast = northeast;
    }

    /**
     * Metod som returnerar länets namn.
     *
     * @return länets namn.
     */
    public String getName() {
        return name;
    }

    /**
     * Metod som returnerar URI som används för att hämta händelser.
     *
     * @return URI för att hämta händelser.
     */
    public String getLink() {
        return link;
    }

    /**
     * Metod som returnerar koordiater för den sydligaste och västligaste punkten i länet.
     *
     * @return koordiater för den sydligaste och västligaste punkten i länet
     */
    public LatLng getSouthwest() {
        return southwest;
    }

    /**
     * Metod som returnerar koordiater för den nordligaste och östligaste punkten i länet.
     *
     * @return oordiater för den nordligaste och östligaste punkten i länet.
     */
    public LatLng getNortheast() {
        return northeast;
    }

    // Parcelable används för att kunna spara information om användaren tex vänder på skärmen så att aktiviteten startar om.

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
