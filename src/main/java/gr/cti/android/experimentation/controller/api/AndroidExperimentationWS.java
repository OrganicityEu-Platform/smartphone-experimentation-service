package gr.cti.android.experimentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.organicity.entities.namespace.OrganicityAttributeTypes;
import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.entities.Reading;
import gr.cti.android.experimentation.entities.Report;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import gr.cti.android.experimentation.service.*;
import gr.cti.android.experimentation.model.Plugin;
import gr.cti.android.experimentation.model.Smartphone;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public static final int LIDIA_PHONE_ID = 11;
    public static final int MYLONAS_PHONE_ID = -6;//6


    @Autowired
    ModelManager modelManager;
    @Autowired
    SmartphoneRepository smartphoneRepository;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    ResultRepository resultRepository;
    @Autowired
    InfluxDbService influxDbService;
    @Autowired
    SqlDbService sqlDbService;
    @Autowired
    OrionService orionService;
    @Autowired
    GCMService gcmService;
    @Autowired
    CityService cityService;
    @ResponseBody
    @RequestMapping(value = "/data", method = RequestMethod.POST, produces = "application/json", consumes = "text/plain")
    public JSONObject saveExperiment(@RequestBody String body, final HttpServletResponse response) throws
            JSONException, IOException {
        LOGGER.info("saveExperiment Called");

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
            orionService.store(newResult);
        } catch (Exception e) {
            LOGGER.error(e, e);
        }

        //send incentive messages to phone
        try {
            gcmService.check(newResult);
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
    @RequestMapping(value = "/statistics/{phoneId}", method = RequestMethod.GET, produces = "application/json")
    public Map<Long, Long> statisticsByPhone(@PathVariable("phoneId") final String phoneId,
                                             final HttpServletResponse response) {

        final Map<Long, Long> counters = new HashMap<>();
        for (long i = 0; i <= 7; i++) {
            counters.put(i, 0L);
        }

        final DateTime date = new DateTime().withMillisOfDay(0);
        final Set<Result> results = resultRepository.findByDeviceIdAndTimestampAfter(Integer.parseInt(phoneId), date.minusDays(7).getMillis());
        final Map<DateTime, Long> datecounters = new HashMap<>();
        for (final Result result : results) {
            final DateTime index = new DateTime(result.getTimestamp()).withMillisOfDay(0);
            if (!datecounters.containsKey(index)) {
                datecounters.put(index, 0L);
            }
            datecounters.put(index, datecounters.get(index) + 1);
        }

        for (final DateTime dateTime : datecounters.keySet()) {
            counters.put((date.getMillis() - dateTime.getMillis()) / 86400000, datecounters.get(dateTime));
        }
        return counters;
    }

    @ResponseBody
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public JSONObject ping(final String pingJson, final HttpServletResponse response) throws JSONException {
        LOGGER.debug("Ping:" + pingJson);
        LOGGER.debug("-----------------------------------");
        return ok(response);
    }

}
