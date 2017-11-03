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
import gr.cti.android.experimentation.model.Badge;
import gr.cti.android.experimentation.model.BadgeDTO;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Measurement;
import gr.cti.android.experimentation.model.Smartphone;
import gr.cti.android.experimentation.model.SmartphoneDTO;
import gr.cti.android.experimentation.model.SmartphoneStatisticsDTO;
import gr.cti.android.experimentation.model.UsageEntry;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class SmartphoneController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartphoneController.class);


    /**
     * Registers a {@see Smartphone} to the service.
     *
     * @return the phoneId generated or -1 if there was a error.
     */
    @ApiOperation(value = "Register Smartphone")
    @RequestMapping(value = "/smartphone", method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Smartphone.class)})
    public SmartphoneDTO registerSmartphone(@RequestBody SmartphoneDTO smartphoneDTO) {
        try {
            LOGGER.info(smartphoneDTO.toString());
            Smartphone smartphone = newSmartphone(smartphoneDTO);
            smartphone = modelService.registerSmartphone(smartphone);
            LOGGER.info("register Smartphone: Device:" + smartphone.getId());
            LOGGER.info("register Smartphone: Device Sensor Rules:" + smartphone.getSensorsRules());
            LOGGER.info("register Smartphone: Device Type:" + smartphone.getDeviceType());
            LOGGER.info("----------------------.-------------");
            return newSmartphoneDTO(smartphone);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.debug(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/smartphone/{smartphoneId}/statistics", method = RequestMethod.GET)
    public SmartphoneStatisticsDTO getTotalSmartphoneStatistics(
            Principal principal,
            @PathVariable(value = "smartphoneId") final int smartphoneId) {
        LOGGER.info("GET /smartphone/" + smartphoneId + "/statistics " + principal);
        return
                getExperimentSmartphoneStatistics(principal, smartphoneId, null);
    }

    @RequestMapping(value = "/smartphone/{smartphoneId}/statistics/{experimentId}", method = RequestMethod.GET)
    public SmartphoneStatisticsDTO getExperimentSmartphoneStatistics(
            Principal principal, @PathVariable(value = "smartphoneId") final long smartphoneId,
            @PathVariable(value = "experimentId") final String experimentId) {
        LOGGER.info("GET /smartphone/" + smartphoneId + "/statistics/" + experimentId + " " + principal);
        final Smartphone smartphone = smartphoneRepository.findById((int) smartphoneId);
        if (smartphone != null) {
            final SmartphoneStatisticsDTO smartphoneStatistics = new SmartphoneStatisticsDTO((int) smartphoneId);
            smartphoneStatistics.setSensorRules(smartphone.getSensorsRules());
            smartphoneStatistics.setReadings(measurementRepository.countByDeviceId(smartphone.getId()));
            if (experimentId != null) {
                final Experiment exp = experimentRepository.findByExperimentId(experimentId);
                smartphoneStatistics.setExperimentReadings(measurementRepository.countByDeviceIdAndExperimentId(smartphone.getId(), experimentId));
            } else {
                smartphoneStatistics.setExperimentReadings(0);
            }

            final Set<Measurement> experimentsTotal = new HashSet<>();
            final Set<String> experimentIdsTotal = new HashSet<>();
            experimentsTotal.addAll(measurementRepository.findDistinctExperimentIdByDeviceId(smartphone.getId()));
            experimentIdsTotal.addAll(experimentsTotal.stream().map(Measurement::getExperimentId).collect(Collectors.toList()));
            smartphoneStatistics.setExperiments(experimentIdsTotal.size());

            smartphoneStatistics.setLast7Days(getLast7DaysTotalReadings(smartphone));
            if (experimentId != null) {
                final Experiment exp = experimentRepository.findByExperimentId(experimentId);
                smartphoneStatistics.setExperimentRankings(getRankingList("", experimentId));
                smartphoneStatistics.setExperimentBadges(newBadgeDTOSet(badgeRepository.findByExperimentIdAndDeviceId(exp.getId(), smartphone.getId())));
                smartphoneStatistics.setExperimentUsage(getExperimentParticipationTime(experimentId, (int) smartphoneId));
            }
            smartphoneStatistics.setBadges(newBadgeDTOSet(badgeRepository.findByDeviceId(smartphone.getId())));
            smartphoneStatistics.setRankings(getRankingList("", null));
            smartphoneStatistics.setUsage(getExperimentParticipationTime(null, (int) smartphoneId));


            return smartphoneStatistics;
        }
        return null;
    }

    private Set<BadgeDTO> newBadgeDTOSet(Set<Badge> byExperimentIdAndDeviceId) {
        final HashSet<BadgeDTO> badgeSet = new HashSet<BadgeDTO>();
        for (final Badge badge : byExperimentIdAndDeviceId) {
            badgeSet.add(newBadgeDTO(badge));
        }
        return badgeSet;
    }

    private BadgeDTO newBadgeDTO(Badge badge) {
        BadgeDTO badgeDTO = new BadgeDTO();
        badgeDTO.setId(badge.getId());
        badgeDTO.setExperimentId(badge.getExperimentId());
        badgeDTO.setDeviceId(badge.getDeviceId());
        badgeDTO.setMessage(badge.getMessage());
        badgeDTO.setTimestamp(badge.getTimestamp());
        return badgeDTO;
    }

    @RequestMapping(value = "/smartphone/{smartphoneId}/badge", method = RequestMethod.GET)
    public Set<Badge> getSmartphoneBadges(
            @PathVariable(value = "smartphoneId") final int smartphoneId) {
        return getExperimentSmartphoneBadges(smartphoneId, 0);
    }

    @RequestMapping(value = "/smartphone/{smartphoneId}/badge/{experimentId}", method = RequestMethod.GET)
    public Set<Badge> getExperimentSmartphoneBadges(
            @PathVariable(value = "smartphoneId") final int smartphoneId, @PathVariable(value = "experimentId") final int experimentId) {
        if (experimentId == 0) {
            return badgeRepository.findByDeviceId(smartphoneId);
        } else {
            return badgeRepository.findByExperimentIdAndDeviceId(experimentId, smartphoneId);
        }
    }

    @RequestMapping(value = "/smartphone/{smartphoneId}/time", method = RequestMethod.GET)
    public TreeSet<UsageEntry> getExperimentParticipationTime(
            @PathVariable(value = "smartphoneId") final int smartphoneId) {
        return getExperimentParticipationTime(null, smartphoneId);
    }

    @RequestMapping(value = "/smartphone/{smartphoneId}/time/{experimentId}", method = RequestMethod.GET)
    public TreeSet<UsageEntry> getExperimentParticipationTime(
            @PathVariable(value = "experimentId") final String experimentId, @PathVariable(value = "smartphoneId") final int smartphoneId) {
        if (experimentId == null) {
            final Set<Measurement> results = measurementRepository.findByDeviceIdAndTimestampAfterOrderByTimestampAsc(smartphoneId, 0);
            return extractUsageTimes(results);
        } else {
            final Set<Measurement> results = measurementRepository.findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(experimentId, smartphoneId, 0);
            return extractUsageTimes(results);
        }
    }

    private TreeSet<UsageEntry> extractUsageTimes(final Set<Measurement> results) {
        final Map<String, Long> res = new TreeMap<>();
        final SortedSet<Long> timestamps = results.stream().map(Measurement::getTimestamp).collect(Collectors.toCollection(TreeSet::new));
        DateTime start = null;
        DateTime lastDay = null;
        for (final Long timestamp : timestamps) {
            final DateTime curDateTime = new DateTime(timestamp);
            if (start == null) {
                start = new DateTime(timestamp);
            } else {
                if (start.withMillisOfDay(0).getMillis() == curDateTime.withMillisOfDay(0).getMillis()) {
                    lastDay = new DateTime(timestamp);
                } else {
                    if (lastDay != null) {
                        long diff = (lastDay.getMillis() - start.getMillis()) / 1000 / 60;
                        res.put(dfDay.format(lastDay.withMillisOfDay(0).getMillis()), diff);
                    }
                    start = null;
                    lastDay = null;
                }
            }
        }

        return res.keySet().stream().map(dateKey -> new UsageEntry(dateKey, res.get(dateKey))).collect(Collectors.toCollection(TreeSet::new));
    }

    private Map<Long, Long> getLast7DaysTotalReadings(final Smartphone smartphone) {
        //Readings in the past 7 days
        final DateTime date = new DateTime().withMillisOfDay(0);
        final Set<Measurement> results = measurementRepository.findByDeviceIdAndTimestampAfter(smartphone.getId(), date.minusDays(7).getMillis());

        return extractCounters(results, date);
    }
}
