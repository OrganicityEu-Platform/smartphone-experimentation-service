package gr.cti.android.experimentation.util;

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

import io.jsonwebtoken.Claims;
import org.keycloak.KeycloakPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

public final class OrganicityAccount extends KeycloakPrincipal {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganicityAccount.class);
    private String id;
    private String user;
    private Date expiration;
    private String email;
    private Collection<? extends GrantedAuthority> roles;
    
    public OrganicityAccount(KeycloakPrincipal k, Collection<? extends GrantedAuthority> authorities) {
        super(k.getName(), k.getKeycloakSecurityContext());
        roles = authorities;
    }
    
    public void parse() throws Exception {
        try {
            JwtParser fwtparser = new JwtParser();
            Claims claims = fwtparser.parseJWT(super.getKeycloakSecurityContext().getTokenString());
            id = claims.getId();
            user = claims.getSubject();
            expiration = claims.getExpiration();
            email = (String) claims.get("email");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public String getUser() {
        return user;
    }
    
    public Date getExpiration() {
        return expiration;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isExperimenter() {
        for (GrantedAuthority role : roles) {
            if (role.getAuthority().equals("experimenter"))
                return true;
        }
        return false;
    }
    
    public boolean isAdministrator() {
        for (GrantedAuthority role : roles) {
            if (role.getAuthority().equals("administrator"))
                return true;
        }
        return false;
    }
    
    public boolean isParticipant(String experimentId) {
        if (experimentId == null)
            return false;
        else
            return true;
    }
    
    public boolean ownsExperiment(String experimentId) {
        if (!isExperimenter())
            return false;
        return false;
    }
    
    
    
    @Override
    public String toString() {
        return "OrganicityAccount{" + "id='" + id + '\'' + ", user='" + user + '\'' + ", expiration=" + expiration + ", email='" + email + '\'' + ", roles=" + roles + '}';
    }
}
