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

import java.text.DecimalFormat;
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


    @ResponseBody
    @RequestMapping(value = "/api/ranking", method = RequestMethod.GET)
    public Set<T> experimentView(final Map<String, Object> model, @RequestParam(value = "after", required = false, defaultValue = "0") final long after) {
        LOGGER.debug("ranking:" + after);

        final DateTime start = new DateTime(after).withMillisOfDay(0);

        SortedSet<T> list = new TreeSet<>((o1, o2) -> (int) (o2.getCount() - o1.getCount()));

        final Iterable<Smartphone> phones = smartphoneRepository.findAll();
        for (final Smartphone phone : phones) {
            long count = resultRepository.countByDeviceIdAndTimestampAfter(phone.getId(), start.getMillis());
            if (count > 0) {
                list.add(new T(phone.getId(), count));
            }
        }

        return list;
    }

    class T {
        long phoneId;
        long count;

        public T(long phoneId, long count) {
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
