package gr.cti.android.experimentation.repository;

import gr.cti.android.experimentation.model.Badge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
public interface BadgeRepository extends CrudRepository<Badge, Long> {

    Page<Badge> findAll(Pageable pageable);

    Badge findById(int id);

    Set<Badge> findByExperimentId(int experimentId);

    Set<Badge> findByDeviceId(int deviceId);

    Set<Badge> findByExperimentIdAndDeviceId(int experimentId, int deviceId);

}