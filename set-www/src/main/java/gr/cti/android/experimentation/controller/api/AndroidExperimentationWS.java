package gr.cti.android.experimentation.controller.api;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.organicity.entities.namespace.OrganicityAttributeTypes;
import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.*;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class AndroidExperimentationWS extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(AndroidExperimentationWS.class);

    @RequestMapping(value = "/data", method = RequestMethod.POST, produces = APPLICATION_JSON, consumes = TEXT_PLAIN)
    public JSONObject data(@RequestBody String body, final HttpServletResponse response) throws
            JSONException, IOException {

        Result newResult = null;
        try {
            newResult = extractResultFromBody(body);
        } catch (Exception e) {
            LOGGER.error(e, e);

        }
        if (newResult != null) {
            //store to sql
            try {
                sqlDbService.store(newResult);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }

        }
        response.setStatus(HttpServletResponse.SC_OK);
        final JSONObject responseObject = new JSONObject();
        responseObject.put("status", "Ok");
        responseObject.put("code", 202);
        return responseObject;
    }

    @RequestMapping(value = "/data/multiple", method = RequestMethod.POST, produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    public ResponseDTO data(@RequestBody final ResultListDTO resultList, final HttpServletResponse response,
                            Principal principal) throws
            JSONException, IOException {
        LOGGER.info("POST /data/multiple " + principal);
        for (final Report resultDTO : resultList.getResultList()) {
            final Result result = newResult(resultDTO);

            if (result != null) {
                //store to sql
                try {
                    sqlDbService.store(result);
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);

        final ResponseDTO r = new ResponseDTO();
        r.setCode(202);
        r.setStatus("Ok");
        return r;
    }

    protected Result newResult(final Report dto) throws IOException, JSONException {

        LOGGER.info("saving for deviceId:" + dto.getDeviceId() + " jobName:" + dto.getJobName());
        final Smartphone phone = smartphoneRepository.findById(dto.getDeviceId());
        final Experiment experiment = experimentRepository.findByExperimentId(dto.getJobName());
        LOGGER.info("saving for PhoneId:" + phone.getPhoneId() + " ExperimentName:" + experiment.getName());

        final Result newResult = new Result();
        final JSONObject objTotal = new JSONObject();

        for (String jobResult : dto.getJobResults()) {
            jobResult = jobResult.replaceAll("atributeType", "attributeType")
                    .replaceAll("org.ambientdynamix.contextplugins.NoiseLevel", OrganicityAttributeTypes.Types.SOUND_PRESSURE_LEVEL.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.sound", OrganicityAttributeTypes.Types.SOUND_PRESSURE_LEVEL.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.10pm", OrganicityAttributeTypes.Types.PARTICLES10.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.25pm", OrganicityAttributeTypes.Types.PARTICLES25.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.co", OrganicityAttributeTypes.Types.CARBON_MONOXIDE.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.lpg", OrganicityAttributeTypes.Types.LPG.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.ch4", OrganicityAttributeTypes.Types.METHANE.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.temperature", OrganicityAttributeTypes.Types.TEMPERATURE.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.battery%", OrganicityAttributeTypes.Types.BATTERY_LEVEL.getUrn())
                    .replaceAll("org.ambientdynamix.contextplugins.batteryv", OrganicityAttributeTypes.Types.BATTERY_VOLTAGE.getUrn());

            LOGGER.info(jobResult);
            if (jobResult.isEmpty()) {
                continue;
            }
            final Reading readingObj = new ObjectMapper().readValue(jobResult, Reading.class);
            final String value = readingObj.getValue();
            final long readingTime = readingObj.getTimestamp();

            try {
                final Set<Result> res =
                        resultRepository.findByExperimentIdAndDeviceIdAndTimestampAndMessage(experiment.getId(), phone.getId(), readingTime, value);
                if (!res.isEmpty()) {
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
            LOGGER.info(jobResult);
            newResult.setDeviceId(phone.getId());
            newResult.setExperimentId(experiment.getId());
            final JSONObject obj = new JSONObject(value);
            for (final String key : JSONObject.getNames(obj)) {
                objTotal.put(key, obj.get(key));
            }
            newResult.setTimestamp(readingTime);
        }

        newResult.setMessage(objTotal.toString());

        LOGGER.info(newResult.toString());
        return newResult;
    }

    private Result extractResultFromBody(String body) throws JSONException, IOException {

        body = body.replaceAll("atributeType", "attributeType")
                .replaceAll("org.ambientdynamix.contextplugins.NoiseLevel", OrganicityAttributeTypes.Types.SOUND_PRESSURE_LEVEL.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.sound", OrganicityAttributeTypes.Types.SOUND_PRESSURE_LEVEL.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.10pm", OrganicityAttributeTypes.Types.PARTICLES10.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.25pm", OrganicityAttributeTypes.Types.PARTICLES25.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.co", OrganicityAttributeTypes.Types.CARBON_MONOXIDE.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.lpg", OrganicityAttributeTypes.Types.LPG.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.ch4", OrganicityAttributeTypes.Types.METHANE.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.temperature", OrganicityAttributeTypes.Types.TEMPERATURE.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.battery%", OrganicityAttributeTypes.Types.BATTERY_LEVEL.getUrn())
                .replaceAll("org.ambientdynamix.contextplugins.batteryv", OrganicityAttributeTypes.Types.BATTERY_VOLTAGE.getUrn());


        Report result = new ObjectMapper().readValue(body, Report.class);
        LOGGER.info("saving for deviceId:" + result.getDeviceId() + " jobName:" + result.getJobName());
        final Smartphone phone = smartphoneRepository.findById(result.getDeviceId());
        final Experiment experiment = experimentRepository.findByExperimentId(result.getJobName());
        LOGGER.info("saving for PhoneId:" + phone.getPhoneId() + " ExperimentName:" + experiment.getName());

        final Result newResult = new Result();
        final JSONObject objTotal = new JSONObject();

        for (final String jobResult : result.getJobResults()) {

            LOGGER.info(jobResult);
            if (jobResult.isEmpty()) {
                continue;
            }

            final Reading readingObj = new ObjectMapper().readValue(jobResult, Reading.class);
            final String value = readingObj.getValue();
            final long readingTime = readingObj.getTimestamp();

            try {
                final Set<Result> res =
                        resultRepository.findByExperimentIdAndDeviceIdAndTimestampAndMessage(experiment.getId(), phone.getId(), readingTime, value);
                if (!res.isEmpty()) {
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
            LOGGER.info(jobResult);
            newResult.setDeviceId(phone.getId());
            newResult.setExperimentId(experiment.getId());
            final JSONObject obj = new JSONObject(value);
            for (final String key : JSONObject.getNames(obj)) {
                objTotal.put(key, obj.get(key));
            }
            newResult.setTimestamp(readingTime);
        }

        newResult.setMessage(objTotal.toString());

        LOGGER.info(newResult.toString());
        return newResult;
    }
}
