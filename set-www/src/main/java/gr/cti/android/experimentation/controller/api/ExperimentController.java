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
import gr.cti.android.experimentation.GcmMessageData;
import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.exception.ExperimentNotFoundException;
import gr.cti.android.experimentation.exception.NotAuthorizedException;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.ExperimentDTO;
import gr.cti.android.experimentation.model.ExperimentListDTO;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Set;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class ExperimentController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(ExperimentController.class);

    /**
     * Sends a message using GCM to all volunteers participating in a specific {@see Experiment}.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experimentId the Id of the {@see Experiment}.
     * @param message      the message to send to the volunteers.
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/contact/experiment", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public String contactExperiment(Principal principal, final HttpServletResponse response,
                                    @RequestParam(value = "experimentId") final String experimentId,
                                    @RequestParam(value = "message") final String message) throws JSONException, ExperimentNotFoundException {
        LOGGER.info("POST /contact/experiment" + " " + principal);

        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        if (experiment == null || !isExperimentOfUser(experiment, principal)) {
            throw new ExperimentNotFoundException();
        }
        try {
            gcmService.send2Experiment(experiment.getId(), message);
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
    @RequestMapping(value = "/contact/smartphone", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public String contactSmartphone(Principal principal, final HttpServletResponse response,
                                    @RequestParam(value = "smartphoneId") final String smartphoneId,
                                    @RequestParam(value = "message") final String message) throws JSONException {
        LOGGER.info("POST /contact/smartphone" + " " + principal);

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
    @RequestMapping(value = "/experiment", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public ExperimentListDTO listExperiments(
            Principal principal,
            @RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId) {
        LOGGER.info("GET /experiment " + principal);
        final ExperimentListDTO experiments = new ExperimentListDTO();
        experiments.setExperiments(new ArrayList<>());
        final Iterable<Experiment> enabledExperiments = experimentRepository.findAll();
        for (Experiment enabledExperiment : enabledExperiments) {
            if (principal != null && !enabledExperiment.getUserId().equals(principal.getName())) {
                continue;
            }
            experiments.getExperiments().add(newExperimentDTO(enabledExperiment));
        }
        return experiments;
    }

    /**
     * Lists all available and enabled {@see Experiment}.
     *
     * @param phoneId the Id of the {@see Smartphone} that will be used.
     * @return A list of {@see Experiment}.
     */
    @RequestMapping(value = "/experiment/live", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public ExperimentListDTO listLiveExperiments(Principal principal,
                                                 @RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId) {
        LOGGER.info("GET /experiment " + pluginRepository);
        final ExperimentListDTO experiments = new ExperimentListDTO();
        experiments.setExperiments(new ArrayList<>());
        final Iterable<Experiment> enabledExperiments = experimentRepository.findAll();
        for (Experiment experiment : enabledExperiments) {
            if (experiment.getEnabled()) {
                //check for live experiments
                experiments.getExperiments().add(newExperimentDTO(experiment));
            } else if (principal != null && experiment.getUserId() != null
                    && experiment.getUserId().contains(principal.getName())) {
                //check for under development experiments
                experiments.getExperiments().add(newExperimentDTO(experiment));
            }
        }
        return experiments;
    }

    /**
     * Returns the information of a specific {@see Experiment}.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experimentId the Id of the {@see Experiment}.
     * @return the information of a specific {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public ExperimentDTO getExperiment(Principal principal, HttpServletResponse response,
                                       @PathVariable(value = "experimentId") final String experimentId)
            throws IOException {
        LOGGER.info("GET /experiment/" + experimentId + " " + principal);

        final Experiment storedExperiment = experimentRepository.findByExperimentId(experimentId);
        if (storedExperiment != null) {
            return newExperimentDTO(storedExperiment);
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
    @RequestMapping(value = "/experiment", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public ApiResponse addExperiment(Principal principal, HttpServletResponse response, @ModelAttribute ExperimentDTO experiment)
            throws IOException, NotAuthorizedException {
        LOGGER.info("POST /experiment " + principal);

        if (principal == null) {
            throw new NotAuthorizedException();
        }

        final ApiResponse apiResponse = new ApiResponse();
        LOGGER.info("addExperiment " + experiment);
        if (experiment.getName() == null
                || experiment.getId() == null
                || experiment.getDescription() == null
                || experiment.getUrlDescription() == null
//                || experiment.getUrl() == null
//                || experiment.getFilename() == null
//                || experiment.getSensorDependencies() == null
                || experiment.getUserId() == null
                ) {
            LOGGER.info("wrong data: " + experiment);
            String errorMessage = "error";
            if (experiment.getId() == null) {
                errorMessage = "experiment id cannot be null";
            } else if (experiment.getName() == null) {
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
                experimentObj.setExperimentId(experiment.getId());
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
                experimentObj.setUrlDescription(experiment.getUrlDescription());
                experimentObj.setUserId(principal.getName());

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
    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public ApiResponse updateExperiment(Principal principal, HttpServletResponse response,
                                        @ModelAttribute @RequestBody final ExperimentDTO experiment,

                                        @PathVariable("experimentId") final String experimentId) throws IOException, NotAuthorizedException, ExperimentNotFoundException {
        LOGGER.info("POST /experiment/" + experimentId + " " + principal);

        if (principal == null) {
            throw new NotAuthorizedException();
        }

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
        final Experiment storedExperiment = experimentRepository.findByExperimentId(experimentId);
        if (storedExperiment != null) {
            if (!isExperimentOfUser(storedExperiment, principal)) {
                throw new ExperimentNotFoundException();
            }

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
            if (experiment.getId() != null) {
                storedExperiment.setExperimentId(experiment.getId());
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
            throw new ExperimentNotFoundException();
        }
    }

    /**
     * Removes an Experiment from the system.
     *
     * @param response     the {@see HttpServletResponse}.
     * @param experimentId the Id of the {@see Experiment}.
     * @return the removed {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    public ApiResponse deleteExperiment(Principal principal, HttpServletResponse response,
                                        @PathVariable(value = "experimentId") final String experimentId)
            throws IOException, NotAuthorizedException, ExperimentNotFoundException {
        LOGGER.info("DELETE /experiment/" + experimentId + " " + principal);

        if (principal == null) {
            throw new NotAuthorizedException();
        }
        final ApiResponse apiResponse = new ApiResponse();
        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            if (!isExperimentOfUser(experiment, principal)) {
                throw new ExperimentNotFoundException();
            }
            experimentRepository.delete(experiment);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(experiment);
            return apiResponse;
        }
    }

}
