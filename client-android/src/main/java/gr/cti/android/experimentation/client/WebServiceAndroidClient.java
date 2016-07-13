package gr.cti.android.experimentation.client;

/*-
 * #%L
 * Smartphone Experimentation Android Client
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
import org.springframework.web.client.RestTemplate;

public class WebServiceAndroidClient {
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://api.smartphone-experimentation.eu/v1/";
    private String token;

    public WebServiceAndroidClient() {
        this.token = "";
        restTemplate = new RestTemplate();
    }

    public WebServiceAndroidClient(final String token) {
        this.token = token;
        restTemplate = new RestTemplate();
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;

    }

    public Experiment[] listExperiments() {
        return restTemplate.getForEntity(BASE_URL + "experiment", Experiment[].class).getBody();
    }

    public Experiment[] listExperiments(final int smartphoneId) {
        return restTemplate.getForEntity(BASE_URL + "experiment?phoneId=" + smartphoneId, Experiment[].class).getBody();
    }

    public Experiment getExperiment(final Integer id) {
        return restTemplate.getForEntity(BASE_URL + "experiment/" + id, ExperimentDTO.class).getBody().getValue();
    }

    public PluginDTO[] listPlugins() {
        return restTemplate.getForEntity(BASE_URL + "plugin", PluginDTO[].class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId) {
        return restTemplate.getForEntity(BASE_URL + "/smartphone/" + smartphoneId + "/statistics", SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId, final int experimentId) {
        return restTemplate.getForEntity(BASE_URL + "/smartphone/" + smartphoneId + "/statistics/" + experimentId, SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneDTO postSmartphone(final SmartphoneDTO smartphone) {
        return restTemplate.postForEntity(BASE_URL + "/smartphone", smartphone, SmartphoneDTO.class).getBody();
    }

    public RegionListDTO getExperimentRegions(final int experimentId) {
        return restTemplate.getForEntity(BASE_URL + "/experiment/" + experimentId + "/region", RegionListDTO.class).getBody();
    }


}
