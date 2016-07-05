//package gr.cti.android.experimentation.test;

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
//import gr.cti.android.experimentation.service.CityService;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * Created by amaxilatis on 24/11/2015.
// */
//public class GeocodingTest {
//
//    private CityService cityService;
//
//    private double patras_lat = 38.2500;
//    private double patras_lng = 21.7333;
//    private double newyork_lat = 40.7127;
//    private double newyork_lng = -74.0059;
//    private double london_lat = 51.5072;
//    private double london_lng = -0.1275;
//    private double aarhus_lat = 56.1572;
//    private double aarhus_lng = 10.2107;
//    private double cti_lat = 38.2943691;
//    private double cti_lng = 21.7984254;
//
//    @Before
//    public void before() throws Exception {
//        cityService = new CityService();
//        cityService.setApiKey("AIzaSyAh6fdHmyeJx4KUn1LM9OSpOwEv1ry9NXg");
//        cityService.init();
//    }
//
//    @Test
//    public void testGeocoding() throws Exception {
//        for (int i = 0; i < 10; i++) {
//            System.out.println(cityService.findLocation(patras_lat, patras_lng));
//            System.out.println(cityService.findLocation(cti_lat, cti_lng));
//            System.out.println(cityService.findLocation(newyork_lat, newyork_lng));
//            System.out.println(cityService.findLocation(london_lat, london_lng));
//            System.out.println(cityService.findLocation(aarhus_lat, aarhus_lng));
//        }
//    }
//
//}
