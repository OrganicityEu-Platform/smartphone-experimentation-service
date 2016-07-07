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


@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartphoneDTO implements Serializable {

    private Integer id;
    private String deviceType;
    private Long phoneId;
    private String sensorsRules;

    public SmartphoneDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }

    public String getSensorsRules() {
        return sensorsRules;
    }

    public void setSensorsRules(String sensorsRules) {
        this.sensorsRules = sensorsRules;
    }

    @Override
    public String toString() {
        return "Smartphone{" +
                "id=" + id +
                ", deviceType='" + deviceType + '\'' +
                ", phoneId=" + phoneId +
                ", sensorsRules='" + sensorsRules + '\'' +
                '}';
    }
}
