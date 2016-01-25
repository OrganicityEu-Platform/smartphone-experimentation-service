package gr.cti.android.experimentation.repository;

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Plugin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
public interface PluginRepository extends CrudRepository<Plugin, Long> {

    Set<Plugin> findAll();

    Set<Plugin> findByContextTypeIsIn(Set<String> contextType);

    Page<Plugin> findAll(Pageable pageable);

    Set<Plugin> findByContextType(String contextType);

    Plugin findById(int id);

    Set<Plugin> findByUserId(Long userId);
}