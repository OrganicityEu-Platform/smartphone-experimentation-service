package gr.cti.android.experimentation.model;

/*-
 * #%L
 * Smartphone Experimentation Model
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2016 CTI - Computer Technology Institute and Press "Diophantus"
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartphoneStatisticsDTO implements Serializable {
    private int id;
    private long readings;
    private long experimentReadings;
    private int experiments;
    private Map<Long, Long> last7Days;
    private String sensorRules;
    private Set<RankingEntry> rankings;
    private Set<RankingEntry> experimentRankings;
    private Set<BadgeDTO> badges;
    private Set<BadgeDTO> experimentBadges;
    private Set<UsageEntry> usage;
    private Set<UsageEntry> experimentUsage;

    public SmartphoneStatisticsDTO() {
    }

    public SmartphoneStatisticsDTO(final int id) {
        this.id = id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setReadings(final long readings) {
        this.readings = readings;
    }

    public long getReadings() {
        return readings;
    }

    public void setExperimentReadings(final long experimentReadings) {
        this.experimentReadings = experimentReadings;
    }

    public long getExperimentReadings() {
        return experimentReadings;
    }

    public void setExperiments(final int experiments) {
        this.experiments = experiments;
    }

    public int getExperiments() {
        return experiments;
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

    public void setBadges(Set<BadgeDTO> badges) {
        this.badges = badges;
    }

    public Set<BadgeDTO> getBadges() {
        return badges;
    }

    public void setExperimentRankings(Set<RankingEntry> experimentRankings) {
        this.experimentRankings = experimentRankings;
    }

    public Set<RankingEntry> getExperimentRankings() {
        return experimentRankings;
    }

    public void setExperimentBadges(Set<BadgeDTO> experimentBadges) {
        this.experimentBadges = experimentBadges;
    }

    public Set<BadgeDTO> getExperimentBadges() {
        return experimentBadges;
    }

    public Set<UsageEntry> getUsage() {
        return usage;
    }

    public void setUsage(Set<UsageEntry> usage) {
        this.usage = usage;
    }

    public Set<UsageEntry> getExperimentUsage() {
        return experimentUsage;
    }

    public void setExperimentUsage(Set<UsageEntry> experimentUsage) {
        this.experimentUsage = experimentUsage;
    }
}
