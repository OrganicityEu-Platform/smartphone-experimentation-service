package gr.cti.android.experimentation.service;

/*-
 * #%L
 * SET Web Interface
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2018 CTI - Computer Technology Institute and Press "Diophantus"
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

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cti.android.experimentation.client.SensingOnTheGoClient;
import gr.cti.android.experimentation.model.ExperimentDTO;
import gr.cti.android.experimentation.model.ExperimentListDTO;
import gr.cti.android.experimentation.model.PluginListDTO;
import gr.cti.android.experimentation.model.RegionListDTO;
import gr.cti.android.experimentation.util.OrganicityAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BackendService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendService.class);
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public PluginListDTO getSensors() {
        final OrganicityAccount ou = OrganicityUserDetailsService.getCurrentUser();
        if (ou != null) {
            SensingOnTheGoClient c = new SensingOnTheGoClient(ou.getKeycloakSecurityContext().getTokenString());
            return c.listPlugins();
        } else {
            return null;
        }
    }
    
    public ExperimentListDTO getExperiments() {
        final OrganicityAccount ou = OrganicityUserDetailsService.getCurrentUser();
        if (ou != null) {
            SensingOnTheGoClient c = new SensingOnTheGoClient(ou.getKeycloakSecurityContext().getTokenString());
            return c.listExperiments();
        } else {
            return null;
        }
    }   public ExperimentDTO getExperiment(final String id) {
        final OrganicityAccount ou = OrganicityUserDetailsService.getCurrentUser();
        if (ou != null) {
            SensingOnTheGoClient c = new SensingOnTheGoClient(ou.getKeycloakSecurityContext().getTokenString());
            return c.getExperiment(id);
        } else {
            return null;
        }
    }
    
    public RegionListDTO getExperimentRegions(final String id) {
        final OrganicityAccount ou = OrganicityUserDetailsService.getCurrentUser();
        if (ou != null) {
            SensingOnTheGoClient c = new SensingOnTheGoClient(ou.getKeycloakSecurityContext().getTokenString());
            return c.getExperimentRegions(id);
        } else {
            return null;
        }
    }
}
