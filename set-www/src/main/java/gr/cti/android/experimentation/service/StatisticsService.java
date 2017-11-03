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
import gr.cti.android.experimentation.model.Measurement;
import gr.cti.android.experimentation.model.Region;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.GeoResultRepository;
import gr.cti.android.experimentation.repository.MeasurementRepository;
import gr.cti.android.experimentation.repository.RegionRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gr.cti.android.experimentation.util.Utils.createPolygonForCoordinates;
import static gr.cti.android.experimentation.util.Utils.createPolygonForRegion;


/**
 * Provides connection to the sql database for storing data.
 */
@Service
public class StatisticsService {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);
    
    private static final double DIFF = 0.00004;

    @Autowired
    MeasurementRepository measurementRepository;
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
        final Set<Measurement> results = measurementRepository.findByExperimentId(experimentId);
        final Map<Integer, Integer> counts = new HashMap<>();
        for (final Measurement result : results) {
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
        final Set<Measurement> results = measurementRepository.findByExperimentId(experimentId);
        final Map<Integer, Integer> counts = new HashMap<>();
        for (final Measurement result : results) {
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

    public Map<Integer, Set<Long>> getTotalUserParticipationPerDay(final int experimentId) {
        LOGGER.debug("===================================USER PARTICIPATED TOTAL");
        final Set<Measurement> results = measurementRepository.findByExperimentId(experimentId);
        final Map<Integer, Set<Long>> counts = new HashMap<>();
        for (final Measurement result : results) {
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
        final Set<Measurement> results = measurementRepository.findByExperimentId(experimentId);
        final Map<Integer, Set<Integer>> counts = new HashMap<>();
        for (final Measurement result : results) {
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
        final Set<Measurement> results = measurementRepository.findByExperimentId(experimentId);
        final GeometryFactory fact = new GeometryFactory();
        final List<Polygon> polygons = new ArrayList<>();
        for (final Measurement result : results) {
            final double lat = result.getLatitude();
            final double lon = result.getLongitude();
            final Polygon pol = createPolygonForCoordinates(lat, lon);
            polygons.add(pol);
        }

        final GeometryCollection geometryCollection = (GeometryCollection) fact.buildGeometry(polygons);
        LOGGER.debug("Total AreaMeasured:" + geometryCollection.getArea() * 12365 + " Km2");
        return geometryCollection.getArea() * 12365;
    }

}
