package gr.cti.android.experimentation.model;

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
    private int experimentId;
    /**
     * Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     */
    private int deviceId;
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
    public int getExperimentId() {
        return experimentId;
    }

    /**
     * Sets the Id of the {@see Experiment} the badge was awarded for.
     *
     * @param experimentId the Id of the {@see Experiment} the badge was awarded for.
     */
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * Returns the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     *
     * @return the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     *
     * @param deviceId the Id of the {@see Smartphone} used to collect the data the badge was awarded for.
     */
    public void setDeviceId(int deviceId) {
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
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + experimentId;
        result = 31 * result + deviceId;
        result = 31 * result + message.hashCode();
        return result;
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
