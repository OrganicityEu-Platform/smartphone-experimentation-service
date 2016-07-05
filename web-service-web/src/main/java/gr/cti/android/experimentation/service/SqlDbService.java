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

import com.google.maps.model.LatLng;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.GeoResult;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.GeoResultRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import gr.cti.android.experimentation.util.Geohasher;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;


/**
 * Provides connection to the sql database for storing data.
 */
@Service
public class SqlDbService {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(SqlDbService.class);

    @Autowired
    ResultRepository resultRepository;
    @Autowired
    GeoResultRepository geoResultRepository;
    @Autowired
    SmartphoneRepository smartphoneRepository;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    GCMService gcmService;
    @Autowired
    OrionService orionService;
    private DecimalFormat df;

    @PostConstruct
    public void init() {
        df = new DecimalFormat("#.000");
    }

    @Async
    public void store(final Result newResult) throws IOException {

        LOGGER.info("saving result");
        try {
            final Set<Result> res = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAndMessage(newResult.getExperimentId(), newResult.getDeviceId(), newResult.getTimestamp(), newResult.getMessage());
            if (res == null || res.isEmpty()) {
                resultRepository.save(newResult);

                LOGGER.info("saveExperiment: OK");
                LOGGER.info("saveExperiment: Stored:");
                LOGGER.info("-----------------------------------");

                //send incentive messages to phone
                try {
                    gcmService.check(newResult);
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }

                //store to orion
                try {
                    Experiment experiment = experimentRepository.findById(newResult.getExperimentId());
                    orionService.store(newResult, experiment);
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
            }
        } catch (Exception e) {
            LOGGER.info("saveExperiment: FAILEd" + e.getMessage(), e);
            LOGGER.info("-----------------------------------");
        }
    }

    @Async
    public void storeGeo(final Result newResult) throws IOException, JSONException {

        GeoResult geoResult = new GeoResult();

        Double longitude = null;
        Double latitude = null;
        JSONObject object = new JSONObject(newResult.getMessage());
        Iterator keysIterator = object.keys();
        while (keysIterator.hasNext()) {
            String key = (String) keysIterator.next();
            if (key.toLowerCase().contains("longitude")) {
                try {
                    longitude = Double.valueOf(object.getString(key));
                } catch (Exception e) {
                    longitude = object.getDouble(key);
                }
            } else if (key.toLowerCase().contains("latitude")) {
                try {
                    latitude = Double.valueOf(object.getString(key));
                } catch (Exception e) {
                    latitude = object.getDouble(key);
                }
            }
        }

        if (latitude != null && longitude != null) {
            String longitudeS = df.format(longitude);
            String latitudeS = df.format(latitude);

            final String hash = Geohasher.hash(new LatLng(latitude, longitude));
            LOGGER.info("Hash:" + hash);

            geoResult.setLatitude(Double.parseDouble(latitudeS));
            geoResult.setLongitude(Double.parseDouble(longitudeS));
            geoResult.setTimestamp(newResult.getTimestamp());
            geoResult.setMessage(newResult.getMessage());
        }

        if (geoResultRepository.findByTimestampAndMessage(geoResult.getTimestamp(), geoResult.getMessage()).isEmpty()) {
            geoResultRepository.save(geoResult);
        }
    }
}
