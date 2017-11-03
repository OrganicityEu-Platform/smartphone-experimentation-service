package gr.cti.android.experimentation.controller.api;

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

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Measurement;
import gr.cti.android.experimentation.service.SqlDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class MeasurementsController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementsController.class);
    
    @Autowired
    SqlDbService sqlDbService;
    
    @RequestMapping(value = "/experiment/measurements/{experimentId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Set<Measurement> getExperimentMeasurementsByExperimentId(@PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after, @RequestParam(value = "to", defaultValue = "0", required = false) final String to, @RequestParam(value = "region", defaultValue = "0", required = false) final String regionId, @RequestParam(value = "accuracy", required = false, defaultValue = "3") final int accuracy, Principal principal) {
        LOGGER.info("GET /experiment/measurements/" + experiment + " " + principal);
        return getExperimentMeasurements(experiment, deviceId, after, to, accuracy);
    }
    
    private Set<Measurement> getExperimentMeasurements(final String experiment, final int deviceId, final String after, final String to, final int accuracy) {
        final String format = getFormat(accuracy);
        final DecimalFormat df = new DecimalFormat(format);
        LOGGER.info("format:" + format);
        final long start = parseDateMillis(after);
        long end = parseDateMillis(to);
        
        if (end == 0) {
            end = System.currentTimeMillis();
        }
        
        final Set<Measurement> results;
        Experiment exp = experimentRepository.findByExperimentId(experiment);
        if (deviceId == 0) {
            results = measurementRepository.findByExperimentIdAndTimestampBetween(exp.getId(), start, end);
        } else {
            results = measurementRepository.findByExperimentIdAndDeviceIdAndTimestampBetween(exp.getId(), deviceId, start, end);
        }
        return results;
    }
    
    private String getFormat(int accuracy) {
        String format = "#";
        if (accuracy > 0) {
            format += ".";
            for (int i = 0; i < accuracy; i++) {
                format += "0";
            }
        }
        return format;
    }
}
