package eu.organicity.discovery.dto;

/*-
 * #%L
 * SET Common Model
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2017 CTI - Computer Technology Institute and Press "Diophantus"
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

/**
 * Created by amaxilatis on 17/1/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextDTO {
    private String experimenter;
    private String experiment;
    private String name;
    private String reputation;
    private String scope;
    private String last_updated_at;
    private LocationDTO position;

    public String getExperimenter() {
        return experimenter;
    }

    public void setExperimenter(String experimenter) {
        this.experimenter = experimenter;
    }

    public String getExperiment() {
        return experiment;
    }

    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReputation() {
        return reputation;
    }

    public void setReputation(String reputation) {
        this.reputation = reputation;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLast_updated_at() {
        return last_updated_at;
    }

    public void setLast_updated_at(String last_updated_at) {
        this.last_updated_at = last_updated_at;
    }

    public LocationDTO getPosition() {
        return position;
    }

    public void setPosition(LocationDTO position) {
        this.position = position;
    }
}
