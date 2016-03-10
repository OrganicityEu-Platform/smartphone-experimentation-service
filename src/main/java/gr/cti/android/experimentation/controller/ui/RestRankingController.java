package gr.cti.android.experimentation.controller.ui;

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
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

    @ResponseBody
    @RequestMapping(value = "/api/v1/ranking", method = RequestMethod.GET)
    public Set<RankingEntry> getRankings(
            @RequestParam(required = false, defaultValue = "") final String after,
            @RequestParam(required = false, defaultValue = "0") final int experimentId
    ) {

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

    @ResponseBody
    @RequestMapping(value = "/api/v1/statistics", method = RequestMethod.GET)
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

        for (final Result integer : experimentsTotal) {
            experimentIdsTotal.add(integer.getExperimentId());
        }
        for (final Result integer : experimentsToday) {
            experimentsIdsToday.add(integer.getExperimentId());
        }
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

    class RankingEntry {
        private long phoneId;
        private long count;

        public RankingEntry(final long phoneId, final long count) {
            this.phoneId = phoneId;
            this.count = count;
        }

        public long getPhoneId() {
            return phoneId;
        }

        public void setPhoneId(long phoneId) {
            this.phoneId = phoneId;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
