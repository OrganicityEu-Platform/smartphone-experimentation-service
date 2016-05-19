package gr.cti.android.experimentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/api/v1")
public class SmartphoneController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(SmartphoneController.class);


    /**
     * Registers a {@see Smartphone} to the service.
     *
     * @return the phoneId generated or -1 if there was a error.
     */
    @ApiOperation(value = "Register Smartphone")
    @RequestMapping(value = "/smartphone", method = RequestMethod.POST, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Smartphone.class)})
    @ResponseBody
    public Smartphone registerSmartphone(@RequestBody String smartphoneString) {
        try {
            LOGGER.info(smartphoneString);
            Smartphone smartphone = new ObjectMapper().readValue(smartphoneString, Smartphone.class);
            smartphone = modelService.registerSmartphone(smartphone);
            LOGGER.info("register Smartphone: Device:" + smartphone.getId());
            LOGGER.info("register Smartphone: Device Sensor Rules:" + smartphone.getSensorsRules());
            LOGGER.info("register Smartphone: Device Type:" + smartphone.getDeviceType());
            LOGGER.info("----------------------.-------------");
            return smartphone;
        } catch (Exception e) {
            LOGGER.error(e, e);
            LOGGER.debug(e.getMessage());
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/smartphone/{smartphoneId}/statistics", method = RequestMethod.GET)
    public SmartphoneStatistics getTotalSmartphoneStatistics(
            @PathVariable(value = "smartphoneId") final int smartphoneId) {
        return getExperimentSmartphoneStatistics(smartphoneId, 0);
    }

    @ResponseBody
    @RequestMapping(value = "/smartphone/{smartphoneId}/statistics/{experimentId}", method = RequestMethod.GET)
    public SmartphoneStatistics getExperimentSmartphoneStatistics(
            @PathVariable(value = "smartphoneId") final int smartphoneId, @PathVariable(value = "experimentId") final int experimentId) {

        final Smartphone smartphone = smartphoneRepository.findById(smartphoneId);
        if (smartphone != null) {
            final SmartphoneStatistics smartphoneStatistics = new SmartphoneStatistics(smartphoneId);
            smartphoneStatistics.setSensorRules(smartphone.getSensorsRules());
            smartphoneStatistics.setReadings(resultRepository.countByDeviceId(smartphone.getId()));
            if (experimentId != 0) {
                smartphoneStatistics.setExperimentReadings(resultRepository.countByDeviceIdAndExperimentId(smartphone.getId(), experimentId));
            } else {
                smartphoneStatistics.setExperimentReadings(0);
            }

            final Set<Result> experimentsTotal = new HashSet<>();
            final Set<Integer> experimentIdsTotal = new HashSet<>();
            experimentsTotal.addAll(resultRepository.findDistinctExperimentIdByDeviceId(smartphone.getId()));
            experimentIdsTotal.addAll(experimentsTotal.stream().map(Result::getExperimentId).collect(Collectors.toList()));
            smartphoneStatistics.setExperiments(experimentIdsTotal.size());

            smartphoneStatistics.setLast7Days(getLast7DaysTotalReadings(smartphone));
            if (experimentId != 0) {
                smartphoneStatistics.setExperimentRankings(getRankingList("", experimentId));
                smartphoneStatistics.setExperimentBadges(badgeRepository.findByExperimentIdAndDeviceId(experimentId, smartphone.getId()));
                smartphoneStatistics.setExperimentUsage(getExperimentParticipationTime(experimentId, smartphoneId));
            }
            smartphoneStatistics.setBadges(badgeRepository.findByDeviceId(smartphone.getId()));
            smartphoneStatistics.setRankings(getRankingList("", 0));
            smartphoneStatistics.setUsage(getExperimentParticipationTime(0, smartphoneId));


            return smartphoneStatistics;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/smartphone/{smartphoneId}/badge", method = RequestMethod.GET)
    public Set<Badge> getSmartphoneBadges(
            @PathVariable(value = "smartphoneId") final int smartphoneId) {
        return getExperimentSmartphoneBadges(smartphoneId, 0);
    }

    @ResponseBody
    @RequestMapping(value = "/smartphone/{smartphoneId}/badge/{experimentId}", method = RequestMethod.GET)
    public Set<Badge> getExperimentSmartphoneBadges(
            @PathVariable(value = "smartphoneId") final int smartphoneId, @PathVariable(value = "experimentId") final int experimentId) {
        if (experimentId == 0) {
            return badgeRepository.findByDeviceId(smartphoneId);
        } else {
            return badgeRepository.findByExperimentIdAndDeviceId(experimentId, smartphoneId);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/smartphone/{smartphoneId}/time", method = RequestMethod.GET)
    public TreeSet<UsageEntry> getExperimentParticipationTime(
            @PathVariable(value = "smartphoneId") final int smartphoneId) {
        return getExperimentParticipationTime(0, smartphoneId);
    }

    @ResponseBody
    @RequestMapping(value = "/smartphone/{smartphoneId}/time/{experimentId}", method = RequestMethod.GET)
    public TreeSet<UsageEntry> getExperimentParticipationTime(
            @PathVariable(value = "experimentId") final int experimentId, @PathVariable(value = "smartphoneId") final int smartphoneId) {
        if (experimentId == 0) {
            final Set<Result> results = resultRepository.findByDeviceIdAndTimestampAfterOrderByTimestampAsc(smartphoneId, 0);
            return extractUsageTimes(results);
        } else {
            final Set<Result> results = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(experimentId, smartphoneId, 0);
            return extractUsageTimes(results);
        }
    }

    private TreeSet<UsageEntry> extractUsageTimes(final Set<Result> results) {
        final Map<String, Long> res = new TreeMap<>();
        final SortedSet<Long> timestamps = results.stream().map(Result::getTimestamp).collect(Collectors.toCollection(TreeSet::new));
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

        final TreeSet<UsageEntry> sortedUsageResults = new TreeSet<>();
        for (final String dateKey : res.keySet()) {
            sortedUsageResults.add(new UsageEntry(dateKey, res.get(dateKey)));
        }
        return sortedUsageResults;
    }

    private Map<Long, Long> getLast7DaysTotalReadings(final Smartphone smartphone) {
        //Readings in the past 7 days
        final DateTime date = new DateTime().withMillisOfDay(0);
        final Set<Result> results = resultRepository.findByDeviceIdAndTimestampAfter(smartphone.getId(), date.minusDays(7).getMillis());

        return extractCounters(results, date);
    }
}
