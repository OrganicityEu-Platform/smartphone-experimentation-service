package gr.cti.android.experimentation.repository;

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