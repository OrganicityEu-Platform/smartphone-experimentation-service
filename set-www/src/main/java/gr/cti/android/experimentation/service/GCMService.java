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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cti.android.experimentation.GcmMessageData;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ResultRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides connection to the gcm service for sending messages to the clients.
 */
@Service
public class GCMService {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GCMService.class);

    @Value("${gcm.key}")
    private String gcmKey;

    @Autowired
    ResultRepository resultRepository;
    @Autowired
    BadgeService badgeService;

    public String send2Experiment(int experiment, String message) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("message", message);
        return send2Topic("/topics/experiment-" + experiment, dataMap);
    }

    public String send2Device(int deviceId, String message) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("message", message);
        return send2Topic("/topics/device-" + deviceId, dataMap);
    }

    private String send2Topic(String topic, Map<String, String> dataMap) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("data", dataMap);
        payloadMap.put("to", topic);
        return post(payloadMap);
    }


    private String post(final Map<String, Object> message) {
        Entity entity;
        try {
            entity = Entity.json(new ObjectMapper().writeValueAsString(message));

            Response response = ClientBuilder.newClient()
                    .target("https://fcm.googleapis.com")
                    .path("gcm/send")
                    .request()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "key=" + gcmKey)
                    .post(entity);
            Response.Status.Family statusFamily = response.getStatusInfo().getFamily();
            if (statusFamily == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(String.class);
            } else {
                return response.getStatusInfo().getReasonPhrase();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Async
    public void check(final Result newResult) throws JsonProcessingException {

        long total = resultRepository.countByDeviceIdAndTimestampAfter(newResult.getDeviceId(),
                new DateTime().withMillisOfDay(0).getMillis());

        LOGGER.info("Total measurements : " + total + " device: " + newResult.getDeviceId());

        if (total == 200) {
            GcmMessageData data = new GcmMessageData();
            data.setType("encourage");
            data.setCount((int) total);
            send2Device(newResult.getDeviceId(), new ObjectMapper().writeValueAsString(data));

            badgeService.addBadge(newResult.getExperimentId(), newResult.getDeviceId(), "200 daily measurements");
        } else if (total == 1000) {
            GcmMessageData data = new GcmMessageData();
            data.setType("encourage");
            data.setCount((int) total);
            send2Device(newResult.getDeviceId(), new ObjectMapper().writeValueAsString(data));

            badgeService.addBadge(newResult.getExperimentId(), newResult.getDeviceId(), "1000 daily measurements");
        } else if (total == 10000) {
            GcmMessageData data = new GcmMessageData();
            data.setType("encourage");
            data.setCount((int) total);
            send2Device(newResult.getDeviceId(), new ObjectMapper().writeValueAsString(data));

            badgeService.addBadge(newResult.getExperimentId(), newResult.getDeviceId(), "10000 daily measurements");
        }
    }

}
