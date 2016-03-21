package gr.cti.android.experimentation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by amaxilatis on 21/3/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadableResult implements Comparable<DownloadableResult>, Serializable {
    private long date;
    private double longitude;
    private double latitude;
    private Map<String, Object> results;

    public DownloadableResult() {
    }

    public DownloadableResult(long date, double longitude, double latitude, Map<String, Object> results) {
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.results = results;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int compareTo(DownloadableResult o) {
        return (int) (o.getDate() - date);
    }
}
