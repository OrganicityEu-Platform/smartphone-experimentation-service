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
        client.setToken("eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI2NDVhMjk1Ni1iYzE4LTRkNzMtODM1Yy1lZWYwNmZhY2M1ODIiLCJleHAiOjE0NzMxNTU3MzAsIm5iZiI6MCwiaWF0IjoxNDczMTU1NDMwLCJpc3MiOiJodHRwczovL2FjY291bnRzLm9yZ2FuaWNpdHkuZXUvcmVhbG1zL29yZ2FuaWNpdHkiLCJhdWQiOiJzbWFydHBob25lLWV4cGVyaW1lbnQtbWFuYWdlbWVudCIsInN1YiI6IjQ3N2EwNDMwLWExMjQtNDA5NC1hMDQ2LWNjYjQxMWMwNGNiZiIsInR5cCI6IkJlYXJlciIsImF6cCI6InNtYXJ0cGhvbmUtZXhwZXJpbWVudC1tYW5hZ2VtZW50Iiwibm9uY2UiOiItNTAyNzc2Mjk1NzEyNTExMTA5MCIsInNlc3Npb25fc3RhdGUiOiI0NjU4ZTYyZi1kYzMyLTRjNzUtYjBhMC04YTU1Nzg2OWVhMjgiLCJjbGllbnRfc2Vzc2lvbiI6Ijg0NmY0OTU1LTExMjItNDQ2MC04YTBkLWE0YTczNGU1ZTVhYyIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3NldC5vcmdhbmljaXR5LmV1LyoiXSwicmVzb3VyY2VfYWNjZXNzIjp7fSwibmFtZSI6IkRpbWl0cmlvcyBBbWF4aWxhdGlzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYW1heGlsYXRAY3RpLmdyIiwiZ2l2ZW5fbmFtZSI6IkRpbWl0cmlvcyIsImZhbWlseV9uYW1lIjoiQW1heGlsYXRpcyIsImVtYWlsIjoiYW1heGlsYXRAY3RpLmdyIn0.CNDjD79T30kG623UfxYtufmkmSq720OeQmuruSxkLpQZBEZ0eXnjW40Ugkjp2uo8KM6XvoqVVIhaL4OBuDDLxD_DQ0-Q154oaFJHf71rYgFrhJM9F2u83K1Mo74aVmKGy4B5Koexqt03EekSJKKfyU7n-oC48hAOopIBXxEhLBl2b44UOhu8cqq3P7EdcQpDSofBM9S2xaZGo0QCE61KFxt0dB2gl1eS7Bjuu1tYk7RoIX5zs0AfIr5vwM4N2epTcbnYwGgNjqM0GZjSaJIAf9aSk52mVeqVkZPySQBLM19PnXH3QGeGwOxBuRZ7l6OLHzaOfKA532VBwUerYRc32A");
    }

    @Test
    public void testListExperiments() throws Exception {
        final ExperimentListDTO experiments = client.listExperiments();
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

}
