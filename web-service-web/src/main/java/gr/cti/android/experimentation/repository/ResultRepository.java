package gr.cti.android.experimentation.repository;

import gr.cti.android.experimentation.model.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

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

    long countByDeviceIdAndExperimentId(int deviceId, int experimentId);

    Set<Result> findDistinctExperimentIdByDeviceId(int deviceId);

    Set<Result> findDistinctExperimentIdByDeviceIdAndTimestampAfter(int deviceId, long millis);
}