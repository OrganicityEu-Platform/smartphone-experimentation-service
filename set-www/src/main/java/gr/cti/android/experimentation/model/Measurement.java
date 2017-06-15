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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measurement implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    private long resultId;
    private long experimentId;
    private long deviceId;
    private long timestamp;
    private Double latitude;
    private Double longitude;
    private String measurementKey;
    private String measurementValue;
    
    public Measurement() {
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public long getResultId() {
        return resultId;
    }
    
    public void setResultId(long resultId) {
        this.resultId = resultId;
    }
    
    public long getExperimentId() {
        return experimentId;
    }
    
    public void setExperimentId(long experimentId) {
        this.experimentId = experimentId;
    }
    
    public long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getMeasurementKey() {
        return measurementKey;
    }
    
    public void setMeasurementKey(String measurementKey) {
        this.measurementKey = measurementKey;
    }
    
    public String getMeasurementValue() {
        return measurementValue;
    }
    
    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }
    
    @Override
    public String toString() {
        return "Measurement{" + "latitude=" + latitude + ", longitude=" + longitude + ", measurementKey='" + measurementKey + '\'' + ", measurementValue='" + measurementValue + '\'' + '}';
    }
}
