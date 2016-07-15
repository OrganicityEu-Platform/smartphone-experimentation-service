package gr.cti.android.experimentation.test;

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
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.ExperimentDTO;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class ExperimentTests {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    ExperimentRepository experimentRepository;

    private Experiment experiment;
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        final Experiment newExperiment = TestUtils.newTestExperiment1();

        experiment = experimentRepository.save(newExperiment);
        template = new TestRestTemplate();
    }

    @Test
    public void testGetExperiment() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/experiment/" + experiment.getId());
        final ResponseEntity<ExperimentDTO> response = template.getForEntity(url.toString(), ExperimentDTO.class);

        assertEquals(experiment, response.getBody().getValue());
    }

    @Test
    public void testListExperiments() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/experiment");
        final ResponseEntity<Experiment[]> response = template.getForEntity(url.toString(), Experiment[].class);

        assertEquals(1, response.getBody().length);
        assertEquals(experiment, response.getBody()[0]);
    }
}
