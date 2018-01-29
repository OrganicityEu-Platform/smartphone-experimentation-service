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


import com.vividsolutions.jts.geom.Polygon;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Measurement;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.MeasurementRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.util.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gr.cti.android.experimentation.util.Utils.createPolygonForCoordinates;


/**
 * Provides connection to the sql database for storing data.
 */
@Service
public class LocationService {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    MeasurementRepository measurementRepository;

//    @PostConstruct
//    public void init() {
//        Iterable<Experiment> exps = experimentRepository.findAll();
//        List<Polygon> allPolygons = new ArrayList<>();
//        for (Experiment exp : exps) {
//            List<Polygon> expPolygons = experimentPolygons(exp);
//            double area = Utils.polygons2Area(expPolygons);
//            System.out.println("Exp:" + exp.getExperimentId() + " area: " + area);
//            allPolygons.addAll(expPolygons);
//        }
//
//        double totalArea = Utils.polygons2Area(allPolygons);
//        System.out.println("Total area: " + totalArea);
//    }

    public double experimentArea(final int id) {
        final Experiment experiment = experimentRepository.findById(id);
        return experimentArea(experiment);
    }

    public double experimentArea(final Experiment experiment) {
        final List<Polygon> polygons = experimentPolygons(experiment);
        return Utils.polygons2Area(polygons);
    }

    public List<Polygon> experimentPolygons(final Experiment experiment) {
        final Set<Measurement> results = measurementRepository.findByExperimentId(experiment.getExperimentId());
        final List<Polygon> polygons = new ArrayList<>();
        System.out.println("converting " + results.size() + " results to area");
        for (final Measurement result : results) {
            if (result.getLatitude() != null && result.getLongitude() != null) {
                double longitude = result.getLongitude();
                double latitude = result.getLatitude();
                final Polygon pol = createPolygonForCoordinates(latitude, longitude);
                polygons.add(pol);
            }
        }
        return polygons;
    }

}
