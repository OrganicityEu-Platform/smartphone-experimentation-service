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

import gr.cti.android.experimentation.client.WebServiceAndroidClient;
import gr.cti.android.experimentation.model.*;
import org.junit.Before;
import org.junit.Test;

public class WebServiceClientTest {

    private WebServiceAndroidClient client;

    @Before
    public void before() throws Exception {
        client = new WebServiceAndroidClient();
    }

    @Test
    public void testListExperiments() throws Exception {
        final Experiment[] experiments = client.listExperiments();
        for (final Experiment experiment : experiments) {
            System.out.println(experiment);
        }
    }

    @Test
    public void testListExperimentsBySmartphoneId() throws Exception {
        final Experiment[] experiments = client.listExperiments(15);
        for (final Experiment experiment : experiments) {
            System.out.println(experiment);
        }
    }

    @Test
    public void testGetExperiment() throws Exception {
        final Experiment[] experiments = client.listExperiments();
        final Experiment experiment = experiments[0];
        final Experiment theExperiment = client.getExperiment(experiment.getId());
        System.out.println(theExperiment);
    }

    @Test
    public void testListPlugins() throws Exception {
        final PluginDTO[] plugins = client.listPlugins();
        for (final PluginDTO plugin : plugins) {
            System.out.println(plugin);
        }
    }

    @Test
    public void testGetPlugin() throws Exception {
        final Experiment[] experiments = client.listExperiments();
        final Experiment experiment = experiments[0];
        final Experiment theExperiment = client.getExperiment(experiment.getId());
        System.out.println(theExperiment);
    }

    @Test
    public void testGetSmartphoneStatistics() throws Exception {
        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15);
        System.out.println(smartphoneStatistics);
    }

    @Test
    public void testGetSmartphoneStatistics2() throws Exception {
        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15, 22);
        System.out.println(smartphoneStatistics);
    }

    @Test
    public void testGetExperimentRegions() throws Exception {
        final RegionListDTO regions = client.getExperimentRegions(22);
        for (RegionDTO dto : regions.getRegions()) {
            System.out.println(dto);
        }
    }

}
