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
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.RegionDTO;
import gr.cti.android.experimentation.model.RegionListDTO;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.RegionRepository;
import gr.cti.android.experimentation.TestUtils;
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
public class RegionTests {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    RegionRepository regionRepository;

    private Experiment experiment;
    private TestRestTemplate template;
    private RegionListDTO regionList;

    @Before
    public void setUp() throws Exception {
        final Experiment newExperiment = TestUtils.newTestExperiment1();
        experiment = experimentRepository.save(newExperiment);

        regionList = TestUtils.newRegionList1();

        final RegionDTO region = TestUtils.newTestRegion1(experiment.getExperimentId());

        regionList.getRegions().add(region);

        template = new TestRestTemplate();
    }

    @Test
    public void testGetRegions() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/experiment/" + experiment.getExperimentId() + "/region");
        System.out.println(experiment);
        final ResponseEntity<RegionListDTO> response = template.getForEntity(url.toString(), RegionListDTO.class);
        System.out.println(response.getBody());
        assertEquals(0, response.getBody().getRegions().size());
    }

    @Test
    public void testAddRegion() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/experiment/" + experiment.getExperimentId() + "/region");
        final ResponseEntity<RegionListDTO> response = template.postForEntity(url.toString(), regionList, RegionListDTO.class);

        assertEquals(1, response.getBody().getRegions().size());
        assertEquals(regionList.getRegions().get(0).getExperimentId(), response.getBody().getRegions().get(0).getExperimentId());
        assertEquals(regionList.getRegions().get(0).getName(), response.getBody().getRegions().get(0).getName());
        assertEquals(regionList.getRegions().get(0).getCoordinates(), response.getBody().getRegions().get(0).getCoordinates());
        assertEquals(regionList.getRegions().get(0).getMaxMeasurements(), response.getBody().getRegions().get(0).getMaxMeasurements());
        assertEquals(regionList.getRegions().get(0).getMinMeasurements(), response.getBody().getRegions().get(0).getMinMeasurements());
        assertEquals(regionList.getRegions().get(0).getWeight(), response.getBody().getRegions().get(0).getWeight());
    }


    @Test
    public void testDeleteRegion() throws Exception {
        final URL url = new URL("http://localhost:" + port + "/v1/experiment/" + experiment.getExperimentId() + "/region");
        final ResponseEntity<RegionListDTO> response = template.postForEntity(url.toString(), regionList, RegionListDTO.class);

        final int regionId = response.getBody().getRegions().get(0).getId();
        final URL urlDelete = new URL("http://localhost:" + port + "/v1/experiment/" + experiment.getExperimentId() + "/region/" + regionId);
        template.delete(urlDelete.toString());

        final ResponseEntity<RegionListDTO> response2 = template.getForEntity(url.toString(), RegionListDTO.class);

        assertEquals(0, response2.getBody().getRegions().size());
    }
}
