package gr.cti.android.experimentation.controller.ui;

/*-
 * #%L
 * Smartphone Experimentation Web Service
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2016 CTI - Computer Technology Institute and Press "Diophantus"
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.DownloadableResultDTO;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.RankingEntry;
import gr.cti.android.experimentation.model.Result;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class RestRankingController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(RestRankingController.class);


    @Deprecated
    @RequestMapping(value = "/statistics/{phoneId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Map<Long, Long> statisticsByPhone(@PathVariable("phoneId") final String phoneId,
                                             final HttpServletResponse response) {

        final DateTime date = new DateTime().withMillisOfDay(0);
        final Set<Result> results = resultRepository.findByDeviceIdAndTimestampAfter(Integer.parseInt(phoneId), date.minusDays(7).getMillis());

        return extractCounters(results, date);
    }


    @RequestMapping(value = {"/results/{experimentId}"}, method = RequestMethod.GET)
    public Set<DownloadableResultDTO> getResults(
            @PathVariable("experimentId") final String experimentId
    ) {
        final Set<DownloadableResultDTO> externalResults = new TreeSet<>();
        Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        final Set<Result> results = resultRepository.findByExperimentId(experiment.getId());
        LOGGER.info("Will try to convert " + results.size() + " results.");
        for (final Result result : results) {
            try {
                final DownloadableResultDTO dres = new DownloadableResultDTO();
                dres.setDate(result.getTimestamp());
                final HashMap<String, Object> dataMap = new ObjectMapper().readValue(result.getMessage(), new HashMap<String, Object>().getClass());
                if (dataMap.containsKey(LONGITUDE) && dataMap.containsKey(LATITUDE)) {
                    dres.setLongitude((Double) dataMap.get(LONGITUDE));
                    dres.setLatitude((Double) dataMap.get(LATITUDE));
                    dres.setResults(new HashMap<>());
                    dataMap.keySet().stream().filter(key -> !key.equals(LATITUDE) && !key.contains(LONGITUDE)).forEach(key -> {
                        dres.getResults().put(key, String.valueOf(dataMap.get(key)));
                    });
                }
                if (dres.getResults() != null) {
                    externalResults.add(dres);
                }
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }
        return externalResults;
    }

    @RequestMapping(value = {"/results/{experimentId}/csv"}, method = RequestMethod.GET, produces = "text/csv")
    public String getResultsCsv(
            @PathVariable("experimentId") final String experimentId
    ) {
        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        final Set<Result> results = resultRepository.findByExperimentId(experiment.getId());
        final StringBuilder resResponse = new StringBuilder();
        final Set<String> headers = new HashSet<>();
        for (final Result result : results) {
            try {
                final HashMap<String, Object> dataMap = new ObjectMapper().readValue(result.getMessage(), new HashMap<String, Object>().getClass());
                headers.addAll(dataMap.keySet());
            } catch (IOException ignore) {
            }
        }
        headers.remove(LONGITUDE);
        headers.remove(LATITUDE);

        resResponse.append("timestamp,longitude,latitude,");
        resResponse.append(String.join(",", headers)).append("\n");
        for (final Result result : results) {
            final List<String> values = new ArrayList<>();
            values.add(String.valueOf(result.getTimestamp()));
            try {
                final HashMap<String, Object> dataMap = new ObjectMapper().readValue(result.getMessage(), new HashMap<String, Object>().getClass());
                values.add(String.valueOf(dataMap.get(LONGITUDE)));
                values.add(String.valueOf(dataMap.get(LATITUDE)));
                for (final String key : headers) {
                    if (dataMap.containsKey(key)) {
                        values.add(String.valueOf(dataMap.get(key)));
                    } else {
                        values.add(null);
                    }
                }
                resResponse.append(String.join(",", values)).append("\n");
            } catch (IOException e) {
                LOGGER.warn(e, e);
            }
        }
        return resResponse.toString();
    }

    @RequestMapping(value = {"/ranking", "/rankings"}, method = RequestMethod.GET)
    public Set<RankingEntry> getRankings(
            @RequestParam(required = false, defaultValue = "") final String after,
            @RequestParam(required = false, defaultValue = "0") final String experimentId
    ) {
        final Experiment exp = experimentRepository.findByExperimentId(experimentId);
        return getRankingList(after, exp.getId());
    }

    @Deprecated
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String getRankings(@RequestParam(required = false, defaultValue = "0") final int deviceId) {

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


}
