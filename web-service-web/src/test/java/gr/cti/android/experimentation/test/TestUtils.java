package gr.cti.android.experimentation.test;

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.RegionDTO;
import gr.cti.android.experimentation.model.RegionListDTO;

import java.util.ArrayList;

/**
 * Created by amaxilatis on 15/7/2016.
 */
public class TestUtils {
    public static Experiment newTestExperiment1() {
        final Experiment newExperiment = new Experiment();
        //initialize
        newExperiment.setTimestamp(System.currentTimeMillis());
        newExperiment.setUrl("http://experiment.url");
        newExperiment.setUrlDescription("http://url.description.com");
        newExperiment.setStatus("TEST EXPERIMENT");
        newExperiment.setSensorDependencies("sensor1,sensor2,sensor3");
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

    public static RegionDTO newTestRegion1(final int experimentId) {
        final RegionDTO region = new RegionDTO();
        region.setExperimentId(experimentId);
        region.setName("TestRegion");
        region.setWeight("Low");
        region.setMaxMeasurements(10000);
        region.setMinMeasurements(1000);
        region.setCoordinates("[[[1,1],[2,2],[1,1]]]");
        return region;
    }
}
