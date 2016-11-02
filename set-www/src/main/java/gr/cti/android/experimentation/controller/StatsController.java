package gr.cti.android.experimentation.controller;

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

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class StatsController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(StatsController.class);

    @RequestMapping(value = "/stats", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public String getStats(final Map<String, Object> model) {

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
                    if (experimentDateCounts.get(experiment.getExperimentId()).containsKey(date)) {
                        experimentDateCounts.get(experiment.getExperimentId()).put(date, (experimentDateCounts.get(experiment.getExperimentId()).get(date)) + 1L);
                    } else {
                        experimentDateCounts.get(experiment.getExperimentId()).put(date, 1L);
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
