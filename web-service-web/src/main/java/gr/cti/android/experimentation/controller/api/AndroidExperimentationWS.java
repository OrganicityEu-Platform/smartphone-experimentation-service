package gr.cti.android.experimentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.organicity.entities.namespace.OrganicityAttributeTypes;
import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.Reading;
import gr.cti.android.experimentation.model.Report;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value = "/api/v1")
public class AndroidExperimentationWS extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(AndroidExperimentationWS.class);

    @ResponseBody
    @RequestMapping(value = "/data", method = RequestMethod.POST, produces = "application/json", consumes = "text/plain")
    public JSONObject saveExperiment(@RequestBody String body, final HttpServletResponse response) throws
            JSONException, IOException {
        LOGGER.info("saveExperiment Called");

        try {
            final Result newResult = extractResultFromBody(body);

            //store to sql
            try {
                sqlDbService.store(newResult);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }

            //store to influx
//        try {
//            boolean res = influxDbService.store(newResult);
//            LOGGER.info(res);
//        } catch (Exception e) {
//            LOGGER.error(e, e);
//        }

            //store to orion
            try {
                Experiment experiment = experimentRepository.findById(newResult.getExperimentId());
                orionService.store(newResult, experiment);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }

            //send incentive messages to phone
            try {
                gcmService.check(newResult);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        final JSONObject responseObje = new JSONObject();
        responseObje.put("status", "Ok");
        responseObje.put("code", 202);
        return responseObje;
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
        final Experiment experiment = experimentRepository.findById(Integer.parseInt(result.getJobName()));
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

    @ResponseBody
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public JSONObject ping(final String pingJson, final HttpServletResponse response) throws JSONException {
        LOGGER.debug("Ping:" + pingJson);
        LOGGER.debug("-----------------------------------");
        return ok(response);
    }

}
