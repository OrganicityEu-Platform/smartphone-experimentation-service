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
//
//
//import de.meggsimum.w3w.Coordinates;
//import de.meggsimum.w3w.What3Words;
//import gr.cti.android.experimentation.service.OrionService;
//import junit.framework.Assert;
//import org.apache.log4j.Logger;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.Properties;
//
//import static gr.cti.android.experimentation.service.OrionService.commaString;
//
//public class W3WordsTest {
//    /**
//     * a log4j logger to print messages.
//     */
//    private static final Logger LOGGER = Logger.getLogger(OrionService.class);
//
//
//    private static final double PATRAS_LAT = 38.2500;
//    private static final double PATRAS_LNG = 21.7333;
//    private static final double NEWYORK_LAT = 40.7127;
//    private static final double NEWYORK_LNG = -74.0059;
//    private static final double LONDON_LAT = 51.5072;
//    private static final double LONDON_LNG = -0.1275;
//    private static final double AARHUS_LAT = 56.1572;
//    private static final double AARHUS_LNG = 10.2107;
//    private static final double CTI_LAT = 38.2943691;
//    private static final double CTI_LNG = 21.7984254;
//
//    private What3Words w3w;
//
//    @Before
//    public void before() throws Exception {
//        final Properties props = new Properties();
//        final InputStream is = ClassLoader.getSystemResourceAsStream("application.properties.backup");
//        try {
//            props.load(is);
//            final String what3WordsApiKey = props.getProperty("w3w.key");
//            w3w = new What3Words(what3WordsApiKey);
//        } catch (IOException e) {
//            LOGGER.error(e, e);
//        }
//    }
//
//    @Test
//    public void testGeocoding() throws Exception {
//        doAssertions(commaString(w3w.positionToWords(new Coordinates(PATRAS_LAT, PATRAS_LNG))));
//        doAssertions(commaString(w3w.positionToWords(new Coordinates(CTI_LAT, CTI_LNG))));
//        doAssertions(commaString(w3w.positionToWords(new Coordinates(NEWYORK_LAT, NEWYORK_LNG))));
//        doAssertions(commaString(w3w.positionToWords(new Coordinates(LONDON_LAT, LONDON_LNG))));
//        doAssertions(commaString(w3w.positionToWords(new Coordinates(AARHUS_LAT, AARHUS_LNG))));
//    }
//
//    private void doAssertions(final String resString) {
//        LOGGER.info("resString: " + resString);
//        Assert.assertTrue(resString.contains(","));
//        final String[] parts = resString.split(",");
//        Assert.assertEquals(3, parts.length);
//        LOGGER.info("parts: " + Arrays.toString(parts));
//    }
//}
