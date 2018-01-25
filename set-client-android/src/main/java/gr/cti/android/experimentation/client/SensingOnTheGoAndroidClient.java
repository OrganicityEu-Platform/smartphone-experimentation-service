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

import eu.organicity.client.OrganicityServiceBaseClient;
import eu.organicity.discovery.dto.FeatureCollectionDTO;
import gr.cti.android.experimentation.model.ExperimentDTO;
import gr.cti.android.experimentation.model.ExperimentListDTO;
import gr.cti.android.experimentation.model.NewAssetDTO;
import gr.cti.android.experimentation.model.PluginDTO;
import gr.cti.android.experimentation.model.PluginListDTO;
import gr.cti.android.experimentation.model.RegionListDTO;
import gr.cti.android.experimentation.model.ResultDTO;
import gr.cti.android.experimentation.model.SmartphoneDTO;
import gr.cti.android.experimentation.model.SmartphoneStatisticsDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;

public class SensingOnTheGoAndroidClient extends OrganicityServiceBaseClient {

    private String baseUrl = "https://api.smartphone-experimentation.eu/";
    private String encodedToken;

    public SensingOnTheGoAndroidClient() {
        this("");
    }

    public SensingOnTheGoAndroidClient(final String token) {
        super(token);
    }

    public SensingOnTheGoAndroidClient(final String token, final String baseUrl) {
        super(token);
        this.baseUrl = baseUrl;
    }

    public void setErrorHandler(final ResponseErrorHandler responseErrorHandler) {
        restTemplate.setErrorHandler(responseErrorHandler);
    }

    public void clearToken() {
        headers.remove(HttpHeaders.AUTHORIZATION);
    }

    public void setEncodedToken(final String encodedToken) {
        this.encodedToken = encodedToken;
    }

    public boolean updateAccessToken() {
        return true;
    }

    public ExperimentListDTO listExperiments() {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/experiment",
                HttpMethod.GET, req, ExperimentListDTO.class).getBody();
    }

    public ExperimentListDTO listLiveExperiments() {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/experiment/live",
                HttpMethod.GET, req, ExperimentListDTO.class).getBody();
    }

    public ExperimentListDTO listExperiments(final int smartphoneId) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/experiment?phoneId=" + smartphoneId,
                HttpMethod.GET, req, ExperimentListDTO.class).getBody();
    }

    public ExperimentDTO getExperiment(final String experimentId) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/experiment/" + experimentId,
                HttpMethod.GET, req, ExperimentDTO.class).getBody();
    }

    public PluginListDTO listPlugins() {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/plugin",
                HttpMethod.GET, req, PluginListDTO.class).getBody();
    }

    public String addPlugin(PluginDTO dto) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/plugin?name=" + dto.getName() + "&contextType=" + dto.getContextType() + "&runtimeFactoryClass=" + dto.getRuntimeFactoryClass() + "&description=" + dto.getDescription() + "&imageUrl=" + dto.getImageUrl() + "&filename=" + dto.getFilename() + "&installUrl=none",
                HttpMethod.POST, new HttpEntity<>(headers), String.class).getBody();
    }

    public boolean deletePlugin(final int pluginId) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/plugin/" + pluginId,
                HttpMethod.DELETE, new HttpEntity<>(headers), String.class).getStatusCode().is2xxSuccessful();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/smartphone/" + smartphoneId + "/statistics",
                HttpMethod.GET, req, SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId, final String experimentId) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/smartphone/" + smartphoneId + "/statistics/" + experimentId,
                HttpMethod.GET, req, SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneDTO postSmartphone(final SmartphoneDTO smartphone) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/smartphone",
                HttpMethod.POST, new HttpEntity<>(smartphone, headers), SmartphoneDTO.class).getBody();
    }

    public RegionListDTO getExperimentRegions(final String experimentId) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/experiment/" + experimentId + "/region",
                HttpMethod.GET, req, RegionListDTO.class).getBody();
    }

    public String postExperimentResults(final ResultDTO dto) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        return restTemplate.exchange(baseUrl + "v1/data",
                HttpMethod.POST, new HttpEntity<>(dto, headers), String.class).getBody();
    }

    public NewAssetDTO sendAsset(final String assetName, final String assetType, final String experimentId,
                                 final double latitude, final double longitude) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }

        NewAssetDTO newAssetDTO = new NewAssetDTO();
        newAssetDTO.setName(assetName);
        newAssetDTO.setType(assetType);
        newAssetDTO.setExperimentId(experimentId);
        newAssetDTO.setLatitude(latitude);
        newAssetDTO.setLongitude(longitude);

        return restTemplate.exchange(baseUrl + "v1/asset/add",
                HttpMethod.POST, new HttpEntity<>(newAssetDTO, headers), NewAssetDTO.class).getBody();
    }

    /**
     * List all nearby assets.
     *
     * @param lat the latitude for the query.
     * @param lon the longitude for the query.
     * @return a {@see FeatureCollectionDTO} array.
     */
    public FeatureCollectionDTO[] listNearbyAssets(final double lat, final double lon) {
        if (!"".equals(getToken())) {
            updateAccessToken();
        }
        final MultiValueMap<String, Object> codeMap = new LinkedMultiValueMap<>();
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, "application/json");
        final HttpEntity<MultiValueMap<String, Object>> internalRec = new HttpEntity<>(codeMap, headers);

        return restTemplate.exchange(baseUrl + "assets/geo/search?lat=" + lat + "&long=" + lon + "&radius=20",
                HttpMethod.GET, internalRec, FeatureCollectionDTO[].class).getBody();
    }
}
