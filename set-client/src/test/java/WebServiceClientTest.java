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

import eu.organicity.discovery.dto.FeatureCollectionDTO;
import eu.organicity.discovery.dto.FeatureDTO;
import gr.cti.android.experimentation.client.WebServiceAndroidClient;
import gr.cti.android.experimentation.model.*;
import org.junit.Before;
import org.junit.Test;

public class WebServiceClientTest {

    private WebServiceAndroidClient client;

    @Before
    public void before() throws Exception {
        client = new WebServiceAndroidClient();
//        client.setToken("eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJkMmJhZTA0MS1iYmJlLTQzYWYtYmE5Zi1lOTRjYWEyMDFiZDciLCJleHAiOjAsIm5iZiI6MCwiaWF0IjoxNDc4Njg5NTU4LCJpc3MiOiJodHRwczovL2FjY291bnRzLm9yZ2FuaWNpdHkuZXUvcmVhbG1zL29yZ2FuaWNpdHkiLCJzdWIiOiIxYjZjMmU0Mi04OTViLTQ1NTYtOGYzNy04OTZhYzI5MDM5OGQiLCJ0eXAiOiJPZmZsaW5lIiwiYXpwIjoic21hcnRwaG9uZS1leHBlcmltZW50LW1hbmFnZW1lbnQiLCJzZXNzaW9uX3N0YXRlIjoiMjZiMjU0MjAtZjkxNC00MzYzLWFmZjEtMTQ2OTIwMjMyOGY1IiwiY2xpZW50X3Nlc3Npb24iOiI5NDZjYTE1Zi00OWQ0LTRhZDQtYjkwMS01M2JjOTQzNjA2ZjkiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiXX0sInJlc291cmNlX2FjY2VzcyI6e319.edgjyQpx5EiPeZQ3M6SZtKwL_lTyG9hMVKVmFpA7htninDAOcCEsqKzZKoDGEob0P2117QEMz7CNeZ0sIpyz6jhT1O38rC3U0Y-sF1FNwnxvUfk427B3LW9O0xIviL3CsV6Lux2IHCqRnHTzxo3xZTffA1gio45cq4u6MS50Q0Iq307052lO7jLsOhk5AcarZOqO5j5k9JxDzGUqQUzQKKZo2_H-GnndwmJQ5jF5S5Q1msULPXtLLlK0i0x6crQIf1If0TudnwB3LyLaCsbHpCCT4vV4HffrxPq_8_c1aBIw7fpXS57mTU1R2LoJd0wU-ocZK_PDZ15GfKW3m7eA_Q");
//        client.setEncodedToken("c21hcnRwaG9uZS1leHBlcmltZW50LW1hbmFnZW1lbnQ6YmI2ODFmZmItNDNiNi00OTRmLTgwYTItNDY3YjE1MDViNzkz");
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
        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15, "22");
        System.out.println(smartphoneStatistics);
    }

    @Test
    public void testGetExperimentRegions() throws Exception {
        final RegionListDTO regions = client.getExperimentRegions("22");
        for (RegionDTO dto : regions.getRegions()) {
            System.out.println(dto);
        }
    }

    @Test
    public void testListNearbyAssets() throws Exception {
        WebServiceAndroidClient client = new WebServiceAndroidClient();
        final FeatureCollectionDTO[] assets = client.listNearbyAssets(38.246639, 21.734573);
        for (final FeatureCollectionDTO asset : assets) {
            System.out.println(asset.getProperties().getName());
            for (final FeatureDTO featureDTO : asset.getFeatures()) {
                System.out.println("\t" + featureDTO.getProperties().getId() + " " + featureDTO.getGeometry());
            }
        }
    }

}
