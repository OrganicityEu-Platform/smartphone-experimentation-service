package gr.cti.android.experimentation;

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

import gr.cti.android.experimentation.model.*;

import java.util.ArrayList;

public class TestUtils {
    public static Experiment newTestExperiment1() {
        final Experiment newExperiment = new Experiment();
        //initialize
        newExperiment.setTimestamp(System.currentTimeMillis());
        newExperiment.setUrl("http://experiment.url");
        newExperiment.setStatus("1");
        newExperiment.setUserId("amaxilat@cti.gr");
        newExperiment.setSensorDependencies("sensor1,sensor2,sensor3");
        newExperiment.setExperimentId("asdbjkasdl213lksd");
        newExperiment.setName("TEST EXPERIMENT");
        newExperiment.setContextType("urn:oc:experiment");
        newExperiment.setFilename("my-file");
        newExperiment.setEnabled(true);
        newExperiment.setDescription("description");
        return newExperiment;
    }

    public static RegionListDTO newRegionList1() {
        final RegionListDTO regionList = new RegionListDTO();
        regionList.setRegions(new ArrayList<>());
        return regionList;
    }

    public static RegionDTO newTestRegion1(final String experimentId) {
        final RegionDTO region = new RegionDTO();
        region.setExperimentId(experimentId);
        region.setName("TestRegion");
        region.setWeight("Low");
        region.setMaxMeasurements(10000);
        region.setMinMeasurements(1000);
        region.setCoordinates("[[[1,1],[2,2],[1,1]]]");
        return region;
    }

    public static Plugin newTestPlugin1() {
        final Plugin plugin = new Plugin();
        plugin.setName("TestPlugin");
        plugin.setContextType("test.plugin.context.type");
        plugin.setFilename("testPlugin.jar");
        plugin.setRuntimeFactoryClass("eu.organicity.set.plugins.testplugin1");
        plugin.setDescription("test description");
        plugin.setImageUrl("http://image.jpg");
        plugin.setInstallUrl("http://repo.smartphone-experimentation.eu/plugin.jar");
        plugin.setUserId("1");
        return plugin;
    }
}
