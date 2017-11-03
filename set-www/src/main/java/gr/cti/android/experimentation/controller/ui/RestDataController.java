package gr.cti.android.experimentation.controller.ui;

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
import gr.cti.android.experimentation.model.Result;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
public class RestDataController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestDataController.class);

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = "text/csv")
    public String dataCsv(@RequestParam(value = "type") final String type) throws JSONException {
        final StringBuilder response = new StringBuilder();
        for (final Result result : resultRepository.findAll()) {
            try {
                final JSONObject object = new JSONObject(result.getMessage());
                if (object.has(type)) {
                    response.append(object.get(type)).append("\n");
                }
            } catch (Exception ignore) {
            }
        }
        return response.toString();
    }
}
