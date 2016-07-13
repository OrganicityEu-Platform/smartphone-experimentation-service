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

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Region;
import gr.cti.android.experimentation.model.RegionDTO;
import gr.cti.android.experimentation.model.RegionListDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
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
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}/region", method = RequestMethod.GET, produces = "application/json")
    public RegionListDTO getExperimentRegions(@PathVariable(value = "experimentId") final int experimentId)
            throws IOException {
        final RegionListDTO list = new RegionListDTO();
        LOGGER.info("get regions for " + experimentId);
        list.setRegions(new ArrayList<>());

        final Experiment experiment = experimentRepository.findById(experimentId);
        if (experiment != null) {
            for (final Region region : regionRepository.findByExperimentId(experimentId)) {
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
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}/region", method = RequestMethod.POST, produces = "application/json")
    public RegionListDTO postRegion2Experiment(@PathVariable(value = "experimentId") final int experimentId
            , @RequestBody final RegionListDTO regionListDTO)
            throws IOException {

        final Experiment experiment = experimentRepository.findById(experimentId);
        if (experiment != null) {
            int count = getExperimentRegions(experimentId).getRegions().size() + 1;
            for (final RegionDTO regionDTO : regionListDTO.getRegions()) {
                final Region region = newRegion(regionDTO);
                if (region.getName() == null) {
                    region.setName("Region " + count);
                }
                region.setExperimentId(experimentId);
                region.setExperimentRegionId(experimentId);
                regionRepository.save(region);
            }
        }

        return getExperimentRegions(experimentId);
    }


    /**
     * Get a {@see Region} entity of a specific {@see Experiment}.
     *
     * @param experimentId the Id of the {@see Experiment}.
     * @param regionId     the Id of the {@see Region}.
     * @return the {@see RegionDTO} element.
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}/region/{regionId}", method = RequestMethod.GET, produces = "application/json")
    public RegionDTO getExperimentRegion(@PathVariable(value = "experimentId") final int experimentId
            , @PathVariable(value = "regionId") final int regionId)
            throws IOException {

//        final Experiment experiment = experimentRepository.findById(experimentId);
//
//        if (experiment != null) {
        Region existingRegion = regionRepository.findById(regionId);
        if (existingRegion != null) {
            return newRegionDTO(existingRegion);
        }
//        }
        return null;
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
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}/region/{regionId}", method = RequestMethod.POST, produces = "application/json")
    public RegionDTO postRegion2Experiment(@PathVariable(value = "experimentId") final int experimentId
            , @PathVariable(value = "regionId") final int regionId
            , @RequestBody final RegionDTO regionDTO)
            throws IOException {

        final Experiment experiment = experimentRepository.findById(experimentId);


        if (experiment != null) {
            Region existingRegion = regionRepository.findById(regionId);
            if (existingRegion != null) {
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
                existingRegion.setExperimentRegionId(experimentId);
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
    @ResponseBody
    @RequestMapping(value = "/experiment/{experimentId}/region/{regionId}", method = RequestMethod.DELETE, produces = "application/json")
    public RegionListDTO postRegion2Experiment(@PathVariable(value = "experimentId") final int experimentId
            , @PathVariable(value = "regionId") final int regionId)
            throws IOException {

        final Experiment experiment = experimentRepository.findById(experimentId);
        if (experiment != null) {
            Region region = regionRepository.findById(regionId);
            if (region != null) {
                regionRepository.delete(region);
            }
        }

        return getExperimentRegions(experimentId);
    }

}
