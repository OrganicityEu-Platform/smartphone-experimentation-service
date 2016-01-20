package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.controller.api.AndroidExperimentationWS;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.BaseExperiment;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Plugin;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.PluginRepository;
import gr.cti.android.experimentation.service.ModelManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class ExperimentController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(ExperimentController.class);
    @Value("${plugins.dir}")
    String pluginsDir;

    @Autowired
    ModelManager modelManager;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    PluginRepository pluginRepository;

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment", method = RequestMethod.GET, produces = "application/json")
    public List<Experiment> getExperiment(@RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId) {
        try {
            if (phoneId == AndroidExperimentationWS.LIDIA_PHONE_ID || phoneId == AndroidExperimentationWS.MYLONAS_PHONE_ID) {
                ArrayList<Experiment> experiements = new ArrayList<>();
                experiements.add(experimentRepository.findById(7));
                return experiements;
            } else {
                return modelManager.getEnabledExperiments();
            }
//            } else {
//
//                final Smartphone smartphone = smartphoneRepository.findById(phoneId);
//                Experiment experiment = modelManager.getExperiment(smartphone);
//                if (phoneId == LIDIA_PHONE_ID) {
//                    experiment = experimentRepository.findById(7);
//                }
//                LOGGER.debug("getExperiment: Device:" + phoneId);
//                LOGGER.debug("getExperiment:" + experiment);
//                LOGGER.debug("-----------------------------------");
//                final ArrayList<Experiment> list = new ArrayList<Experiment>();
//                list.add(experiment);
//                return list;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug(e.getMessage());
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment/{experimentId}", method = RequestMethod.GET, produces = "application/json")
    public ApiResponse getExperiment(HttpServletResponse response,
                                     @PathVariable(value = "experimentId") final int experimentId) throws IOException {

        final ApiResponse apiResponse = new ApiResponse();

        final Experiment storedExperiment = experimentRepository.findById(experimentId);
        if (storedExperiment != null) {
            LOGGER.info("getExperiment: " + storedExperiment);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(storedExperiment);
            return apiResponse;
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "no experiment found with the given id");
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment", method = RequestMethod.POST, produces = "application/json")
    public ApiResponse addExperiment(HttpServletResponse response, @ModelAttribute BaseExperiment experiment) throws IOException {
        final ApiResponse apiResponse = new ApiResponse();
        LOGGER.info("addExperiment " + experiment);
        if (experiment.getName() == null
                || experiment.getDescription() == null
                || experiment.getUrlDescription() == null
                || experiment.getFilename() == null
                || experiment.getSensorDependencies() == null
                || experiment.getUserId() == null
                ) {
            LOGGER.info("wrong data: " + experiment);
            String errorMessage = "error";
            if (experiment.getName() == null) {
                errorMessage = "name cannot be null";
            } else if (experiment.getDescription() == null) {
                errorMessage = "description cannot be null";
            } else if (experiment.getUrlDescription() == null) {
                errorMessage = "urlDescription cannot be null";
            } else if (experiment.getFilename() == null) {
                errorMessage = "filename cannot be null";
            } else if (experiment.getSensorDependencies() == null) {
                errorMessage = "sensorDependencies cannot be null";
            } else if (experiment.getUserId() == null) {
                errorMessage = "userId cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            final Set<Experiment> existingExperiments = experimentRepository.findByNameAndUserId(experiment.getName(), experiment.getUserId());
            if (existingExperiments.isEmpty()) {
                Experiment experimentObj = new Experiment();
                experimentObj.setName(experiment.getName());
                experimentObj.setDescription(experiment.getDescription());
                experimentObj.setUrlDescription(experiment.getUrlDescription());
                experimentObj.setFilename(experiment.getFilename());
                experimentObj.setSensorDependencies(experiment.getSensorDependencies());
                experimentObj.setUserId(experiment.getUserId());
                LOGGER.info("addExperiment: " + experiment);
                //setInstall Url
                experimentObj.setUrl("http://195.220.224.231:8080/dynamixRepository/" + experimentObj.getFilename());
                experimentObj.setEnabled(false);
                experimentObj.setTimestamp(System.currentTimeMillis());
                experimentRepository.save(experimentObj);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(experiment);
                return apiResponse;
            } else {
                LOGGER.info("experiment exists: " + experiment);
                response.sendError(HttpServletResponse.SC_CONFLICT, "an experiment with this name already exists for this user");
            }
        }
        return null;
    }
}
