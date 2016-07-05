package gr.cti.android.experimentation.repository;

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

import gr.cti.android.experimentation.model.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
public interface ResultRepository extends CrudRepository<Result, Long> {

    Page<Result> findAll(Pageable pageable);

    Set<Result> findByExperimentId(int experimentId);

    Set<Result> findByExperimentIdAndDeviceId(int experimentId, int deviceId);

    Set<Result> findByExperimentIdAndTimestampAfter(int experimentId, long start);

    Set<Result> findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(int experimentId, int deviceId, long start);

    Long countByExperimentIdAndDeviceId(int experimentId, int deviceId);

    Set<Result> findByDeviceIdAndTimestampBetween(int deviceId, long start, long end);

    Set<Result> findByDeviceIdAndTimestampAfter(int deviceId, long start);

    Set<Result> findByDeviceIdAndTimestampAfterOrderByTimestampAsc(int deviceId, long start);

    Set<Result> findByDeviceIdAndTimestampIsBetween(int deviceId, long start, long end);

    Set<Result> findByExperimentIdAndDeviceIdAndTimestampAndMessage(int experimentId, int deviceId, long timestamp, String message);

    long countByDeviceIdAndTimestampAfter(int deviceId, long millis);

    long countByDeviceIdAndExperimentIdAndTimestampAfter(int deviceId, int experimentId, long millis);

    long countByDeviceId(int deviceId);

    long countByExperimentId(int experimentId);

    long countByDeviceIdAndExperimentId(int deviceId, int experimentId);

    Set<Result> findDistinctExperimentIdByDeviceId(int deviceId);

    Set<Result> findDistinctExperimentIdByDeviceIdAndTimestampAfter(int deviceId, long millis);

    List<Result> findTimestampByExperimentId(int experimentId);

}