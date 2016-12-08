package eu.organicity.sitemanager.client;

/*-
 * #%L
 * SET Android Client
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
import eu.organicity.experiment.management.dto.OCApplicationListDTO;
import eu.organicity.sitemanager.dto.Asset;
import gr.cti.android.experimentation.client.OauthTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class OrganicityAndroidClient {
    private final RestTemplate restTemplate;
    private static final String ACCOUNTS_TOKEN_ENDPOINT = "https://accounts.organicity.eu/realms/organicity/protocol/openid-connect/token";
    private static final String EXPERIMENTERS_SITE_ENDPOINT = "https://exp.orion.organicity.eu/v2/";
    private static final String EXPERIMENT_MANAGEMENT_ENDPOINT = "http://31.200.243.76:8081/";
    private static final String ADS_ENDPOINT = "http://discovery.organicity.eu/v0/";
    private static final String SITEMANAGER_URL = "https://sitemanager.organicity.eu/v1/";
    private String token;
    private String encodedToken;
    private HttpHeaders headers;
    private HttpEntity<String> req;
    private String applicationId;
    private String experimentId;

    public OrganicityAndroidClient() {
        this("");
    }

    public OrganicityAndroidClient(final String token) {
        this.token = token;
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        req = new HttpEntity<>("", headers);
    }


    public void setErrorHandler(final ResponseErrorHandler responseErrorHandler) {
        restTemplate.setErrorHandler(responseErrorHandler);
    }

    public void clearToken() {
        this.token = "";
        headers.remove(HttpHeaders.AUTHORIZATION);
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setEncodedToken(final String encodedToken) {
        this.encodedToken = encodedToken;
    }


    public boolean updateAccessToken() {
        final MultiValueMap<String, String> codeMap = new LinkedMultiValueMap<>();
        codeMap.add("grant_type", "refresh_token");
        codeMap.add("refresh_token", token);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        final HttpEntity<MultiValueMap<String, String>> internalRec = new HttpEntity<>(codeMap, headers);

        final ResponseEntity<OauthTokenResponse> response =
                restTemplate.exchange(ACCOUNTS_TOKEN_ENDPOINT, HttpMethod.POST, internalRec, OauthTokenResponse.class);
        if (response.hasBody()) {
            OauthTokenResponse credentials = response.getBody();

            headers.remove(HttpHeaders.AUTHORIZATION);
            headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", credentials.getAccess_token()));
            req = new HttpEntity<>("", headers);
            return true;
        }
        return false;
    }

    //    Experimenters Site API

    public String postAsset(final String entity) {
        if (!"".equals(token)) {
            updateAccessToken();
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add("X-Organicity-Application", applicationId);
        headers.add("X-Organicity-Experiment", experimentId);

        final HttpEntity<String> requestEntity = new HttpEntity<>(entity, headers);
        return restTemplate.exchange(EXPERIMENTERS_SITE_ENDPOINT + "entities",
                HttpMethod.POST, requestEntity, String.class).getBody();
    }

    //    Site Management API

    /**
     * List all the avaialble asset types for OC.
     *
     * @return an {@see Asset} array.
     */
    public Asset[] listAssetTypes() {
        if (!"".equals(token)) {
            updateAccessToken();
        }
        return restTemplate.exchange(SITEMANAGER_URL + "dictionary/assettypes",
                HttpMethod.GET, req, Asset[].class).getBody();
    }

    //    Discovery API

    /**
     * List all nearby assets.
     *
     * @param lat    the latitude for the query.
     * @param lon    the longitude for the query.
     * @param radius the radius for the circle to search for assets inside it.
     * @return a {@see FeatureCollectionDTO} array.
     */
    public FeatureCollectionDTO[] listNearbyAssets(final double lat, final double lon, final int radius) {
        if (!"".equals(token)) {
            updateAccessToken();
        }
        final MultiValueMap<String, String> codeMap = new LinkedMultiValueMap<>();
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.USER_AGENT, "java-client");
        final HttpEntity<MultiValueMap<String, String>> internalRec = new HttpEntity<>(codeMap, headers);
        return restTemplate.exchange(ADS_ENDPOINT + "assets/geo/search?lat=" + lat + "&long=" + lon + "&radius=" + radius + "&km=true",
                HttpMethod.GET, internalRec, FeatureCollectionDTO[].class).getBody();
    }

    //    Experiment Management API

    /**
     * List all applications for the current user from the Experiment Management OC Service.
     *
     * @return a {@see OCApplicationListDTO}.
     */
    public OCApplicationListDTO listApplications() {
        return restTemplate.exchange(EXPERIMENT_MANAGEMENT_ENDPOINT + "allapplications",
                HttpMethod.GET, req, OCApplicationListDTO.class).getBody();
    }
}
