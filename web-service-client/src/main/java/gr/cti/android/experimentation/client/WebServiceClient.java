package gr.cti.android.experimentation.client;

import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.ExperimentDTO;
import gr.cti.android.experimentation.model.PluginDTO;
import gr.cti.android.experimentation.model.SmartphoneStatisticsDTO;
import org.springframework.web.client.RestTemplate;

public class WebServiceClient {
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://api.smartphone-experimentation.eu/api/v1/";

    public WebServiceClient() {
        restTemplate = new RestTemplate();
    }

    public Experiment[] listExperiments() {
        return restTemplate.getForEntity(BASE_URL + "experiment", Experiment[].class).getBody();
    }

    public Experiment[] listExperiments(final int smartphoneId) {
        return restTemplate.getForEntity(BASE_URL + "experiment?phoneId=" + smartphoneId, Experiment[].class).getBody();
    }

    public Experiment getExperiment(final Integer id) {
        return restTemplate.getForEntity(BASE_URL + "experiment/" + id, ExperimentDTO.class).getBody().getValue();
    }

    public PluginDTO[] listPlugins() {
        return restTemplate.getForEntity(BASE_URL + "plugin", PluginDTO[].class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId) {
        return restTemplate.getForEntity(BASE_URL + "/smartphone/" + smartphoneId + "/statistics", SmartphoneStatisticsDTO.class).getBody();
    }

    public SmartphoneStatisticsDTO getSmartphoneStatistics(final int smartphoneId, final int experimentId) {
        return restTemplate.getForEntity(BASE_URL + "/smartphone/" + smartphoneId + "/statistics/" + experimentId, SmartphoneStatisticsDTO.class).getBody();
    }

}
