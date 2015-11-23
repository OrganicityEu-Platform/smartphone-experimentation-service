package gr.cti.android.experimentation.repository;

import gr.cti.android.experimentation.model.Experiment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Dimitrios Amaxilatis.
 */
public interface ExperimentRepository extends CrudRepository<Experiment, Long> {

    Page<Experiment> findAll(Pageable pageable);

    Experiment findById(int id);
}