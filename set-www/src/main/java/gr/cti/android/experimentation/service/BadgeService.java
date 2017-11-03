package gr.cti.android.experimentation.service;

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

import gr.cti.android.experimentation.model.Badge;
import gr.cti.android.experimentation.repository.BadgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BadgeService.class);

    @Autowired
    BadgeRepository badgeRepository;

    /**
     * Adds a new badge to the specific device.
     *
     * @param experimentId the experiment performed during badge aquisition.
     * @param deviceId     the device that aquired the badge.
     * @param message      the message of the badge.
     */
    public void addBadge(String experimentId, long deviceId, String message) {
        LOGGER.debug("addBadge expId:" + experimentId + " devId: " + deviceId + " message: " + message);
        final Badge b = new Badge();
        b.setDeviceId(deviceId);
        b.setExperimentId(experimentId);
        b.setMessage(message);
        b.setTimestamp(System.currentTimeMillis());
        badgeRepository.save(b);
    }
}
