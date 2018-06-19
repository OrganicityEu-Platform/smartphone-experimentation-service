/*-
 * #%L
 * Smartphone Experimentation Client
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

import gr.cti.android.experimentation.client.SensingOnTheGoAndroidClient;
import gr.cti.android.experimentation.model.ExperimentDTO;
import gr.cti.android.experimentation.model.ExperimentListDTO;
import gr.cti.android.experimentation.model.PluginDTO;
import gr.cti.android.experimentation.model.PluginListDTO;
import gr.cti.android.experimentation.model.RegionDTO;
import gr.cti.android.experimentation.model.RegionListDTO;
import gr.cti.android.experimentation.model.SmartphoneStatisticsDTO;
import org.junit.Before;
import org.junit.Test;

public class SensingOnTheGoClientTest {

    private SensingOnTheGoAndroidClient client;

    @Before
    public void before() throws Exception {
        client = new SensingOnTheGoAndroidClient();
    }

    @Test
    public void testListExperiments() throws Exception {
        final ExperimentListDTO experiments = client.listExperiments();
        for (final ExperimentDTO experiment : experiments.getExperiments()) {
            System.out.println(experiment);
        }
    }

    @Test
    public void testListExperimentsLive() throws Exception {
        final ExperimentListDTO experiments = client.listLiveExperiments();
        for (final ExperimentDTO experiment : experiments.getExperiments()) {
            System.out.println(experiment);
        }
    }

    @Test
    public void testListExperimentsBySmartphoneId() throws Exception {
        final ExperimentListDTO experiments = client.listExperiments(15);
        for (final ExperimentDTO experiment : experiments.getExperiments()) {
            System.out.println(experiment);
        }
    }

    @Test
    public void testGetExperiment() throws Exception {
        final ExperimentListDTO experiments = client.listExperiments();
        final ExperimentDTO experiment = experiments.getExperiments().get(0);
        final ExperimentDTO theExperiment = client.getExperiment(experiment.getId());
        System.out.println(theExperiment);
    }

    @Test
    public void testListPlugins() throws Exception {
        final PluginListDTO pluginList = client.listPlugins();
        for (final PluginDTO plugin : pluginList.getPlugins()) {
            System.out.println(plugin);
        }
    }

    @Test
    public void testGetPlugin() throws Exception {
        final ExperimentListDTO experiments = client.listExperiments();
        final ExperimentDTO experiment = experiments.getExperiments().get(0);
        final ExperimentDTO theExperiment = client.getExperiment(experiment.getId());
        System.out.println(theExperiment);
    }

    @Test
    public void testGetSmartphoneStatistics() throws Exception {
        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15);
        System.out.println(smartphoneStatistics);
    }

    @Test
    public void testGetSmartphoneStatistics2() throws Exception {
        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15, "5a5356feebdd75d06468e5e4");
        System.out.println(smartphoneStatistics);
    }

    @Test
    public void testGetExperimentRegions() throws Exception {
        final RegionListDTO regions = client.getExperimentRegions("5a5356feebdd75d06468e5e4");
        for (RegionDTO dto : regions.getRegions()) {
            System.out.println(dto);
        }
    }

//    @Test
//    public void testListNearbyAssets() throws Exception {
//        SensingOnTheGoAndroidClient client = new SensingOnTheGoAndroidClient();
//        final FeatureCollectionDTO[] assets = client.listNearbyAssets(38.246639, 21.734573);
//        for (final FeatureCollectionDTO asset : assets) {
//            System.out.println(asset.getProperties().getName());
//            for (final FeatureDTO featureDTO : asset.getFeatures()) {
//                System.out.println("\t" + featureDTO.getProperties().getId() + " " + featureDTO.getGeometry());
//            }
//        }
//    }

}
