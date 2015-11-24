package gr.cti.android.experimentation.service;

import com.amaxilatis.orion.OrionClient;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import eu.organicity.entities.handler.attributes.Attribute;
import eu.organicity.entities.handler.entities.SmartphoneDevice;
import eu.organicity.entities.handler.metadata.Datatype;
import eu.organicity.entities.namespace.OrganicityAttributeTypes;
import eu.organicity.entities.namespace.OrganicityDatatypes;
import gr.cti.android.experimentation.model.Result;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * Provides connection to the google geoCoding api to retrieve the address of the city name near which the data is collected.
 */
@Service
public class CityService {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(CityService.class);

    @Value("${geoapicontext.apikey}")
    private String apiKey;

    private GeoApiContext context;

    private Map<String, String> locationMap;

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    @PostConstruct
    public void init() {
        context = new GeoApiContext().setApiKey(apiKey);
        this.locationMap = new HashMap<>();
    }


    public String findLocation(double lat, double lng) throws Exception {
        final LatLng location = new LatLng(lat, lng);

        if (locationMap.containsKey(location.toString())) {
            return locationMap.get(location.toString());
        }

        final GeocodingResult[] results = GeocodingApi.reverseGeocode(context, location).await();
        String level4 = null;
        String locality = null;
        for (final GeocodingResult result : results) {
            final Set<String> typeSet = new HashSet<>();
            for (final AddressType type : result.types) {
                typeSet.add(type.toString());
            }
            if (typeSet.contains("administrative_area_level_4") && typeSet.contains("political")) {
                level4 = result.formattedAddress.split(",")[0].toLowerCase().replaceAll(" ", "");
            }
            if (typeSet.contains("locality") && typeSet.contains("political")) {
                locality = result.formattedAddress.split(",")[0].toLowerCase().replaceAll(" ", "");
            }
        }

        if (level4 != null) {
            locationMap.put(location.toString(), level4);
        } else {
            if (locality != null) {
                locationMap.put(location.toString(), locality);
            }
        }

        return locationMap.get(location.toString());
    }
}
