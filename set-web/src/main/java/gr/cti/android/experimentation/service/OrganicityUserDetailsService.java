package gr.cti.android.experimentation.service;

/*-
 * #%L
 * SET Web Interface
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2018 CTI - Computer Technology Institute and Press "Diophantus"
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


import gr.cti.android.experimentation.util.OrganicityAccount;
import org.keycloak.KeycloakPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class OrganicityUserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganicityUserDetailsService.class);
    
    public static OrganicityAccount getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            try {
                OrganicityAccount oa = new OrganicityAccount((KeycloakPrincipal) authentication.getPrincipal(), authentication.getAuthorities());
                oa.parse();
                return oa;
            } catch (Exception e) {
                SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                SecurityContextHolder.clearContext();
                return null;
            }
        }
        return null;
    }
}