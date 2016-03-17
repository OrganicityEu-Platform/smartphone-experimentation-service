package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.controller.api.AndroidExperimentationWS;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.BaseExperiment;
import gr.cti.android.experimentation.model.Experiment;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class ExperimentController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(ExperimentController.class);
    private static final String EXPERIMENT_CONTEXT_TYPE = "org.ambientdynamix.contextplugins.ExperimentPlugin";


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
                || experiment.getUrl() == null
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
            } else if (experiment.getUrl() == null) {
                errorMessage = "url cannot be null";
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
                experimentObj.setUrlDescription(experiment.getUrl());
                experimentObj.setFilename(experiment.getFilename());
                experimentObj.setContextType(EXPERIMENT_CONTEXT_TYPE);
                experimentObj.setSensorDependencies(experiment.getSensorDependencies());
                experimentObj.setUserId(experiment.getUserId());
                experimentObj.setUrl(experiment.getUrl());
                experimentObj.setEnabled(true);
                LOGGER.info("addExperiment: " + experiment);
                experimentObj.setTimestamp(System.currentTimeMillis());
                experimentRepository.save(experimentObj);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(experimentObj);
                return apiResponse;
            } else {
                LOGGER.info("experiment exists: " + experiment);
                response.sendError(HttpServletResponse.SC_CONFLICT, "an experiment with this name already exists for this user");
            }
        }
        return null;
    }


    /**
     * Update an existing experiment.
     *
     * @param response     the HTTP response object.
     * @param experiment   the experiment object to update.
     * @param experimentId the id of the experiment to update.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment/{experimentId}", method = RequestMethod.POST, produces = "application/json")
    public Object updateExperiment(HttpServletResponse response, @ModelAttribute final BaseExperiment experiment, @PathVariable("experimentId") final long experimentId) throws IOException {

        final ApiResponse apiResponse = new ApiResponse();
        if (experiment.getName() == null
                || experiment.getDescription() == null
                || experiment.getUrlDescription() == null
//                || experiment.getUrl() == null
//                || experiment.getFilename() == null
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
//            } else if (experiment.getFilename() == null) {
//                errorMessage = "filename cannot be null";
//            } else if (experiment.getUrl() == null) {
//                errorMessage = "url cannot be null";
            } else if (experiment.getSensorDependencies() == null) {
                errorMessage = "sensorDependencies cannot be null";
            } else if (experiment.getUserId() == null) {
                errorMessage = "userId cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            final Experiment storedExperiment = experimentRepository.findById((int) experimentId);
            if (storedExperiment != null) {
                storedExperiment.setName(experiment.getName());
                storedExperiment.setDescription(experiment.getDescription());
                storedExperiment.setUrlDescription(experiment.getUrlDescription());
                if (experiment.getUrl() != null) {
                    storedExperiment.setUrl(experiment.getUrl());
                }
                if (experiment.getFilename() != null) {
                    storedExperiment.setFilename(experiment.getFilename());
                }
                storedExperiment.setContextType(EXPERIMENT_CONTEXT_TYPE);
                storedExperiment.setSensorDependencies(experiment.getSensorDependencies());
                storedExperiment.setUserId(experiment.getUserId());
                LOGGER.info("updateExperiment: " + experiment);
                //setInstall Url
                storedExperiment.setEnabled(false);
                storedExperiment.setTimestamp(System.currentTimeMillis());
                experimentRepository.save(storedExperiment);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(storedExperiment);
                return apiResponse;
            } else {
                LOGGER.error("experiment not found: " + experiment);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "an experiment with this id does not exist");
            }
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment/{experimentId}", method = RequestMethod.DELETE, produces = "application/json")
    public ApiResponse deleteExperiment(HttpServletResponse response,
                                        @PathVariable(value = "experimentId") final int experimentId) throws IOException {

        final ApiResponse apiResponse = new ApiResponse();
        final Experiment experiment = experimentRepository.findById(experimentId);
        if (experiment == null) {
            final String errorMessage = "no experiment found with this id";
            response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMessage);
        } else {
            experimentRepository.delete(experiment);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(experiment);
            return apiResponse;
        }
        return null;
    }

}