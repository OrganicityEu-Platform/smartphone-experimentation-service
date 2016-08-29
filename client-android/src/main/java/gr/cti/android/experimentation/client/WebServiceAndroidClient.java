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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class WebServiceAndroidClient {
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://api.smartphone-experimentation.eu/v1/";
    private String token;
    private HttpHeaders headers;
    private HttpEntity<String> req;

    public WebServiceAndroidClient() {
        this("");
    }

    public WebServiceAndroidClient(final String token) {
        this.token = token;
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        req = new HttpEntity<>("", headers);
    }

    public void clearToken() {
        this.token = "";
        headers.remove(HttpHeaders.AUTHORIZATION);
    }

    public void setToken(final String token) {
        this.token = token;
        if (!"".equals(token)) {
            headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
        }
        req = new HttpEntity<>("", headers);
    }

    public ExperimentListDTO listExperiments() {
        return restTemplate.exchange(BASE_URL + "experiment",
                HttpMethod.GET, req, ExperimentListDTO.class).getBody();
    }

    public ExperimentListDTO listLiveExperiments() {
        return restTemplate.exchange(BASE_URL + "experiment/live",
                HttpMethod.GET, req, ExperimentListDTO.class).getBody();
    }

    public ExperimentListDTO listExperiments(final int smartphoneId) {
        return restTemplate.exchange(BASE_URL + "experiment?phoneId=" + smartphoneId,
                HttpMethod.GET, req, ExperimentListDTO.class).getBody();
    }

    public ExperimentDTO getExperiment(final String id) {
        return restTemplate.exchange(BASE_URL + "experiment/" + id,
                HttpMethod.GET, req, ExperimentDTO.class).getBody();
    }

    public PluginListDTO listPlugins() {
        return restTemplate.exchange(BASE_URL + "plugin",
                HttpMethod.GET, req, PluginListDTO.class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId) {
        return restTemplate.exchange(BASE_URL + "/smartphone/" + smartphoneId + "/statistics",
                HttpMethod.GET, req, SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId, final int experimentId) {
        return restTemplate.exchange(BASE_URL + "/smartphone/" + smartphoneId + "/statistics/" + experimentId,
                HttpMethod.GET, req, SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneDTO postSmartphone(final SmartphoneDTO smartphone) {
        return restTemplate.exchange(BASE_URL + "/smartphone",
                HttpMethod.POST, new HttpEntity<>(smartphone, headers), SmartphoneDTO.class).getBody();
    }

    public RegionListDTO getExperimentRegions(final int experimentId) {
        return restTemplate.exchange(BASE_URL + "/experiment/" + experimentId + "/region",
                HttpMethod.GET, req, RegionListDTO.class).getBody();
    }

    public ResponseDTO postExperimentResults(final ResultListDTO resultListDTO) {
        return restTemplate.exchange(BASE_URL + "/data/multiple",
                HttpMethod.POST, new HttpEntity<>(resultListDTO, headers), ResponseDTO.class).getBody();
    }

}
