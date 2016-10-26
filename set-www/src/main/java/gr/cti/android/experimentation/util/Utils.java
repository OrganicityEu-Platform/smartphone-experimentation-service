
package gr.cti.android.experimentation.util;

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


import com.vividsolutions.jts.geom.*;
import gr.cti.android.experimentation.model.Region;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final double DIFF = 0.00004;
    public static final String LATITUDE = "org.ambientdynamix.contextplugins.Latitude";
    public static final String LONGITUDE = "org.ambientdynamix.contextplugins.Longitude";

    public static Polygon createPolygonForRegion(final Region region) throws JSONException {
        final GeometryFactory fact = new GeometryFactory();
        final List<Coordinate> seq = new ArrayList<>();
        final JSONArray arr = new JSONArray(region.getCoordinates());
        for (int i = 0; i < arr.length(); i++) {
            final JSONArray elems = (JSONArray) arr.get(i);
            seq.add(new Coordinate(elems.getDouble(1), elems.getDouble(0)));
        }
        return fact.createPolygon(seq.toArray(new Coordinate[1]));
    }

    public static Polygon createPolygonForCoordinates(double lat, double lon) {
        final GeometryFactory fact = new GeometryFactory();

        final List<Coordinate> seq = new ArrayList<>();
        seq.add(new Coordinate(lat - DIFF, lon - DIFF));
        seq.add(new Coordinate(lat + DIFF, lon - DIFF));
        seq.add(new Coordinate(lat + DIFF, lon + DIFF));
        seq.add(new Coordinate(lat - DIFF, lon + DIFF));
        seq.add(new Coordinate(lat - DIFF, lon - DIFF));
        return fact.createPolygon(seq.toArray(new Coordinate[1]));
    }

    public static Point createPointForCoordinates(final String latitude, final String longitude) {
        final GeometryFactory fact = new GeometryFactory();
        return fact.createPoint(new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude)));
    }

    public static double polygons2Area(List<Polygon> polygons) {
        final GeometryFactory fact = new GeometryFactory();
        // note the following geometry collection may be invalid (say with overlapping polygons)
        GeometryCollection geometryCollection = (GeometryCollection) fact.buildGeometry(polygons);
        return geometryCollection.union().getArea() * 12365 * 1000 * 1000;
    }
}
