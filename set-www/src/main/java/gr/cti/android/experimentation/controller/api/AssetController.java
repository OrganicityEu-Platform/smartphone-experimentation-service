package gr.cti.android.experimentation.controller.api;

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

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.model.NewAssetDTO;
import gr.cti.android.experimentation.service.OrionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.logging.log4j.LogManager.getLogger;

@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class AssetController extends BaseController {
    
    /**
     * a log4j logger to print messages.
     */
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(AssetController.class);
    
    @Autowired
    OrionService orionService;
    
    /**
     * Registers a new asset for the experiment.
     *
     * @return the new asset added to OrganiCity.
     */
    @RequestMapping(value = "/asset/add", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public NewAssetDTO addAsset(@RequestBody NewAssetDTO newAssetDTO) {
        LOGGER.info("adding asset " + newAssetDTO);
        orionService.store(newAssetDTO);
        return newAssetDTO;
    }
}
