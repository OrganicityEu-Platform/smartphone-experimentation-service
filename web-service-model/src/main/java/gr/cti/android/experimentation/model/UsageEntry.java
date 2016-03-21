package gr.cti.android.experimentation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by amaxilatis on 21/3/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageEntry implements Comparable<UsageEntry>, Serializable {
    private String date;
    private long time;

    public UsageEntry() {
    }

    public UsageEntry(String date, long time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int compareTo(UsageEntry o) {
        return o.getDate().compareTo(this.getDate());
    }
}
