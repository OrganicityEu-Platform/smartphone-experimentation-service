
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


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import gr.cti.android.experimentation.model.Region;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final double DIFF = 0.00004;

    public static Polygon createPolygonForRegion(final Region region) throws JSONException {
        final GeometryFactory fact = new GeometryFactory();
        final List<Coordinate> seq = new ArrayList<>();
        try {
            final JSONArray arr = (JSONArray) new JSONArray(region.getCoordinates()).get(0);
            for (int i = 0; i < arr.length(); i++) {
                final JSONArray elems = (JSONArray) arr.get(i);
                seq.add(new Coordinate(elems.getDouble(1), elems.getDouble(0)));
            }
        } catch (Exception e) {
            final JSONArray arr = new JSONArray(region.getCoordinates());
            for (int i = 0; i < arr.length(); i++) {
                final JSONArray elems = (JSONArray) arr.get(i);
                seq.add(new Coordinate(elems.getDouble(1), elems.getDouble(0)));
            }
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
}
