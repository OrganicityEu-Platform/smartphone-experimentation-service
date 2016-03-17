package gr.cti.android.experimentation.test;


import gr.cti.android.experimentation.service.CityService;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by amaxilatis on 24/11/2015.
 */
public class GeocodingTest {

    private CityService cityService;

    private double patras_lat = 38.2500;
    private double patras_lng = 21.7333;
    private double newyork_lat = 40.7127;
    private double newyork_lng = -74.0059;
    private double london_lat = 51.5072;
    private double london_lng = -0.1275;
    private double aarhus_lat = 56.1572;
    private double aarhus_lng = 10.2107;
    private double cti_lat = 38.2943691;
    private double cti_lng = 21.7984254;

    @Before
    public void before() throws Exception {
        cityService = new CityService();
        cityService.setApiKey("AIzaSyAh6fdHmyeJx4KUn1LM9OSpOwEv1ry9NXg");
        cityService.init();
    }

    @Test
    public void testGeocoding() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println(cityService.findLocation(patras_lat, patras_lng));
            System.out.println(cityService.findLocation(cti_lat, cti_lng));
            System.out.println(cityService.findLocation(newyork_lat, newyork_lng));
            System.out.println(cityService.findLocation(london_lat, london_lng));
            System.out.println(cityService.findLocation(aarhus_lat, aarhus_lng));
        }
    }

}
