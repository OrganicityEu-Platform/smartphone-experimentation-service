package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class StatsController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(StatsController.class);

    @RequestMapping(value = "/stats", method = RequestMethod.GET, produces = "application/json")
    public String getStats(final Map<String, Object> model) {

        final DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd");
        final Map<Integer, Long> experimentCount = new HashMap<>();
        model.put("experimentCount", experimentCount);
        final Map<Long, Long> totalDateCounts = new HashMap<>();
        final Map<Integer, HashMap<Long, Long>> experimentDateCounts = new HashMap<>();
        model.put("totalDateCounts", totalDateCounts);
        model.put("experimentDateCounts", experimentDateCounts);
        for (final Experiment experiment : experimentRepository.findAll()) {
            experimentCount.put(experiment.getId(), resultRepository.countByExperimentId(experiment.getId()));

            experimentDateCounts.put(experiment.getId(), new HashMap<>());
            final List<Result> results = resultRepository.findTimestampByExperimentId(experiment.getId());
            for (final Result result : results) {
                final long date = new DateTime(result.getTimestamp()).withMillisOfDay(0).getMillis();
                if (date > 0) {
                    if (totalDateCounts.containsKey(date)) {
                        totalDateCounts.put(date, totalDateCounts.get(date) + 1L);
                    } else {
                        totalDateCounts.put(date, 1L);
                    }
                    if (experimentDateCounts.get(experiment.getId()).containsKey(date)) {
                        experimentDateCounts.get(experiment.getId()).put(date, (experimentDateCounts.get(experiment.getId()).get(date)) + 1L);
                    } else {
                        experimentDateCounts.get(experiment.getId()).put(date, 1L);
                    }
                }
            }
        }

        final Map<Integer, Long> userCount = new HashMap<>();
        model.put("userCount", userCount);
        for (final Smartphone phone : smartphoneRepository.findAll()) {
            userCount.put(phone.getId(), resultRepository.countByDeviceId(phone.getId()));
        }

        return "stats";
    }
}
