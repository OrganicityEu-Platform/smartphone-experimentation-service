package gr.cti.android.experimentation.controller.ui;

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ResultRepository;
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
public class RestDataController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(RestDataController.class);

    @RequestMapping(value = "/experiment/data/{experimentId}", method = RequestMethod.GET)
    public String experimentView(final Map<String, Object> model, @PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
        LOGGER.debug("experiment:" + experiment);
        if (deviceId == 0) {
            model.put("title", "Experiment " + experiment);
        } else {
            model.put("title", "Experiment " + experiment + " device:" + deviceId);
        }
        model.put("addressPoints", getExperimentData(experiment, deviceId, after).toString());
        LOGGER.debug("-----------------------------------");
        return "experiment-data";
    }

    @RequestMapping(value = "/experiment/data2/{experimentId}", method = RequestMethod.GET)
    public String experimentView2(final Map<String, Object> model, @PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) throws JSONException {
        LOGGER.debug("experiment:" + experiment);
        if (deviceId == 0) {
            model.put("title", "Experiment " + experiment);
        } else {
            model.put("title", "Experiment " + experiment + " device:" + deviceId);
        }
        model.put("addressPoints", getExperimentData(experiment, deviceId, after).toString());
        model.put("max", getExperimentDataMax(experiment, deviceId, after).toString());
        LOGGER.debug("-----------------------------------");
        return "experiment-data2";
    }

    @ResponseBody
    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = "text/csv")
    public String dataCsv(final Map<String, Object> model, @RequestParam(value = "type") final String type) throws JSONException {
        final StringBuilder response = new StringBuilder();
        for (final Result result : resultRepository.findAll()) {
            try {
                final JSONObject object = new JSONObject(result.getMessage());
                if (object.has(type)) {
                    response.append(object.get(type)).append("\n");
                }
            } catch (Exception ingore) {
            }
        }
        return response.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/data", method = RequestMethod.GET, produces = "application/json")
    public String getExperimentDataByExperimentId(@RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
        return getAllData(deviceId, after).toString();
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment/data/{experimentId}", method = RequestMethod.GET, produces = "application/json")
    public String getExperimentDataByExperimentId(@PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
        return getExperimentData(experiment, deviceId, after).toString();
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment/data/{experimentId}/hour", method = RequestMethod.GET, produces = "application/json")
    public String getExperimentDataHourlyByExperimentId(@PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
        JSONObject data = getExperimentHourlyData(experiment, deviceId, after);
        LOGGER.info(data);
        return data.toString();
    }

//    @ResponseBody
//    @RequestMapping(value = "/api/v1/experiment/data/{experimentId}/rankings", method = RequestMethod.GET, produces = "application/json")
//    public String getExperimentDataHourlyByExperimentId(@PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
//        JSONObject data = getExperimentHourlyData(experiment, deviceId, after);
//        LOGGER.info(data);
//        return data.toString();
//    }

    private JSONArray getExperimentData(final String experiment, final int deviceId, final String after) {
        DecimalFormat df = new DecimalFormat("#.000");
        long start;
        try {
            start = Long.parseLong(after);
        } catch (Exception e) {
            switch (after) {
                case "Today":
                case "today":
                    start = new DateTime().withMillisOfDay(0).getMillis();
                    break;
                case "Yesterday":
                case "yesterday":
                    start = new DateTime().withMillisOfDay(0).minusDays(1).getMillis();
                    break;
                default:
                    start = 0;
                    break;
            }
        }
        final Set<Result> results;
        if (deviceId == 0) {
            results = resultRepository.findByExperimentIdAndTimestampAfter(Integer.parseInt(experiment), start);
        } else {
            results = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(Integer.parseInt(experiment), deviceId, start);
        }

        Map<String, Map<String, Map<String, DescriptiveStatistics>>> dataAggregates = new HashMap<>();
        String longitude = null;
        String latitude = null;
        DescriptiveStatistics wholeDataStatistics = new DescriptiveStatistics();
        Map<String, Map<String, Long>> locationsHeatMap = new HashMap<>();
        for (Result result : results) {
            try {
                if (!result.getMessage().startsWith("{")) {
                    continue;
                }
                final JSONObject message = new JSONObject(result.getMessage());

                if (message.has(LATITUDE) && message.has(LONGITUDE)) {
                    longitude = df.format(message.getDouble(LONGITUDE));
                    latitude = df.format(message.getDouble(LATITUDE));
                    if (!dataAggregates.containsKey(longitude)) {
                        dataAggregates.put(longitude, new HashMap<>());
                    }
                    if (!dataAggregates.get(longitude).containsKey(latitude)) {
                        dataAggregates.get(longitude).put(latitude, new HashMap<>());
                    }

                    //HeatMap
                    if (!locationsHeatMap.containsKey(longitude)) {
                        locationsHeatMap.put(longitude, new HashMap<>());
                    }
                    if (!locationsHeatMap.get(longitude).containsKey(latitude)) {
                        locationsHeatMap.get(longitude).put(latitude, 0L);
                    }
                    final Long val = locationsHeatMap.get(longitude).get(latitude);
                    locationsHeatMap.get(longitude).put(latitude, val + 1);


                    final Iterator iterator = message.keys();
                    if (longitude != null && latitude != null) {
                        while (iterator.hasNext()) {
                            final String key = (String) iterator.next();
                            if (key.equals(LATITUDE) || key.equals(LONGITUDE)) {
                                continue;
                            }

                            if (!dataAggregates.get(longitude).get(latitude).containsKey(key)) {
                                dataAggregates.get(longitude).get(latitude).put(key, new DescriptiveStatistics());
                            }
                            try {
                                dataAggregates.get(longitude).get(latitude).get(key).addValue(
                                        message.getDouble(key)
                                );
                                wholeDataStatistics.addValue(message.getDouble(key));
                            } catch (Exception e) {
                                LOGGER.error(e, e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }
        final JSONArray addressPoints = new JSONArray();
        for (final String longit : dataAggregates.keySet()) {
            for (final String latit : dataAggregates.get(longit).keySet()) {
                LOGGER.info("{" + longit + ":" + latit + "}");
                final JSONArray measurement = new JSONArray();
                try {
                    measurement.put(Double.parseDouble(latit));
                    measurement.put(Double.parseDouble(longit));
                    if (locationsHeatMap.containsKey(longit) && locationsHeatMap.get(longit).containsKey(latit)) {
                        measurement.put(String.valueOf(locationsHeatMap.get(longit).get(latit)));
                    } else {
                        measurement.put(1);
                    }
                    final JSONObject data = new JSONObject();
                    measurement.put(data);
                    for (final Object key : dataAggregates.get(longit).get(latit).keySet()) {
                        final String keyString = (String) key;
                        final String part = keyString.split("\\.")[keyString.split("\\.").length - 1];
                        data.put(part, dataAggregates.get(longit).get(latit).get(keyString).getMean());
                    }
                    addressPoints.put(measurement);
                } catch (JSONException e) {
                    LOGGER.error(e, e);
                }
            }
        }
        LOGGER.info(addressPoints.toString());
        return addressPoints;
    }

    private JSONArray getAllData(final int deviceId, final String after) {
        DecimalFormat df = new DecimalFormat("#.000");
        long start;
        try {
            start = Long.parseLong(after);
        } catch (Exception e) {
            switch (after) {
                case "Today":
                case "today":
                    start = new DateTime().withMillisOfDay(0).getMillis();
                    break;
                case "Yesterday":
                case "yesterday":
                    start = new DateTime().withMillisOfDay(0).minusDays(1).getMillis();
                    break;
                default:
                    start = 0;
                    break;
            }
        }
        final Set<Result> results;
        if (deviceId == 0) {
            results = new HashSet<>();
        } else {
            results = resultRepository.findByDeviceIdAndTimestampAfterOrderByTimestampAsc(deviceId, start);
        }

        Map<String, Map<String, Map<String, DescriptiveStatistics>>> dataAggregates = new HashMap<>();
        String longitude = null;
        String latitude = null;
        DescriptiveStatistics wholeDataStatistics = new DescriptiveStatistics();
        Map<String, Map<String, Long>> locationsHeatMap = new HashMap<>();
        for (Result result : results) {
            try {
                if (!result.getMessage().startsWith("{")) {
                    continue;
                }
                final JSONObject message = new JSONObject(result.getMessage());

                if (message.has(LATITUDE) && message.has(LONGITUDE)) {
                    longitude = df.format(message.getDouble(LONGITUDE));
                    latitude = df.format(message.getDouble(LATITUDE));
                    if (!dataAggregates.containsKey(longitude)) {
                        dataAggregates.put(longitude, new HashMap<>());
                    }
                    if (!dataAggregates.get(longitude).containsKey(latitude)) {
                        dataAggregates.get(longitude).put(latitude, new HashMap<>());
                    }

                    //HeatMap
                    if (!locationsHeatMap.containsKey(longitude)) {
                        locationsHeatMap.put(longitude, new HashMap<>());
                    }
                    if (!locationsHeatMap.get(longitude).containsKey(latitude)) {
                        locationsHeatMap.get(longitude).put(latitude, 0L);
                    }
                    final Long val = locationsHeatMap.get(longitude).get(latitude);
                    locationsHeatMap.get(longitude).put(latitude, val + 1);


                    final Iterator iterator = message.keys();
                    if (longitude != null && latitude != null) {
                        while (iterator.hasNext()) {
                            final String key = (String) iterator.next();
                            if (key.equals(LATITUDE) || key.equals(LONGITUDE)) {
                                continue;
                            }

                            if (!dataAggregates.get(longitude).get(latitude).containsKey(key)) {
                                dataAggregates.get(longitude).get(latitude).put(key, new DescriptiveStatistics());
                            }
                            try {
                                dataAggregates.get(longitude).get(latitude).get(key).addValue(
                                        message.getDouble(key)
                                );
                                wholeDataStatistics.addValue(message.getDouble(key));
                            } catch (Exception e) {
                                LOGGER.error(e, e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }
        final JSONArray addressPoints = new JSONArray();
        for (final String longit : dataAggregates.keySet()) {
            for (final String latit : dataAggregates.get(longit).keySet()) {
                LOGGER.info("{" + longit + ":" + latit + "}");
                final JSONArray measurement = new JSONArray();
                try {
                    measurement.put(Double.parseDouble(latit));
                    measurement.put(Double.parseDouble(longit));
                    if (locationsHeatMap.containsKey(longit) && locationsHeatMap.get(longit).containsKey(latit)) {
                        measurement.put(String.valueOf(locationsHeatMap.get(longit).get(latit)));
                    } else {
                        measurement.put(1);
                    }
                    final JSONObject data = new JSONObject();
                    measurement.put(data);
                    for (final Object key : dataAggregates.get(longit).get(latit).keySet()) {
                        final String keyString = (String) key;
                        final String part = keyString.split("\\.")[keyString.split("\\.").length - 1];
                        data.put(part, dataAggregates.get(longit).get(latit).get(keyString).getMean());
                    }
                    addressPoints.put(measurement);
                } catch (JSONException e) {
                    LOGGER.error(e, e);
                }
            }
        }
        LOGGER.info(addressPoints.toString());
        return addressPoints;
    }

    private JSONObject getExperimentHourlyData(final String experiment, final int deviceId, final String after) {
        DecimalFormat df = new DecimalFormat("#.000");
        long start;
        try {
            start = Long.parseLong(after);
        } catch (Exception e) {
            switch (after) {
                case "Today":
                case "today":
                    start = new DateTime().withMillisOfDay(0).getMillis();
                    break;
                case "Yesterday":
                case "yesterday":
                    start = new DateTime().withMillisOfDay(0).minusDays(1).getMillis();
                    break;
                default:
                    start = 0;
                    break;
            }
        }
        final Set<Result> results;
        if (deviceId == 0) {
            results = resultRepository.findByExperimentIdAndTimestampAfter(Integer.parseInt(experiment), start);
        } else {
            results = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(Integer.parseInt(experiment), deviceId, start);
        }

        try {
            Map<Integer, Map<String, Map<String, Map<String, DescriptiveStatistics>>>> dataAggregates = new HashMap<>();
            String longitude = null;
            String latitude = null;
            DescriptiveStatistics wholeDataStatistics = new DescriptiveStatistics();
            Map<Integer, Map<String, Map<String, Long>>> locationsHeatMap = new HashMap<>();
            for (Result result : results) {
                try {
                    if (!result.getMessage().startsWith("{")) {
                        continue;
                    }
                    final JSONObject message = new JSONObject(result.getMessage());

                    int hour = new DateTime(result.getTimestamp()).getHourOfDay();

                    if (message.has(LATITUDE) && message.has(LONGITUDE)) {
                        longitude = df.format(message.getDouble(LONGITUDE));
                        latitude = df.format(message.getDouble(LATITUDE));
                        if (!dataAggregates.containsKey(hour)) {
                            dataAggregates.put(hour, new HashMap<>());
                        }
                        if (!dataAggregates.get(hour).containsKey(longitude)) {
                            dataAggregates.get(hour).put(longitude, new HashMap<>());
                        }
                        if (!dataAggregates.get(hour).get(longitude).containsKey(latitude)) {
                            dataAggregates.get(hour).get(longitude).put(latitude, new HashMap<>());
                        }

                        //HeatMap
                        if (!locationsHeatMap.containsKey(hour)) {
                            locationsHeatMap.put(hour, new HashMap<>());
                        }
                        if (!locationsHeatMap.get(hour).containsKey(longitude)) {
                            locationsHeatMap.get(hour).put(longitude, new HashMap<>());
                        }
                        if (!locationsHeatMap.get(hour).get(longitude).containsKey(latitude)) {
                            locationsHeatMap.get(hour).get(longitude).put(latitude, 0L);
                        }

                        final Long val = locationsHeatMap.get(hour).get(longitude).get(latitude);
                        locationsHeatMap.get(hour).get(longitude).put(latitude, val + 1);


                        final Iterator iterator = message.keys();
                        if (longitude != null && latitude != null) {
                            while (iterator.hasNext()) {
                                final String key = (String) iterator.next();
                                if (key.equals(LATITUDE) || key.equals(LONGITUDE)) {
                                    continue;
                                }

                                if (!dataAggregates.get(hour).get(longitude).get(latitude).containsKey(key)) {
                                    dataAggregates.get(hour).get(longitude).get(latitude).put(key, new DescriptiveStatistics());
                                }
                                try {
                                    dataAggregates.get(hour).get(longitude).get(latitude).get(key).addValue(message.getDouble(key));
                                    wholeDataStatistics.addValue(message.getDouble(key));
                                } catch (Exception e) {
                                    LOGGER.error(e, e);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
            }
            final JSONObject hourlyPoints = new JSONObject();
            for (final Integer hour : dataAggregates.keySet()) {
                final JSONArray addressPoints = new JSONArray();
                for (final String longit : dataAggregates.get(hour).keySet()) {
                    for (final String latit : dataAggregates.get(hour).get(longit).keySet()) {
                        LOGGER.info("{" + longit + ":" + latit + "}");
                        final JSONArray measurement = new JSONArray();
                        try {
                            measurement.put(Double.parseDouble(latit));
                            measurement.put(Double.parseDouble(longit));
                            if (locationsHeatMap.containsKey(hour) && locationsHeatMap.get(hour).containsKey(longit) && locationsHeatMap.get(hour).get(longit).containsKey(latit)) {
                                measurement.put(locationsHeatMap.get(hour).get(longit).get(latit));
                            } else {
                                measurement.put(0);
                            }
                            final JSONObject data = new JSONObject();
                            measurement.put(data);
                            for (final Object key : dataAggregates.get(hour).get(longit).get(latit).keySet()) {
                                final String keyString = (String) key;
                                final String part = keyString.split("\\.")[keyString.split("\\.").length - 1];
                                data.put(part, dataAggregates.get(hour).get(longit).get(latit).get(keyString).getMean());
                            }
                            addressPoints.put(measurement);
                        } catch (JSONException e) {
                            LOGGER.error(e, e);
                        }
                    }
                }
                try {
                    hourlyPoints.put(String.valueOf(hour), addressPoints);
                } catch (JSONException e) {
                    LOGGER.error(e, e);
                }
            }
            LOGGER.info(hourlyPoints.toString());
            return hourlyPoints;
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        return null;

    }

    private JSONObject getExperimentDataMax(final String experiment, final int deviceId, final String after) {
        long start;
        try {
            start = Long.parseLong(after);
        } catch (Exception e) {
            if (after.equals("Today") || after.equals("today")) {
                start = new DateTime().withMillisOfDay(0).getMillis();
            } else if (after.equals("Yesterday") || after.equals("yesterday")) {
                start = new DateTime().withMillisOfDay(0).minusDays(1).getMillis();
            } else {
                start = 0;
            }
        }
        final Set<Result> results;
        if (deviceId == 0) {
            results = resultRepository.findByExperimentIdAndTimestampAfter(Integer.parseInt(experiment), start);
        } else {
            results = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(Integer.parseInt(experiment), deviceId, start);
        }

        final Map<String, DescriptiveStatistics> maxValues = new HashMap<String, DescriptiveStatistics>();
        final JSONObject maxJson = new JSONObject();
        for (Result result : results) {
            try {
                if (!result.getMessage().startsWith("{")) {
                    continue;
                }
                final JSONObject message = new JSONObject(result.getMessage());

                Iterator iterator = message.keys();
                while (iterator.hasNext()) {
                    final String keyString = (String) iterator.next();
                    if (!maxValues.containsKey(keyString)) {
                        maxValues.put(keyString, new DescriptiveStatistics());
                    }
                    maxValues.get(keyString).addValue(message.getDouble(keyString));
                    try {
                        maxJson.put(keyString, maxValues.get(keyString).getMax());
                    } catch (JSONException e) {
                        //ignore
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }
        return maxJson;
    }
}
