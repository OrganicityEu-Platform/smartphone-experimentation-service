package gr.cti.android.experimentation.util.test;

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


import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.Assert;
import gr.cti.android.experimentation.service.OrionService;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static gr.cti.android.experimentation.util.Utils.createPolygonForCoordinates;

public class PolygonTest {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(OrionService.class);
    private static final double LAT = 38.290007;
    private static final double LON = 21.7759985;


    @Before
    public void before() {
    }

    @Test
    public void testGeocoding() throws Exception {
        GeometryFactory fact = new GeometryFactory();
        List<Polygon> polygons = new ArrayList<>();
        double singleArea = 0;
        for (int i = 0; i < 10; i++) {
            final Polygon pol = createPolygonForCoordinates(LAT, LON);
            singleArea = pol.getArea() * 12365 * 1000 * 1000;
            LOGGER.info("Single Area " + i + ": " + singleArea + " m2");
            polygons.add(pol);
        }

        // note the following geometry collection may be invalid (say with overlapping polygons)
        GeometryCollection geometryCollection = (GeometryCollection) fact.buildGeometry(polygons);
        double totalArea = geometryCollection.union().getArea() * 12365 * 1000 * 1000;
        LOGGER.info("Total Area: " + totalArea + " m2");
        Assert.equals(singleArea, totalArea);

    }

}
