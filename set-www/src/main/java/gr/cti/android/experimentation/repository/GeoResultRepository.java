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

import gr.cti.android.experimentation.model.GeoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
public interface GeoResultRepository extends CrudRepository<GeoResult, Long> {

    Page<GeoResult> findAll(Pageable pageable);


    Set<GeoResult> findByTimestampAfter(long start);

    Set<GeoResult> findByTimestampAfterOrderByTimestampAsc(long start);

    Set<GeoResult> findByTimestampBetween(long start, long end);

    Set<GeoResult> findByTimestampAndMessage(long timestamp, String message);

    long countByTimestampAfter(long millis);
}