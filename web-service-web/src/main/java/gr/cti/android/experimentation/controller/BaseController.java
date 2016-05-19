package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.model.RankingEntry;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import gr.cti.android.experimentation.repository.*;
import gr.cti.android.experimentation.service.*;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class BaseController {

    protected static final String LATITUDE = "org.ambientdynamix.contextplugins.Latitude";
    protected static final String LONGITUDE = "org.ambientdynamix.contextplugins.Longitude";
    protected static final String EXPERIMENT_CONTEXT_TYPE = "org.ambientdynamix.contextplugins.ExperimentPlugin";

    @Autowired
    protected ResultRepository resultRepository;
    @Autowired
    protected SmartphoneRepository smartphoneRepository;
    @Autowired
    protected ExperimentRepository experimentRepository;
    @Autowired
    protected PluginRepository pluginRepository;
    @Autowired
    protected BadgeRepository badgeRepository;

    @Autowired
    protected ModelService modelService;
    @Autowired
    protected SqlDbService sqlDbService;
    @Autowired
    protected OrionService orionService;
    @Autowired
    protected GCMService gcmService;

    protected SimpleDateFormat dfTime;
    protected SimpleDateFormat dfDay;

    @PostConstruct
    public void init() {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        dfTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        dfDay = new SimpleDateFormat("yyyy-MM-dd");
        dfTime.setTimeZone(tz);
        dfDay.setTimeZone(tz);

    }

    protected JSONObject ok() throws JSONException {
        final JSONObject response = new JSONObject();
        response.put("status", "Ok");
        response.put("code", 200);
        return response;
    }

    protected JSONObject ok(final HttpServletResponse servletResponse) throws JSONException {
        servletResponse.setStatus(200);
        return ok();
    }

    protected JSONObject internalServerError(final HttpServletResponse response) throws JSONException {
        response.setStatus(500);
        final JSONObject res = new JSONObject();
        res.put("status", "Internal Server Error");
        res.put("code", 5500);
        return res;
    }

    protected long parseDateMillis(final String after) {
        try {
            return Long.parseLong(after);
        } catch (Exception e) {
            switch (after) {
                case "Today":
                case "today":
                    return new DateTime().withMillisOfDay(0).getMillis();
                case "Yesterday":
                case "yesterday":
                    return new DateTime().withMillisOfDay(0).minusDays(1).getMillis();
                default:
                    return 0;
            }
        }
    }

    protected Map<Long, Long> extractCounters(final Set<Result> results, final DateTime date) {
        final Map<Long, Long> counters = new HashMap<>();
        for (long i = 0; i <= 7; i++) {
            counters.put(i, 0L);
        }
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

    protected SortedSet<RankingEntry> getRankingList(final String after, final int experimentId) {

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
