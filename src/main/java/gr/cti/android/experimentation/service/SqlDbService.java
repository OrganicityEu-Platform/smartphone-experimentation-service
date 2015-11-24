package gr.cti.android.experimentation.service;

import gr.cti.android.experimentation.model.Result;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.ResultRepository;
import gr.cti.android.experimentation.repository.SmartphoneRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;


/**
 * Provides connection to the sql database for storing data.
 */
@Service
public class SqlDbService {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(SqlDbService.class);

    @Autowired
    ResultRepository resultRepository;
    @Autowired
    SmartphoneRepository smartphoneRepository;
    @Autowired
    ExperimentRepository experimentRepository;

    @Async
    public void store(final Result newResult) throws IOException {

        LOGGER.info("saving result");
        try {
            try {
                final Set<Result> res = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAndMessage(newResult.getExperimentId(), newResult.getDeviceId(), newResult.getTimestamp(), newResult.getMessage());
                if (res == null || (res.isEmpty())) {
                    resultRepository.save(newResult);
                }
            } catch (Exception e) {
                resultRepository.save(newResult);
            }
            LOGGER.info("saveExperiment: OK");
            LOGGER.info("saveExperiment: Stored:");
            LOGGER.info("-----------------------------------");
        } catch (Exception e) {
            LOGGER.info("saveExperiment: FAILEd" + e.getMessage(), e);
            LOGGER.info("-----------------------------------");
        }

    }
}
