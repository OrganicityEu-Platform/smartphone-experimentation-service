package gr.cti.android.experimentation.controller;

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
import gr.cti.android.experimentation.GcmMessageData;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.BaseExperiment;
import gr.cti.android.experimentation.model.Experiment;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
@RequestMapping(value = {"/api/v1", "/v1"})
public class ExperimentController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(ExperimentController.class);

    /**
     * Sends a message using GCM to all volunteers participating in a specific {@see Experiment}.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experimentId the Id of the {@see Experiment}.
     * @param message      the message to send to the volunteers.
     * @return
     * @throws JSONException
     */
    @ResponseBody
    @RequestMapping(value = "/contact/experiment", method = RequestMethod.POST, produces = "application/json")
    public String contactExperiment(final HttpServletResponse response,
                                    @RequestParam(value = "experimentId") final String experimentId,
                                    @RequestParam(value = "message") final String message) throws JSONException {
        try {
            gcmService.send2Experiment(Integer.parseInt(experimentId), message);
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        return ok(response).toString();
    }

    /**
     * Sends a message using GCM to the volunteer participating with a specific {@see Smartphone}.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param smartphoneId the Id of the {@see Smartphone}.
     * @param message      the message to send to the volunteer.
     * @return
     * @throws JSONException
     */
    @ResponseBody
    @RequestMapping(value = "/contact/smartphone", method = RequestMethod.POST, produces = "application/json")
    public String contactSmartphone(final HttpServletResponse response,
                                    @RequestParam(value = "smartphoneId") final String smartphoneId,
                                    @RequestParam(value = "message") final String message) throws JSONException {
        final GcmMessageData data = new GcmMessageData();
        data.setType("text");
        data.setText(message);
        try {
            gcmService.send2Device(Integer.parseInt(smartphoneId), new ObjectMapper().writeValueAsString(data));
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        return ok(response).toString();
    }

    /**
     * Lists all available and enabled {@see Experiment}.
     *
     * @param phoneId the Id of the {@see Smartphone} that will be used.
     * @return A list of {@see Experiment}.
     */
    @ResponseBody
    @RequestMapping(value = "/experiment", method = RequestMethod.GET, produces = "application/json")
    public List<Experiment> listExperiments(@RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId) {
        return modelService.getEnabledExperiments();
    }

    /**
     * Returns the information of a specific {@see Experiment}.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experimentId the Id of the {@see Experiment}.
     * @return the information of a specific {@see Experiment}.
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.GET, produces = "application/json")
    public ApiResponse getExperiment(HttpServletResponse response,
                                     @PathVariable(value = "experimentId") final int experimentId)
            throws IOException {

        final ApiResponse apiResponse = new ApiResponse();

        final Experiment storedExperiment = experimentRepository.findById(experimentId);
        if (storedExperiment != null) {
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(storedExperiment);
            return apiResponse;
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "no experiment found with the given id");
        }
        return null;
    }

    /**
     * Adds a new {@see Experiment} to the system.
     *
     * @param response   the {@see HttpServletResponse}.
     * @param experiment the information about the {@see Experiment}.
     * @return the information of the {@see Experiment} added.
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/experiment", method = RequestMethod.POST, produces = "application/json")
    public ApiResponse addExperiment(HttpServletResponse response, @ModelAttribute BaseExperiment experiment)
            throws IOException {
        final ApiResponse apiResponse = new ApiResponse();
        LOGGER.info("addExperiment " + experiment);
        if (experiment.getName() == null
                || experiment.getDescription() == null
                || experiment.getUrlDescription() == null
//                || experiment.getUrl() == null
//                || experiment.getFilename() == null
//                || experiment.getSensorDependencies() == null
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
//            } else if (experiment.getSensorDependencies() == null) {
//                errorMessage = "sensorDependencies cannot be null";
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

                if (experiment.getFilename() != null && !experiment.getFilename().equals("")) {
                    experimentObj.setFilename(experiment.getFilename());
                }
                if (experiment.getUrl() != null && !experiment.getUrl().equals("")) {
                    experimentObj.setUrl(experiment.getUrl());
                }
                experimentObj.setContextType(EXPERIMENT_CONTEXT_TYPE);
                experimentObj.setSensorDependencies(experiment.getSensorDependencies());
                experimentObj.setUserId(experiment.getUserId());

                experimentObj.setEnabled(true);
                experimentObj.setStatus("1");
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
     * Update an existing {@see Experiment}.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experiment   the {@see Experiment} object to update.
     * @param experimentId the id of the {@see Experiment} to update.
     * @return the updated information of the {@see Experiment}.
     */
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.POST, produces = "application/json")
    public ApiResponse updateExperiment(HttpServletResponse response, @ModelAttribute final BaseExperiment experiment, @PathVariable("experimentId") final long experimentId) throws IOException {

        final ApiResponse apiResponse = new ApiResponse();
//        if (experiment.getName() == null
//                || experiment.getDescription() == null
//                || experiment.getUrlDescription() == null
//                || experiment.getUrl() == null
//                || experiment.getFilename() == null
//                || experiment.getSensorDependencies() == null
//                || experiment.getUserId() == null
//                ) {
//            LOGGER.info("wrong data: " + experiment);
//            String errorMessage = "error";
//            if (experiment.getName() == null) {
//                errorMessage = "name cannot be null";
//            } else if (experiment.getDescription() == null) {
//                errorMessage = "description cannot be null";
//            } else if (experiment.getUrlDescription() == null) {
//                errorMessage = "urlDescription cannot be null";
//            } else if (experiment.getFilename() == null) {
//                errorMessage = "filename cannot be null";
//            } else if (experiment.getUrl() == null) {
//                errorMessage = "url cannot be null";
//            } else if (experiment.getSensorDependencies() == null) {
//                errorMessage = "sensorDependencies cannot be null";
//            } else if (experiment.getUserId() == null) {
//                errorMessage = "userId cannot be null";
//            }
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
//        } else {
        final Experiment storedExperiment = experimentRepository.findById((int) experimentId);
        if (storedExperiment != null) {
            if (experiment.getName() != null) {
                storedExperiment.setName(experiment.getName());
            }
            if (experiment.getDescription() != null) {
                storedExperiment.setDescription(experiment.getDescription());
            }
            if (experiment.getUrlDescription() != null) {
                storedExperiment.setUrlDescription(experiment.getUrlDescription());
            }
            if (experiment.getFilename() != null && !experiment.getFilename().equals("")) {
                storedExperiment.setFilename(experiment.getFilename());
            }
            if (experiment.getUrl() != null && !experiment.getUrl().equals("")) {
                storedExperiment.setUrl(experiment.getUrl());
            }
            storedExperiment.setContextType(EXPERIMENT_CONTEXT_TYPE);
            if (experiment.getSensorDependencies() != null) {
                storedExperiment.setSensorDependencies(experiment.getSensorDependencies());
            }
            if (experiment.getUserId() != null) {
                storedExperiment.setUserId(experiment.getUserId());
            }
            LOGGER.info("updateExperiment: " + experiment);
            //setInstall Url
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
//        }
        return null;
    }

    /**
     * Removes an Experiment from the system.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experimentId the Id of the {@see Experiment}.
     * @return the removed {@see Experiment}.
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.DELETE, produces = "application/json")
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
