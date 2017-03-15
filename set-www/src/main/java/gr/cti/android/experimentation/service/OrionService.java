package gr.cti.android.experimentation.service;

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

import com.amaxilatis.orion.OrionClient;
import com.amaxilatis.orion.model.OrionContextElement;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.organicity.entities.handler.attributes.Attribute;
import eu.organicity.entities.handler.entities.SmartphoneDevice;
import eu.organicity.entities.handler.metadata.Datatype;
import eu.organicity.entities.namespace.OrganicityAttributeTypes;
import eu.organicity.entities.namespace.OrganicityDatatypes;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.NewAssetDTO;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.util.OauthTokenResponse;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Provides connection to the orion context broker for storing data.
 */
@Service
public class OrionService {
    
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = getLogger(OrionService.class);
    
    private static final String ORION_SMARTPHONE_EXPERIMENT_ID_FORMAT = "urn:oc:entity:experimenters:%s:%s:%s";
    private static final String ACCOUNTS_TOKEN_ENDPOINT = "https://accounts.organicity.eu/realms/organicity/protocol/openid-connect/token";
    private static final String EXP_PROXY_ENDPOINT = "https://exp.orion.organicity.eu/v2/entities";
    
    private OrionClient orionClient;
    
    private ObjectMapper mapper;
    @Value("${w3w.key}")
    private String w3wApiKey;
    @Value("${orion.url}")
    private String orionUrl;
    @Value("${orion.token}")
    private String orionToken;
    @Value("${orion.service}")
    private String orionService;
    @Value("${orion.servicePath}")
    private String orionServicePath;
    @Value("${token.offline}")
    private String offlineToken;
    @Value("${token.encoded}")
    private String encodedToken;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    //    private What3Words w3w;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    ExperimentPortalService experimentPortalService;
    
    @PostConstruct
    
