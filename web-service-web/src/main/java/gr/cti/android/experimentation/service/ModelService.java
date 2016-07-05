package gr.cti.android.experimentation.service;

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

import gr.cti.android.experimentation.model.Report;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Plugin;
import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.model.Smartphone;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.PluginRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ModelService {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger log = Logger.getLogger(ModelService.class);

    @Autowired
    PluginRepository pluginRepository;
    @Autowired
    SmartphoneRepository smartphoneRepository;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    ResultRepository resultRepository;

    public Set<Plugin> getPlugins() {
        log.info("getPlugins Called");
        return pluginRepository.findAll();
    }

    public Set<Plugin> getPlugins(Set<String> dependencies) {
        return pluginRepository.findByContextTypeIsIn(dependencies);
    }


    public Experiment getExperiment(final Smartphone device) {

        device.setSensorsRules(device.getSensorsRules());
        String[] smartphoneDependencies = device.getSensorsRules().split(",");

        Iterator<Experiment> experimentsListIterator = experimentRepository.findAll().iterator();
        Experiment experiment;
        do {
            experiment = experimentsListIterator.next();
        } while ((experimentsListIterator.hasNext()));
        if (experiment != null) {
            String[] experimentDependencies = experiment.getSensorDependencies().split(",");
            if (!"finished".equals(experiment.getStatus())
                    && match(smartphoneDependencies, experimentDependencies)) {
                return experiment;
            }
        }
        log.info("getExperiment Called");
        return null;
    }

    public Experiment getExperiment() {

        Iterable<Experiment> experimentsList = experimentRepository.findAll();
        for (Experiment exp : experimentsList) {
            if (exp.getStatus().equals("active")) return exp;
        }
        log.info("getExperiment Called");
        return null;
    }

    public List<Experiment> getExperiments() {

        Iterator<Experiment> experimentsListIterator = experimentRepository.findAll().iterator();
        List<Experiment> experimentsList = new ArrayList<>();
        while (experimentsListIterator.hasNext()) {
            experimentsList.add(experimentsListIterator.next());
        }
        log.info("getExperiment Called");
        return experimentsList;
    }

    public List<Experiment> getEnabledExperiments() {

        final Iterator<Experiment> experimentsListIterator = experimentRepository.findAll().iterator();
        final List<Experiment> experimentsList = new ArrayList<>();
        while (experimentsListIterator.hasNext()) {
            final Experiment experiment = experimentsListIterator.next();
            if (experiment.getEnabled()) {
                experimentsList.add(experiment);
            }
        }
        log.info("getExperiment Called");
        return experimentsList;
    }


    private static boolean match(String[] smartphoneDependencies, String[] experimentDependencies) {
        for (String expDependency : experimentDependencies) {
            boolean found = false;
            for (String smartphoneDependency : smartphoneDependencies) {
                if (smartphoneDependency.equals(expDependency)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public void saveExperiment(Experiment experiment) {
        log.info("saveExperiment Called");
        experimentRepository.save(experiment);
    }


    public Smartphone registerSmartphone(final Smartphone smartphone) {
        if (smartphone.getId() != null && (smartphone.getId() == -1 || smartphone.getId() == 0)) {
            smartphone.setId(null);
        }
        log.info("registerSmartphone: id:" + smartphone.getId() + " phoneId:" + smartphone.getPhoneId());
        final Smartphone phone = smartphoneRepository.findByPhoneId(smartphone.getPhoneId());
        log.info("registerSmartphone: phone:" + phone);
        if (phone == null) {
            return smartphoneRepository.save(smartphone);
        } else {
            phone.setSensorsRules(smartphone.getSensorsRules());
            return smartphoneRepository.save(phone);
        }

    }

    public void reportResults(final Report report) {
        final String expId = report.getName();
        final List<String> experimentResults = report.getResults();
        System.out.println("experiment Id: " + expId);

        final Experiment experiment = experimentRepository.findById(Integer.parseInt(expId));

        for (final String result : experimentResults) {
            final Result resultsEntity = new Result();
            resultsEntity.setExperimentId(experiment.getId());
            resultsEntity.setDeviceId(report.getDeviceId());
            resultsEntity.setTimestamp(System.currentTimeMillis());

            if (result != null) {
                resultsEntity.setMessage(result);
            } else {
                resultsEntity.setMessage("");
            }

            resultRepository.save(resultsEntity);
        }
    }


    public List<Result> getResults(Integer experimentId) {
        if (experimentId == null)
            return new ArrayList<>();
        final Set<Result> resultSet = resultRepository.findByExperimentId(experimentId);
        return new ArrayList<>(resultSet);
    }

    public List<Result> getLastResults(Integer experimentId) {
        return getResults(experimentId);
    }

    public Long getResultSize(Integer experimentId) {
        if (experimentId == null)
            return 0L;
        return (long) getResults(experimentId).size();
    }

    public String durationString(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.valueOf(days) +
                " Days " +
                hours +
                " Hours " +
                minutes +
                " Minutes " +
                seconds +
                " Seconds";
    }

    // returns the total number of messages produced by a single device during a specific day
    // Used for displaying stats for each device over the web
    // timestamp1 is midnight for the beginning of a day, for 5/11/2013 we take into account readings after 00:00 hours

    public String getDailyStats(Long timestamp1, int deviceID) {
        return "";
//
//        // create and additional timestamp for the end of same day, 24 hours minus 1 millisecond
//        Long timestamp2 = timestamp1 + (24 * 60 * 60 * 1000) - 1;
//
//        Query q = getCurrentSession().createQuery("select count(*) from Result where deviceId= :devId and timestamp>= :t1 and timestamp<= :t2");
//
//        q.setParameter("devId", Integer.valueOf(deviceID));
//        q.setParameter("t1", Long.valueOf(timestamp1));
//        q.setParameter("t2", Long.valueOf(timestamp2));
//        if (q.list().size() > 0)
//            return String.valueOf(q.list().get(0));
//        else
//            return "0";
//        //String msgTotal = String.valueOf(results.size());
//
//        //return msgTotal;

    }


    // returns the total number of messages produced by a single device during a specific week as an array
    // with a number per day per array position. It takes as input a timestamp pointing to 00:00 of that day.
    // Used for displaying stats for each device over the web, it utilizes getDailyStats to get stats for each day.

    public String[] getWeeklyStats(Long timestamp, int deviceID) {

        // dummy initialisation in case of no actual readings available
        String[] resultStats = new String[14];
        int t = 6;

        for (int i = 0; i <= 12; i += 2) {

            Long tt = timestamp - t * (24 * 60 * 60 * 1000);
            DateFormat df = new SimpleDateFormat("dd/MM/yy");
            String day = df.format(new Date(tt));
            resultStats[i] = day;
            resultStats[i + 1] = getDailyStats(tt, deviceID);
            t -= 1;
        }

        return resultStats;

    }


    public float pert() {
        Random r = new Random();
        int x = r.nextInt(3);
        return (float) x / 1000;
    }

    public String formatDouble(Float d) {
        return String.format("%.6f", d).replace(',', '.');
    }


    public String assignColor(HashMap<Integer, String> deviceColors, Integer deviceID) {
        String color = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";

        if (deviceColors.containsKey(deviceID)) {
            color = deviceColors.get(deviceID);
            return color;
        }
        int numOfDev = deviceColors.keySet().size();
        if (numOfDev % 6 == 0) color = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";
        else if (numOfDev % 6 == 1) color = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";
        else if (numOfDev % 6 == 2) color = "http://maps.google.com/mapfiles/ms/icons/green-dot.png";
        else if (numOfDev % 6 == 3) color = "http://maps.google.com/mapfiles/ms/icons/yellow-dot.png";
        else if (numOfDev % 6 == 4) color = "http://maps.google.com/mapfiles/ms/icons/purple-dot.png";
        else if (numOfDev % 6 == 5) color = "http://maps.google.com/mapfiles/ms/icons/pink-dot.png";
        deviceColors.put(deviceID, color);
        return color;
    }


}
