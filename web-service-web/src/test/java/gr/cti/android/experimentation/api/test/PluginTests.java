package gr.cti.android.experimentation.api.test;

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


import gr.cti.android.experimentation.Application;
import gr.cti.android.experimentation.TestUtils;
import gr.cti.android.experimentation.controller.PluginController;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.Plugin;
import gr.cti.android.experimentation.model.PluginListDTO;
import gr.cti.android.experimentation.repository.PluginRepository;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class PluginTests {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginController.class);

    @Value("${local.server.port}")
    private int port;

    @Autowired
    PluginRepository pluginRepository;

    private Plugin plugin;
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        final Plugin newPlugin = TestUtils.newTestPlugin1();

        pluginRepository.deleteAll();

        plugin = pluginRepository.save(newPlugin);
        LOGGER.debug("plugin: " + plugin.getId());
        template = new TestRestTemplate();
    }

    @Test
    public void testGetPlugin() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/plugin/" + plugin.getId());
        final ResponseEntity<ApiResponse> response = template.getForEntity(url.toString(), ApiResponse.class);

        Map<String, Object> pluginResponse = (Map<String, Object>) response.getBody().getValue();
        assertEquals(plugin.getName(), pluginResponse.get("name"));
        assertEquals(plugin.getContextType(), pluginResponse.get("contextType"));
        assertEquals(plugin.getRuntimeFactoryClass(), pluginResponse.get("runtimeFactoryClass"));
        assertEquals(plugin.getDescription(), pluginResponse.get("description"));
        assertEquals(plugin.getInstallUrl(), pluginResponse.get("installUrl"));
        assertEquals(plugin.getImageUrl(), pluginResponse.get("imageUrl"));
        assertEquals(plugin.getFilename(), pluginResponse.get("filename"));
    }

    @Test
    public void testListPlugins() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/plugin");
        final ResponseEntity<PluginListDTO> response = template.getForEntity(url.toString(), PluginListDTO.class);

        assertEquals(1, response.getBody().getPlugins().size());
        assertEquals(plugin.getName(), response.getBody().getPlugins().get(0).getName());
    }

    @Test
    public void testDeletePlugin() throws Exception {
        final URL listUrl = new URL("http://localhost:" + port + "/v1/plugin");
        final ResponseEntity<PluginListDTO> firstResponse = template.getForEntity(listUrl.toString(), PluginListDTO.class);
        int plugins = firstResponse.getBody().getPlugins().size();

        final URL url = new URL("http://localhost:" + port + "/v1/plugin/" + plugin.getId());

        final ResponseEntity<ApiResponse> deleteResponse
                = template.exchange(url.toString(), HttpMethod.DELETE, new HttpEntity<>(""), ApiResponse.class);
        if (deleteResponse.getStatusCode() == HttpStatus.OK) {
            final ResponseEntity<PluginListDTO> response = template.getForEntity(listUrl.toString(), PluginListDTO.class);
            assertEquals(plugins - 1, response.getBody().getPlugins().size());
        } else {
            assertEquals(HttpStatus.UNAUTHORIZED, deleteResponse.getStatusCode());
        }
    }
}
