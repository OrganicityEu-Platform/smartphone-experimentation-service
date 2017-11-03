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

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.DownloadableResultDTO;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Measurement;
import gr.cti.android.experimentation.model.RankingEntry;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class RestRankingController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestRankingController.class);


    @Deprecated
    @RequestMapping(value = "/statistics/{phoneId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Map<Long, Long> statisticsByPhone(@PathVariable("phoneId") final String phoneId,
                                             final HttpServletResponse response) {

        final DateTime date = new DateTime().withMillisOfDay(0);
        final Set<Measurement> results = measurementRepository.findByDeviceIdAndTimestampAfter(Integer.parseInt(phoneId), date.minusDays(7).getMillis());

        return extractCounters(results, date);
    }


    @RequestMapping(value = {"/results/{experimentId}"}, method = RequestMethod.GET)
    public Set<DownloadableResultDTO> getResults(
            @PathVariable("experimentId") final String experimentId
    ) {
        final Set<DownloadableResultDTO> externalResults = new TreeSet<>();
        Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        final Set<Measurement> results = measurementRepository.findByExperimentId(experiment.getId());
        LOGGER.info("Will try to convert " + results.size() + " results.");
        for (final Measurement result : results) {
            try {
                final DownloadableResultDTO dres = new DownloadableResultDTO();
                dres.setDate(result.getTimestamp());
                dres.setLongitude(result.getLongitude());
                dres.setLatitude(result.getLatitude());
                dres.setResults(new HashMap<>());
                dres.getResults().put(result.getMeasurementKey(), result.getMeasurementValue());
                if (dres.getResults() != null) {
                    externalResults.add(dres);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return externalResults;
    }

    @RequestMapping(value = {"/results/{experimentId}/csv"}, method = RequestMethod.GET, produces = "text/csv")
    public String getResultsCsv(
            @PathVariable("experimentId") final String experimentId
    ) {
        final Experiment experiment = experimentRepository.findByExperimentId(experimentId);
        final Set<Measurement> results = measurementRepository.findByExperimentId(experiment.getId());
        final StringBuilder resResponse = new StringBuilder();
        final Set<String> headers = new HashSet<>();
        for (final Measurement result : results) {
            headers.add(result.getMeasurementValue());
        }
        headers.remove(LONGITUDE);
        headers.remove(LATITUDE);

        resResponse.append("timestamp,longitude,latitude,");
        resResponse.append(String.join(",", headers)).append("\n");
        for (final Measurement result : results) {
            final List<String> values = new ArrayList<>();
            values.add(String.valueOf(result.getTimestamp()));
            values.add(String.valueOf(result.getLongitude()));
            values.add(String.valueOf(result.getLatitude()));
            for (final String key : headers) {
                if (result.getMeasurementKey().equals(key)) {
                    values.add(result.getMeasurementValue());
                } else {
                    values.add(null);
                }
            }
            resResponse.append(String.join(",", values)).append("\n");
        }
        return resResponse.toString();
    }

    @RequestMapping(value = {"/ranking", "/rankings"}, method = RequestMethod.GET)
    public Set<RankingEntry> getRankings(
            @RequestParam(required = false, defaultValue = "") final String after,
            @RequestParam(required = false, defaultValue = "0") final String experimentId
    ) {
        return getRankingList(after, experimentId);
    }

    @Deprecated
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String getRankings(@RequestParam(required = false, defaultValue = "0") final int deviceId) {

        final long resultsTotal = measurementRepository.countByDeviceId(deviceId);
        final long resultsToday = measurementRepository.countByDeviceIdAndTimestampAfter(deviceId, new DateTime().withMillisOfDay(0).getMillis());
        final Set<Measurement> experimentsTotal = new HashSet<>();
        final Set<String> experimentIdsTotal = new HashSet<>();
        experimentsTotal.addAll(measurementRepository.findDistinctExperimentIdByDeviceId(deviceId));
        final Set<Measurement> experimentsToday = new HashSet<>();
        final Set<String> experimentsIdsToday = new HashSet<>();
        experimentsToday.addAll(measurementRepository.findDistinctExperimentIdByDeviceIdAndTimestampAfter(deviceId, new DateTime().withMillisOfDay(0).getMillis()));

        experimentIdsTotal.addAll(experimentsTotal.stream().map(Measurement::getExperimentId).collect(Collectors.toList()));
        experimentsIdsToday.addAll(experimentsToday.stream().map(Measurement::getExperimentId).collect(Collectors.toList()));
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
