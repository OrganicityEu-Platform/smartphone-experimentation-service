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


import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import gr.cti.android.experimentation.model.Region;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.*;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static gr.cti.android.experimentation.util.Utils.createPolygonForCoordinates;
import static gr.cti.android.experimentation.util.Utils.createPolygonForRegion;
import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Provides connection to the sql database for storing data.
 */
@Service
public class StatisticsService {

    /**
     * a log4j logger to print messages.
     */
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(StatisticsService.class);
    
    private static final double DIFF = 0.00004;

    @Autowired
    ResultRepository resultRepository;
    @Autowired
    GeoResultRepository geoResultRepository;
    @Autowired
    SmartphoneRepository smartphoneRepository;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    GCMService gcmService;
    @Autowired
    OrionService orionService;


    public Map<Integer, Integer> getContributionsPerHour(final int experimentId) {
        LOGGER.debug("===================================CONTRIBUTION PER HOUR");
        final Set<Result> results = resultRepository.findByExperimentId(experimentId);
        final Map<Integer, Integer> counts = new HashMap<>();
        for (final Result result : results) {
            int index = new DateTime(result.getTimestamp()).getHourOfDay();
            if (!counts.containsKey(index)) {
                counts.put(index, 0);
            }
            counts.put(index, counts.get(index) + 1);
        }
        for (final Integer integer : counts.keySet()) {
            LOGGER.debug(integer + " " + counts.get(integer));
        }
        return counts;
    }

    public Map<Integer, Integer> getContributionsPerDay(final int experimentId) {
        LOGGER.debug("===================================CONTRIBUTION PER DAY");
        final Set<Result> results = resultRepository.findByExperimentId(experimentId);
        final Map<Integer, Integer> counts = new HashMap<>();
        for (final Result result : results) {
            int index = new DateTime(result.getTimestamp()).withMillisOfDay(0).getDayOfYear();
            if (!counts.containsKey(index)) {
                counts.put(index, 0);
            }
            counts.put(index, counts.get(index) + 1);
        }
        for (final Integer integer : counts.keySet()) {
            LOGGER.info(integer + " " + counts.get(integer));
        }
        return counts;
    }

    public Map<Integer, Set<Integer>> getTotalUserParticipationPerDay(final int experimentId) {
        LOGGER.debug("===================================USER PARTICIPATED TOTAL");
        final Set<Result> results = resultRepository.findByExperimentId(experimentId);
        final Map<Integer, Set<Integer>> counts = new HashMap<>();
        for (final Result result : results) {
            int index = new DateTime(result.getTimestamp()).withMillisOfDay(0).getDayOfYear();
            if (!counts.containsKey(index)) {
                counts.put(index, new HashSet<>());
            }
            counts.get(index).add(result.getDeviceId());
        }
        for (final Integer integer : counts.keySet()) {
            LOGGER.debug(integer + " " + counts.get(integer).size());
        }
        LOGGER.info("===================================USERS PARTICIPATING");
        for (final Integer integer : counts.keySet()) {
            LOGGER.debug(integer + " " + counts.get(integer));
        }
        return counts;
    }

    public Map<Integer, Set<Integer>> getTemporalCoveragePerDay(final int experimentId) {
        LOGGER.debug("===================================TEMPORAL COVERAGE");
        final Set<Result> results = resultRepository.findByExperimentId(experimentId);
        final Map<Integer, Set<Integer>> counts = new HashMap<>();
        for (final Result result : results) {
            int index = new DateTime(result.getTimestamp()).withMillisOfDay(0).getDayOfYear();
            int hour = new DateTime(result.getTimestamp()).getHourOfDay();
            if (!counts.containsKey(index)) {
                counts.put(index, new HashSet<>());
            }
            counts.get(index).add(hour);
        }
        for (final Integer integer : counts.keySet()) {
            LOGGER.debug(integer + " " + counts.get(integer).size());
        }
        LOGGER.debug("===================================");
        for (final Integer integer : counts.keySet()) {
            LOGGER.debug(integer + " " + counts.get(integer));
        }
        return counts;
    }

    public double getExperimentArea(final String experimentId) {
        LOGGER.debug("===================================TEMPORAL COVERAGE");
        final Set<Region> regions = regionRepository.findByExperimentId(experimentId);

        final GeometryFactory fact = new GeometryFactory();
        final List<Polygon> polygons = new ArrayList<>();

        for (final Region region : regions) {
            try {
                final Polygon polygonFromCoordinates = createPolygonForRegion(region);
                polygons.add(polygonFromCoordinates);
                LOGGER.debug(region.getName() + " Area:" + polygonFromCoordinates.getArea() * 12365 + " Km2");
            } catch (JSONException ignore) {
            }
        }
        final MultiPolygon polygon = new MultiPolygon(polygons.toArray(new Polygon[polygons.size()]), fact);

        LOGGER.debug("Total Area:" + polygon.getArea() * 12365 + " Km2");
        return polygon.getArea() * 12365;
    }

    public double getCoveredExperimentArea(final int experimentId) {
        LOGGER.debug("===================================TEMPORAL COVERAGE");
        final Set<Result> results = resultRepository.findByExperimentId(experimentId);
        final GeometryFactory fact = new GeometryFactory();
        final List<Polygon> polygons = new ArrayList<>();
        for (final Result result : results) {
            try {
                final JSONObject res = new JSONObject(result.getMessage());
                final double lat = res.getDouble("org.ambientdynamix.contextplugins.Latitude");
                final double lon = res.getDouble("org.ambientdynamix.contextplugins.Longitude");
                final Polygon pol = createPolygonForCoordinates(lat, lon);
                polygons.add(pol);
            } catch (Exception ignore) {
            }
        }

        final GeometryCollection geometryCollection = (GeometryCollection) fact.buildGeometry(polygons);
        LOGGER.debug("Total AreaMeasured:" + geometryCollection.getArea() * 12365 + " Km2");
        return geometryCollection.getArea() * 12365;
    }

}
