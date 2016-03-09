package gr.cti.android.experimentation.controller.ui;

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
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

    @Autowired
    ResultRepository resultRepository;
    @Autowired
    SmartphoneRepository smartphoneRepository;
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
    @RequestMapping(value = "/api/ranking", method = RequestMethod.GET)
    public Set<RankingEntry> experimentView(@RequestParam(value = "after", required = false, defaultValue = "") final String after) {

        SortedSet<RankingEntry> list = new TreeSet<>((o1, o2) -> (int) (o2.getCount() - o1.getCount()));
        final Iterable<Smartphone> phones = smartphoneRepository.findAll();

        if (after.isEmpty()) {
            for (final Smartphone phone : phones) {
                long count = resultRepository.countByDeviceId(phone.getId());
                if (count > 0) {
                    list.add(new RankingEntry(phone.getId(), count));
                }
            }
        } else {

            try {
                Date afterMillis;
                if (after.contains("T")) {
                    afterMillis = dfTime.parse(after);
                } else {
                    afterMillis = dfDay.parse(after);
                }
                for (final Smartphone phone : phones) {
                    long count = resultRepository.countByDeviceIdAndTimestampAfter(phone.getId(), afterMillis.getTime());
                    if (count > 0) {
                        list.add(new RankingEntry(phone.getId(), count));
                    }
                }
            } catch (ParseException e) {
                return null;
            }

        }
        return list;
    }

    class RankingEntry {
        long phoneId;
        long count;

        public RankingEntry(long phoneId, long count) {
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
