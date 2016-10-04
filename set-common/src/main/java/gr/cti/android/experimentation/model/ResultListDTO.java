package gr.cti.android.experimentation.model;

/*-
 * #%L
 * Smartphone Experimentation Model
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultListDTO implements Serializable {
    @Id
    @GeneratedValue
    private Set<Report> resultList;

    public Set<Report> getResultList() {
        return resultList;
    }

    public void setResultList(Set<Report> resultList) {
        this.resultList = resultList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultListDTO that = (ResultListDTO) o;

        return resultList != null ? resultList.equals(that.resultList) : that.resultList == null;

    }

    @Override
    public int hashCode() {
        return resultList != null ? resultList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ResultListDTO{" +
                "resultList=" + resultList.size() +
                '}';
    }
}
