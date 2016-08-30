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

import gr.cti.android.experimentation.model.*;
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
    protected RegionRepository regionRepository;

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


    protected SmartphoneDTO newSmartphoneDTO(Smartphone smartphone) {
        final SmartphoneDTO dto = new SmartphoneDTO();
        dto.setDeviceType(smartphone.getDeviceType());
        dto.setId(smartphone.getId());
        dto.setPhoneId(smartphone.getPhoneId());
        dto.setSensorsRules(smartphone.getSensorsRules());
        return dto;
    }

    protected Smartphone newSmartphone(SmartphoneDTO dto) {
        final Smartphone smartphone = new Smartphone();
        smartphone.setDeviceType(dto.getDeviceType());
        smartphone.setId(dto.getId());
        smartphone.setPhoneId(dto.getPhoneId());
        smartphone.setSensorsRules(dto.getSensorsRules());
        return smartphone;
    }

    protected RegionDTO newRegionDTO(final Region region) {
        final RegionDTO dto = new RegionDTO();
        dto.setId(region.getId());
        dto.setCoordinates(region.getCoordinates());
        dto.setStartDate(region.getStartDate());
        dto.setEndDate(region.getEndDate());
        dto.setStartTime(region.getStartTime());
        dto.setEndTime(region.getEndTime());
        dto.setExperimentId(region.getExperimentId());
        dto.setMaxMeasurements(region.getMaxMeasurements());
        dto.setMinMeasurements(region.getMinMeasurements());
        dto.setWeight(region.getWeight());
        dto.setName(region.getName());
        return dto;
    }

    protected Region newRegion(final RegionDTO dto) {
        final Region region = new Region();
        region.setId(dto.getId());
        region.setCoordinates(dto.getCoordinates());
        region.setStartDate(dto.getStartDate());
        region.setEndDate(dto.getEndDate());
        region.setStartTime(dto.getStartTime());
        region.setEndTime(dto.getEndTime());
        region.setMaxMeasurements(dto.getMaxMeasurements());
        region.setMinMeasurements(dto.getMinMeasurements());
        region.setWeight(dto.getWeight());
        region.setName(dto.getName());
        return region;
    }

    protected PluginDTO newPluginDTO(final Plugin plugin) {
        final PluginDTO dto = new PluginDTO();
        dto.setId(plugin.getId());
        dto.setUserId(plugin.getUserId());
        dto.setImageUrl(plugin.getImageUrl());
        dto.setInstallUrl(plugin.getInstallUrl());
        dto.setContextType(plugin.getContextType());
        dto.setDescription(plugin.getDescription());
        dto.setName(plugin.getName());
        dto.setFilename(plugin.getFilename());
        dto.setRuntimeFactoryClass(plugin.getRuntimeFactoryClass());
        return dto;
    }

    protected ExperimentDTO newExperimentDTO(final Experiment experiment) {
        final ExperimentDTO dto = new ExperimentDTO();
        dto.setId(experiment.getExperimentId());
        dto.setUserId(experiment.getUserId());
        dto.setName(experiment.getName());
        dto.setUrl(experiment.getUrl());
        dto.setUrlDescription(experiment.getUrlDescription());
        dto.setTimestamp(experiment.getTimestamp());
        dto.setContextType(experiment.getContextType());
        dto.setStatus(experiment.getStatus());
        dto.setSensorDependencies(experiment.getSensorDependencies());
        dto.setDescription(experiment.getDescription());
        return dto;
    }

}
