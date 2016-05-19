package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.repository.*;
import gr.cti.android.experimentation.service.*;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;

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
}
