package tesis.ejemplo.weather.model;

/**
 * Created by Alberto on 6/26/2017.
 */

public class coord {

    private double lat;
    private double lon;

    public coord(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
