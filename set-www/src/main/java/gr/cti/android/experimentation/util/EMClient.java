package gr.cti.android.experimentation.util;

/*-
 * #%L
 * SET Android Client
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

import eu.organicity.experiment.management.dto.OCApplicationListDTO;
import org.springframework.web.client.RestTemplate;

//    Experiment Management API
public class EMClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String EXPERIMENT_MANAGEMENT_ENDPOINT = "http://31.200.243.76:8081/";
    
    /**
     * List all applications for the current user from the Experiment Management OC Service.
     *
     * @return a {@see OCApplicationListDTO}.
     */
    public OCApplicationListDTO listApplications() {
        return restTemplate.getForObject(EXPERIMENT_MANAGEMENT_ENDPOINT + "allapplications", OCApplicationListDTO.class);
    }
}
