package gr.cti.android.experimentation.util;/*-
 * #%L
 * Smartphone Experimentation Client
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

import eu.organicity.discovery.dto.AssetTypeDTO;
import eu.organicity.discovery.dto.ExperimentAssetDTO;
import eu.organicity.discovery.dto.FeatureCollectionDTO;
import eu.organicity.discovery.dto.FeatureDTO;
import eu.organicity.experiment.management.dto.OCApplicationDTO;
import eu.organicity.experiment.management.dto.OCApplicationListDTO;
import eu.organicity.sitemanager.client.OrganicityClient;
import org.junit.Before;
import org.junit.Test;

public class OrganicityClientTest {

    private OrganicityClient client;

    @Before
    public void before() throws Exception {
        client = new OrganicityClient();
        client.setToken("");
        client.setEncodedToken("");
    }

    @Test
    public void testAssetTypes() throws Exception {
        final AssetTypeDTO[] assets = client.listAssetTypes();
        for (final AssetTypeDTO asset : assets) {
            System.out.println(asset);
        }
    }

    @Test
    public void testListNearbyAssets() throws Exception {
        final FeatureCollectionDTO[] assets = client.listNearbyAssets(38.246639, 21.734573, 20);
        for (final FeatureCollectionDTO asset : assets) {
            System.out.println(asset.getProperties().getName());
            for (final FeatureDTO featureDTO : asset.getFeatures()) {
                System.out.println("\t" + featureDTO.getProperties().getId() + " " + featureDTO.getGeometry());
            }
        }
    }


    @Test
    public void testListExperiments() throws Exception {
        OCApplicationListDTO applications = client.listApplications();
        for (final OCApplicationDTO application : applications.getApplications()) {
            System.out.println(application);
        }
    }

    @Test
    public void testListExperimentAssets() throws Exception {
        ExperimentAssetDTO[] experimentAssetDTOs = client.listExperimentAssets("5820b17baeb046575877af53");
        for (final ExperimentAssetDTO experimentAssetDTO : experimentAssetDTOs) {
            System.out.println(experimentAssetDTO);
        }

        experimentAssetDTOs = client.listExperimentAssets("57f210e59324fdd11103d93c");
        for (final ExperimentAssetDTO experimentAssetDTO : experimentAssetDTOs) {
            System.out.println(experimentAssetDTO);
        }
    }

}
