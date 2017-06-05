package gr.cti.android.experimentation.service;

/*-
 * #%L
 * Smartphone Experimentation Web Service
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

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.util.EMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Provides connection to the orion context broker for storing data.
 */
@Service
public class ExperimentPortalService {
    
    /**
     * a log4j logger to print messages.
     */
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(ExperimentPortalService.class);
    
    private String BASE_URL = "http://31.200.243.76:8081/";
    
    @Autowired
    ExperimentRepository experimentRepository;
    
    private EMClient emClient = new EMClient();
    
    public void getOCExperimentId(final String experimentId) {
        emClient.listApplications().getApplications().stream().filter(ocApplicationDTO -> ocApplicationDTO.getApplicationId().equals(experimentId)).forEach(ocApplicationDTO -> {
            Experiment exp = experimentRepository.findByExperimentId(experimentId);
            exp.setOcExperimentId(ocApplicationDTO.getExperimentId());
            experimentRepository.save(exp);
        });
    }
}