    public void init() {
        orionClient = new OrionClient(orionUrl, orionToken, orionService, orionServicePath);
        mapper = new ObjectMapper();
        
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {
                if (clienthttpresponse.getStatusCode() == HttpStatus.BAD_REQUEST || clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
                    LOGGER.error("Text:" + clienthttpresponse.getStatusText());
                    String result = new BufferedReader(new InputStreamReader(clienthttpresponse.getBody())).lines().collect(Collectors.joining("\n"));
                    LOGGER.error("result:" + result);
                }
            }
            
            @Override
            public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {
                if (clienthttpresponse.getStatusCode() != HttpStatus.OK) {
                    return true;
                }
                return false;
            }
        });
        //        w3w = new What3Words(w3wApiKey);
    }
    
    @Async
    public void store(Result newResult, Experiment experiment) {
        LOGGER.info("store-orion:" + newResult.getDeviceId());
        
        final SmartphoneDevice locationPhoneEntity = new SmartphoneDevice();
        
        locationPhoneEntity.setTimestamp(new Date());
        
        if (experiment.getOcExperimentId() == null) {
            experimentPortalService.getOCExperimentId(experiment.getExperimentId());
        }
        
        experiment = experimentRepository.findById(experiment.getId());
        if (experiment == null || experiment.getUserId() == null || experiment.getOcExperimentId() == null) {
            LOGGER.error("experiment == null");
            return;
        }
        final String uri = String.format(ORION_SMARTPHONE_EXPERIMENT_ID_FORMAT, experiment.getUserId(), experiment.getOcExperimentId(), newResult.getDeviceId());
        LOGGER.info("Orion URI: " + uri);
        
        try {
            LOGGER.info(newResult.getMessage());
            final JSONObject readingList = new JSONObject(newResult.getMessage());
            
            final Iterator keys = readingList.keys();
            String latitude = null;
            String longitude = null;
            while (keys.hasNext()) {
                final String key = (String) keys.next();
                if (key.contains("Latitude")) {
                    latitude = String.valueOf(readingList.get(key));
                } else if (key.contains("Longitude")) {
                    longitude = String.valueOf(readingList.get(key));
                } else if (key.startsWith(OrganicityAttributeTypes.Types.SOUND_PRESSURE_LEVEL.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.SOUND_PRESSURE_LEVEL, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.TEMPERATURE.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.TEMPERATURE, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.RELATIVE_HUMIDITY.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.RELATIVE_HUMIDITY, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.ATMOSPHERIC_PRESSURE.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.ATMOSPHERIC_PRESSURE, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.CARBON_MONOXIDE.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.CARBON_MONOXIDE, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.METHANE.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.METHANE, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.LPG.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.LPG, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.ILLUMINANCE.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.ILLUMINANCE, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.PARTICLES10.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.PARTICLES10, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.PARTICLES25.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.PARTICLES25, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.BATTERY_LEVEL.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.BATTERY_LEVEL, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                } else if (key.contains(OrganicityAttributeTypes.Types.BATTERY_VOLTAGE.getUrn())) {
                    Attribute a = new Attribute(OrganicityAttributeTypes.Types.BATTERY_VOLTAGE, String.valueOf(readingList.get(key)));
                    Datatype dm = new Datatype(OrganicityDatatypes.DATATYPES.NUMERIC);
                    a.addMetadata(dm);
                    locationPhoneEntity.addAttribute(a);
                }
            }
            
            
            if (latitude != null && longitude != null) {
                
                locationPhoneEntity.setId(uri);
                
                locationPhoneEntity.setPosition(Double.parseDouble(latitude), Double.parseDouble(longitude));
                locationPhoneEntity.setDatasource(false, "http://set.organicity.eu");
                try {
                    final OrionContextElement entity = locationPhoneEntity.getContextElement();
                    //post to local orion
                    orionClient.postContextEntity(uri, entity);
                    //get from local orion
                    final HttpHeaders headers = new HttpHeaders();
                    headers.add("Fiware-Service", orionService);
                    headers.add("Fiware-ServicePath", orionServicePath);
                    final HttpEntity r1 = new HttpEntity(headers);
                    
                    String formattedEntity = restTemplate.exchange(orionUrl + "v2/entities/" + uri, HttpMethod.GET, r1, String.class).getBody();
                    LOGGER.info(formattedEntity);
                    //Uses the Anon-user offline-token to get a new access token every time.
                    //offline-tokens expire after 30 days of inactivity.
                    String token = updateAccessToken();
                    postProxy(formattedEntity, token, experiment.getOcExperimentId(), experiment.getExperimentId());
                    
                    
                    //                    LOGGER.info(res.replaceAll("\n", ""));
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } else {
                LOGGER.warn("latitude:" + latitude + " longitude:" + longitude);
            }
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
    }
    
    @Async
    public void store(NewAssetDTO newAssetDTO) {
        
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            
            Experiment experiment = experimentRepository.findByExperimentId(newAssetDTO.getExperimentId());
            
            JSONObject object = new JSONObject();
            final String uri = String.format(ORION_SMARTPHONE_EXPERIMENT_ID_FORMAT, experiment.getUserId(), experiment.getOcExperimentId(), newAssetDTO.getName());
            object.put("id", uri);
            object.put("type", newAssetDTO.getType());
            
            JSONObject locationJson = new JSONObject();
            locationJson.put("type", "geo:point");
            locationJson.put("value", newAssetDTO.getLatitude() + ", " + newAssetDTO.getLongitude());
            object.put("location", locationJson);
            
            JSONObject timeInstantJson = new JSONObject();
            timeInstantJson.put("type", "urn:oc:attributeType:ISO8601");
            timeInstantJson.put("value", df.format(new Date()));
            object.put("TimeInstant", timeInstantJson);
            
            //Uses the Anon-user offline-token to get a new access token every time.
            //offline-tokens expire after 30 days of inactivity.
            String token = updateAccessToken();
            postProxy(object.toString(), token, experiment.getOcExperimentId(), experiment.getExperimentId());
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    private String updateAccessToken() {
        return updateAccessToken(offlineToken, encodedToken);
    }
    
    public String updateAccessToken(final String offlineToken, final String encodedToken) {
        LOGGER.info("updateAccessToken()");
        
        MultiValueMap<String, String> codeMap = new LinkedMultiValueMap<>();
        codeMap.add("grant_type", "refresh_token");
        codeMap.add("refresh_token", offlineToken);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedToken);
        LOGGER.info(HttpHeaders.AUTHORIZATION + " Basic " + encodedToken);
        LOGGER.info("offlineToken:" + offlineToken);
        
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        final HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(codeMap, headers);
        
        final ResponseEntity<OauthTokenResponse> response = restTemplate.exchange(ACCOUNTS_TOKEN_ENDPOINT, HttpMethod.POST, req, OauthTokenResponse.class);
        LOGGER.info("OauthTokenResponse:" + response.toString());
        if (response.hasBody()) {
            LOGGER.info("response.hasBody:" + response.hasBody());
            OauthTokenResponse credentials = response.getBody();
            LOGGER.info("response.body:" + response.getBody());
            LOGGER.info(credentials);
            LOGGER.info("response.body.access_token:" + credentials.getAccess_token());
            
            return credentials.getAccess_token();
        }
        return null;
    }
    
    private String postProxy(final String entity, final String accessToken, final String expId, final String appId) {
        
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add("X-Organicity-Application", appId);
        headers.add("X-Organicity-Experiment", expId);
        LOGGER.info("Bearer " + accessToken);
        LOGGER.info("X-Organicity-Application: " + appId);
        LOGGER.info("X-Organicity-Experiment: " + expId);
        
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT, "application/json");
        
        return restTemplate.exchange(EXP_PROXY_ENDPOINT, HttpMethod.POST, new HttpEntity<>(entity, headers), String.class).getBody();
        
    }
}
