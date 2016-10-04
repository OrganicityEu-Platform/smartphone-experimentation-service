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

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.exception.ExperimentNotFoundException;
import gr.cti.android.experimentation.exception.NotAuthorizedException;
import gr.cti.android.experimentation.exception.RegionNotFoundException;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Region;
import gr.cti.android.experimentation.model.RegionDTO;
import gr.cti.android.experimentation.model.RegionListDTO;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class RegionController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(RegionController.class);

    /**
     * Returns the {@see Region} information of a specific {@see Experiment}.
     *
     * @param experimentId the Id of the {@see Experiment}.
     * @return the {@see Region} entities of a specific {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}/region", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public RegionListDTO getExperimentRegions(@PathVariable(value = "experimentId") final String experimentId,
                                              Principal principal)
            throws IOException, ExperimentNotFoundException {
        LOGGER.info(String.format("GET /experiment/%s/region %s", experimentId, principal));

        final RegionListDTO list = new RegionListDTO();
        LOGGER.info("get regions for " + experimentId);
        list.setRegions(new ArrayList<>());
        LOGGER.info("Experiment:" + experimentId);
        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        LOGGER.error("Experiment:" + experiment);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            final Set<Region> regions = regionRepository.findByExperimentId(experimentId);
            for (final Region region : regions) {
                list.getRegions().add(newRegionDTO(region));
            }
        }
        return list;
    }


    /**
     * Adds a list of {@see Region} entities to a specific {@see Experiment}.
     *
     * @param experimentId  the Id of the {@see Experiment}.
     * @param regionListDTO a list of {@see RegionDTO} elements.
     * @return the {@see Region} entities of a specific {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}/region", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public RegionListDTO postRegion2Experiment(
            Principal principal, @PathVariable(value = "experimentId") final String experimentId,
            @RequestBody final RegionListDTO regionListDTO)
            throws IOException, NotAuthorizedException, ExperimentNotFoundException {
        LOGGER.info(String.format("POST /experiment/%s/region %s", experimentId, principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }

        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            if (!isExperimentOfUser(experiment, principal)) {
                throw new ExperimentNotFoundException();
            }

            int count = getExperimentRegions(experimentId, principal).getRegions().size() + 1;
            for (final RegionDTO regionDTO : regionListDTO.getRegions()) {
                final Region region = newRegion(regionDTO);
                if (region.getName() == null) {
                    region.setName("Region " + count);
                }
                region.setExperimentId(experiment.getExperimentId());
                regionRepository.save(region);
            }
        }

        return getExperimentRegions(experimentId, principal);
    }

    /**
     * Replace the list of {@see Region} entities of a specific {@see Experiment}.
     *
     * @param experimentId  the Id of the {@see Experiment}.
     * @param regionListDTO a list of {@see RegionDTO} elements.
     * @return the {@see Region} entities of a specific {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}/region", method = RequestMethod.PUT, produces = APPLICATION_JSON)
    public RegionListDTO putRegion2Experiment(Principal principal, @PathVariable(value = "experimentId") final String experimentId
            , @RequestBody final RegionListDTO regionListDTO)
            throws IOException, NotAuthorizedException, ExperimentNotFoundException {
        LOGGER.info(String.format("PUT /experiment/%s/region %s", experimentId, principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }

        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            if (!isExperimentOfUser(experiment, principal)) {
                throw new ExperimentNotFoundException();
            }

            //remove old regions
            final Set<Region> oldRegions = regionRepository.findByExperimentId(experimentId);
            regionRepository.delete(oldRegions);
            //add new regions
            int count = 0;
            for (final RegionDTO regionDTO : regionListDTO.getRegions()) {
                count++;
                final Region region = newRegion(regionDTO);
                if (region.getName() == null) {
                    region.setName("Region " + count);
                }
                region.setExperimentId(experiment.getExperimentId());
                regionRepository.save(region);
            }
        }

        return getExperimentRegions(experimentId, principal);
    }


    /**
     * Get a {@see Region} entity of a specific {@see Experiment}.
     *
     * @param experimentId the Id of the {@see Experiment}.
     * @param regionId     the Id of the {@see Region}.
     * @return the {@see RegionDTO} element.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}/region/{regionId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public RegionDTO getExperimentRegion(@PathVariable(value = "experimentId") final String experimentId
            , @PathVariable(value = "regionId") final int regionId, Principal principal)
            throws IOException, RegionNotFoundException, ExperimentNotFoundException {
        LOGGER.info(String.format("GET /experiment/%s/region/%s %s", experimentId, regionId, principal));

        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            final Region existingRegion = regionRepository.findById(regionId);
            if (existingRegion == null) {
                throw new RegionNotFoundException();
            }
            return newRegionDTO(existingRegion);
        }
    }

    /**
     * Updates a {@see Region} entity of a specific {@see Experiment}.
     *
     * @param experimentId the Id of the {@see Experiment}.
     * @param regionId     the Id of the {@see Region}.
     * @param regionDTO    an update {@see RegionDTO} element.
     * @return the {@see Region} entities of a specific {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}/region/{regionId}", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public RegionDTO postRegion2Experiment(Principal principal, @PathVariable(value = "experimentId") final String experimentId
            , @PathVariable(value = "regionId") final int regionId
            , @RequestBody final RegionDTO regionDTO)
            throws IOException, NotAuthorizedException, ExperimentNotFoundException, RegionNotFoundException {
        LOGGER.info(String.format("POST /experiment/%s/region/%s %s", experimentId, regionId, principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }
        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);

        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            if (!isExperimentOfUser(experiment, principal)) {
                throw new ExperimentNotFoundException();
            }

            Region existingRegion = regionRepository.findById(regionId);
            if (existingRegion == null) {
                throw new RegionNotFoundException();
            } else {
                final Region region = newRegion(regionDTO);
                LOGGER.info("Region: " + regionDTO);
                if (region.getCoordinates() != null) {
                    existingRegion.setCoordinates(region.getCoordinates());
                }
                if (region.getWeight() != null) {
                    existingRegion.setWeight(region.getWeight());
                }
                if (region.getStartDate() != null) {
                    existingRegion.setStartDate(region.getStartDate());
                }
                if (region.getEndDate() != null) {
                    existingRegion.setEndDate(region.getEndDate());
                }
                if (region.getStartTime() != null) {
                    existingRegion.setStartTime(region.getStartTime());
                }
                if (region.getEndTime() != null) {
                    existingRegion.setEndTime(region.getEndTime());
                }
                if (region.getName() != null) {
                    existingRegion.setName(region.getName());
                }
                if (region.getMaxMeasurements() != null) {
                    existingRegion.setMaxMeasurements(region.getMaxMeasurements());
                }
                if (region.getMinMeasurements() != null) {
                    existingRegion.setMinMeasurements(region.getMinMeasurements());
                }
                LOGGER.info("Region: " + existingRegion);
                regionRepository.save(existingRegion);
            }
        }

        return newRegionDTO(regionRepository.findById(regionId));
    }

    /**
     * Deletes {@see Region} entity of a specific {@see Experiment}.
     *
     * @param experimentId the Id of the {@see Experiment}.
     * @param regionId     the Id of the {@see Region}.
     * @return the {@see Region} entities of a specific {@see Experiment}.
     * @throws IOException
     */
    @RequestMapping(value = "/experiment/{experimentId}/region/{regionId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    public RegionListDTO postRegion2Experiment(Principal principal, @PathVariable(value = "experimentId") final String experimentId
            , @PathVariable(value = "regionId") final int regionId)
            throws IOException, NotAuthorizedException, ExperimentNotFoundException, RegionNotFoundException {
        LOGGER.info(String.format("DELETE /experiment/%s/region/%s %s", experimentId, regionId, principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }
        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        } else {
            if (!isExperimentOfUser(experiment, principal)) {
                throw new ExperimentNotFoundException();
            }
            Region region = regionRepository.findById(regionId);
            if (region == null) {
                throw new RegionNotFoundException();
            } else {
                regionRepository.delete(region);
            }
        }

        return getExperimentRegions(experimentId,principal);
    }

}
