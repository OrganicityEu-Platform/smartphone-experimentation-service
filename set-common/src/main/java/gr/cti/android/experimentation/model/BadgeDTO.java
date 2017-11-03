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

/**
 * Entity that describes an award badge given to users for gathering data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BadgeDTO implements Serializable {
    /**
     * Id of the entity.
     */
    private int id;
    /**
     * Timestamp of when the badge was awarded.
     */
    private long timestamp;
    /**
     * Id of the {@see Experiment} the badge was awarded for.
     */
    private String experimentId;
    /**
     * Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     */
    private long deviceId;
    /**
     * Message of the badge. Used to describe the action awarded for.
     */
    private String message;

    /**
     * Returns the unique id of the badge.
     *
     * @return the unique id of the badge.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique id of the badge.
     *
     * @param id the unique id of the badge.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the timestamp the badge was awarded at.
     *
     * @return the timestamp the badge was awarded at.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp the badge was awarded at.
     *
     * @param timestamp the timestamp the badge was awarded at.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the Id of the {@see Experiment} the badge was awarded for.
     *
     * @return the Id of the {@see Experiment} the badge was awarded for.
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * Sets the Id of the {@see Experiment} the badge was awarded for.
     *
     * @param experimentId the Id of the {@see Experiment} the badge was awarded for.
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * Returns the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     *
     * @return the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     */
    public long getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     *
     * @param deviceId the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     */
    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the message of the badge. Used to describe the action awarded for.
     *
     * @return the message of the badge. Used to describe the action awarded for.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the badge. Used to describe the action awarded for.
     *
     * @param message the message of the badge. Used to describe the action awarded for.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BadgeDTO badge = (BadgeDTO) o;

        if (id != badge.id
                || timestamp != badge.timestamp
                || experimentId != badge.experimentId
                || deviceId != badge.deviceId) {
            return false;
        }
        return message.equals(badge.message);

    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", experimentId=" + experimentId +
                ", deviceId=" + deviceId +
                ", message='" + message + '\'' +
                '}';
    }
}
