package gr.cti.android.experimentation.service;

import gr.cti.android.experimentation.model.Badge;
import gr.cti.android.experimentation.repository.BadgeRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides operations on badges.
 */
@Service
public class BadgeService {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(BadgeService.class);

    @Autowired
    BadgeRepository badgeRepository;

    /**
     * Adds a new badge to the specific device.
     *
     * @param experimentId the experiment performed during badge aquisition.
     * @param deviceId     the device that aquired the badge.
     * @param message      the message of the badge.
     */
    public void addBadge(int experimentId, int deviceId, String message) {
        LOGGER.debug("addBadge expId:" + experimentId + " devId: " + deviceId + " message: " + message);
        final Badge b = new Badge();
        b.setDeviceId(deviceId);
        b.setExperimentId(experimentId);
        b.setMessage(message);
        b.setTimestamp(System.currentTimeMillis());
        badgeRepository.save(b);
    }
}
