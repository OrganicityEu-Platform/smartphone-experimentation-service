package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.PluginRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import gr.cti.android.experimentation.service.*;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseController {

    protected static final String LATITUDE = "org.ambientdynamix.contextplugins.Latitude";
    protected static final String LONGITUDE = "org.ambientdynamix.contextplugins.Longitude";

    @Autowired
    protected ResultRepository resultRepository;
    @Autowired
    protected SmartphoneRepository smartphoneRepository;
    @Autowired
    protected ExperimentRepository experimentRepository;
    @Autowired
    protected PluginRepository pluginRepository;
    @Autowired
    protected ModelManager modelManager;
    @Autowired
    protected InfluxDbService influxDbService;
    @Autowired
    protected SqlDbService sqlDbService;
    @Autowired
    protected OrionService orionService;
    @Autowired
    protected GCMService gcmService;
    @Autowired
    protected CityService cityService;

    @Value("${plugins.dir}")
    protected String pluginsDir;

    protected JSONObject ok(final HttpServletResponse servletResponse) throws JSONException {
        servletResponse.setStatus(200);
        final JSONObject response = new JSONObject();
        response.put("status", "Ok");
        response.put("code", 200);
        return response;
    }

    protected JSONObject internalServerError(final HttpServletResponse response) throws JSONException {
        response.setStatus(500);
        final JSONObject res = new JSONObject();
        res.put("status", "Internal Server Error");
        res.put("code", 5500);
        return res;
    }

}
