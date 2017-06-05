package gr.cti.android.experimentation.controller;

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

import eu.organicity.client.client.OrganicityClient;
import eu.organicity.discovery.dto.FeatureCollectionDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class HomeController extends BaseController {
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "redirect:/swagger-ui.html";
    }
    
    @ResponseBody
    @RequestMapping(value = "/assets/geo/search", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public FeatureCollectionDTO[] assetsGeoSearch(@RequestParam double lat, @RequestParam("long") double lon, @RequestParam int radius) {
        return new OrganicityClient().listNearbyAssets(lat, lon, radius);
    }
    
}
