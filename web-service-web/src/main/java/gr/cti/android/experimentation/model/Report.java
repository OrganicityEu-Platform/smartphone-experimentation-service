package gr.cti.android.experimentation.model;

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

import java.util.List;

import com.google.gson.Gson;

public class Report{

    private String jobName;
    private int  deviceId;
    private List<String> jobResults;

    public Report()
    {
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Report(String jobName)
    {
        this.jobName = jobName;
    }

    public void setResults(List<String> jobResults)
    {
        this.jobResults = jobResults;
    }
    public void setJobResults(List<String> jobResults)
    {
        this.jobResults = jobResults;
    }

    public List<String> getResults()
    {
        return this.jobResults;
    }
    public List<String> getJobResults()
    {
        return this.jobResults;
    }

    public String getName()
    {
        return jobName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static Report fromJson(String json){
        return new Gson().fromJson(json, Report.class);
    }
}
