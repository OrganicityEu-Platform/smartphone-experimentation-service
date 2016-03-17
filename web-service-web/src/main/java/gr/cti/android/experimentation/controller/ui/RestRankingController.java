package gr.cti.android.experimentation.controller.ui;

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
@RequestMapping(value = "/api/v1")
public class RestRankingController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(RestRankingController.class);


    private SimpleDateFormat dfTime;
    private SimpleDateFormat dfDay;

    @PostConstruct
    public void init() {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        dfTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        dfDay = new SimpleDateFormat("yyyy-MM-dd");
        dfTime.setTimeZone(tz);
        dfDay.setTimeZone(tz);
    }

    @Deprecated
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
    @RequestMapping(value = {"/ranking", "/rankings"}, method = RequestMethod.GET)
    public Set<RankingEntry> getRankings(
            @RequestParam(required = false, defaultValue = "") final String after,
            @RequestParam(required = false, defaultValue = "0") final int experimentId
    ) {
        return getRankingList(after, experimentId);
    }

    @ResponseBody
    @Deprecated
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String getRankings(
            @RequestParam(required = false, defaultValue = "0") final int deviceId) {

        final long resultsTotal = resultRepository.countByDeviceId(deviceId);
        final long resultsToday = resultRepository.countByDeviceIdAndTimestampAfter(deviceId, new DateTime().withMillisOfDay(0).getMillis());
        final Set<Result> experimentsTotal = new HashSet<>();
        final Set<Integer> experimentIdsTotal = new HashSet<>();
        experimentsTotal.addAll(resultRepository.findDistinctExperimentIdByDeviceId(deviceId));
        final Set<Result> experimentsToday = new HashSet<>();
        final Set<Integer> experimentsIdsToday = new HashSet<>();
        experimentsToday.addAll(resultRepository.findDistinctExperimentIdByDeviceIdAndTimestampAfter(deviceId, new DateTime().withMillisOfDay(0).getMillis()));

        experimentIdsTotal.addAll(experimentsTotal.stream().map(Result::getExperimentId).collect(Collectors.toList()));
        experimentsIdsToday.addAll(experimentsToday.stream().map(Result::getExperimentId).collect(Collectors.toList()));
        final JSONObject obj = new JSONObject();
        try {
            obj.put("resultsTotal", resultsTotal);
            obj.put("resultsToday", resultsToday);
            obj.put("experimentsTotal", experimentIdsTotal.size());
            obj.put("experimentsToday", experimentsIdsToday.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
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
                smartphoneStatistics.setExperimentRankings(getRankings("", experimentId));
                smartphoneStatistics.setExperimentBadges(badgeRepository.findByExperimentIdAndDeviceId(experimentId, smartphone.getId()));
            }
            smartphoneStatistics.setBadges(badgeRepository.findByDeviceId(smartphone.getId()));
            smartphoneStatistics.setRankings(getRankings("", 0));


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

    private Map<Long, Long> getLast7DaysTotalReadings(final Smartphone smartphone) {
        //Readings in the past 7 days
        final Map<Long, Long> counters = new HashMap<>();
        for (long i = 0; i <= 7; i++) {
            counters.put(i, 0L);
        }

        final DateTime date = new DateTime().withMillisOfDay(0);
        final Set<Result> results = resultRepository.findByDeviceIdAndTimestampAfter(smartphone.getId(), date.minusDays(7).getMillis());
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

    private SortedSet<RankingEntry> getRankingList(final String after, final int experimentId) {

        final SortedSet<RankingEntry> list = new TreeSet<>((o1, o2) -> (int) (o2.getCount() - o1.getCount()));
        final Iterable<Smartphone> phones = smartphoneRepository.findAll();

        if (after.isEmpty()) {
            for (final Smartphone phone : phones) {
                if (experimentId == 0) {
                    long count = resultRepository.countByDeviceId(phone.getId());
                    if (count > 0) {
                        list.add(new RankingEntry(phone.getId(), count));
                    }
                } else {
                    long count = resultRepository.countByDeviceIdAndExperimentId(phone.getId(), experimentId);
                    if (count > 0) {
                        list.add(new RankingEntry(phone.getId(), count));
                    }
                }
            }
        } else {
            try {
                final Date afterMillis;
                if (after.contains("T")) {
                    afterMillis = dfTime.parse(after);
                } else {
                    afterMillis = dfDay.parse(after);
                }
                for (final Smartphone phone : phones) {
                    if (experimentId == 0) {
                        long count = resultRepository.countByDeviceIdAndTimestampAfter(phone.getId(), afterMillis.getTime());
                        if (count > 0) {
                            list.add(new RankingEntry(phone.getId(), count));
                        }
                    } else {
                        long count = resultRepository.countByDeviceIdAndExperimentIdAndTimestampAfter(phone.getId(), experimentId, afterMillis.getTime());
                        if (count > 0) {
                            list.add(new RankingEntry(phone.getId(), count));
                        }
                    }
                }
            } catch (ParseException e) {
                return null;
            }
        }
        return list;
    }


}
