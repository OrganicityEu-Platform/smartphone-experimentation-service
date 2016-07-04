//import gr.cti.android.experimentation.client.WebServiceClient;
//import gr.cti.android.experimentation.model.Experiment;
//import gr.cti.android.experimentation.model.PluginDTO;
//import gr.cti.android.experimentation.model.SmartphoneStatisticsDTO;
//import org.junit.Before;
//import org.junit.Test;
//
//public class WebServiceClientTest {
//
//    private WebServiceClient client;
//
//    @Before
//    public void before() throws Exception {
//        client = new WebServiceClient();
//    }
//
//    @Test
//    public void testListExperiments() throws Exception {
//        final Experiment[] experiments = client.listExperiments();
//        for (final Experiment experiment : experiments) {
//            System.out.println(experiment);
//        }
//    }
//
//    @Test
//    public void testListExperimentsBySmartphoneId() throws Exception {
//        final Experiment[] experiments = client.listExperiments(15);
//        for (final Experiment experiment : experiments) {
//            System.out.println(experiment);
//        }
//    }
//
//    @Test
//    public void testGetExperiment() throws Exception {
//        final Experiment[] experiments = client.listExperiments();
//        final Experiment experiment = experiments[0];
//        final Experiment theExperiment = client.getExperiment(experiment.getId());
//        System.out.println(theExperiment);
//    }
//
//    @Test
//    public void testListPlugins() throws Exception {
//        final PluginDTO[] plugins = client.listPlugins();
//        for (final PluginDTO plugin : plugins) {
//            System.out.println(plugin);
//        }
//    }
//
//    @Test
//    public void testGetPlugin() throws Exception {
//        final Experiment[] experiments = client.listExperiments();
//        final Experiment experiment = experiments[0];
//        final Experiment theExperiment = client.getExperiment(experiment.getId());
//        System.out.println(theExperiment);
//    }
//
//    @Test
//    public void testGetSmartphoneStatistics() throws Exception {
//        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15);
//        System.out.println(smartphoneStatistics);
//    }
//
//    @Test
//    public void testGetSmartphoneStatistics2() throws Exception {
//        final SmartphoneStatisticsDTO smartphoneStatistics = client.getSmartphoneStatistics(15, 22);
//        System.out.println(smartphoneStatistics);
//    }
//
//}
