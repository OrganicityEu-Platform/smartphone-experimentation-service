package gr.cti.android.experimentation.model;

import java.util.Map;
import java.util.Set;

/**
 * Created by amaxilatis on 12/3/2016.
 */
public class SmartphoneStatistics {
    private int id;
    private long readingsTotal;
    private long readingsToday;
    private int experimentsTotal;
    private int experimentsToday;
    private Map<Long, Long> last7Days;
    private String sensorRules;
    private Set<RankingEntry> rankings;
    private Set<Badge> badges;

    public SmartphoneStatistics() {
    }

    public SmartphoneStatistics(final int id) {
        this.id = id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setReadingsTotal(final long readingsTotal) {
        this.readingsTotal = readingsTotal;
    }

    public long getReadingsTotal() {
        return readingsTotal;
    }

    public void setReadingsToday(final long readingsToday) {
        this.readingsToday = readingsToday;
    }

    public long getReadingsToday() {
        return readingsToday;
    }

    public void setExperimentsTotal(final int experimentsTotal) {
        this.experimentsTotal = experimentsTotal;
    }

    public int getExperimentsTotal() {
        return experimentsTotal;
    }

    public void setExperimentsToday(final int experimentsToday) {
        this.experimentsToday = experimentsToday;
    }

    public int getExperimentsToday() {
        return experimentsToday;
    }

    public void setLast7Days(final Map<Long, Long> last7Days) {
        this.last7Days = last7Days;
    }

    public Map<Long, Long> getLast7Days() {
        return last7Days;
    }

    public void setSensorRules(final String sensorRules) {
        this.sensorRules = sensorRules;
    }

    public String getSensorRules() {
        return sensorRules;
    }

    public void setRankings(Set<RankingEntry> rankings) {
        this.rankings = rankings;
    }

    public Set<RankingEntry> getRankings() {
        return rankings;
    }

    public void setBadges(Set<Badge> badges) {
        this.badges = badges;
    }

    public Set<Badge> getBadges() {
        return badges;
    }
}
