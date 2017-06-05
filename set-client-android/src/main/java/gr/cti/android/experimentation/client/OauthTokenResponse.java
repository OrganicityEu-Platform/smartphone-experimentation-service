package gr.cti.android.experimentation.client;

/*-
 * #%L
 * SET Android Client
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OauthTokenResponse {
    private String access_token;
    private int expires_in;
    private int refresh_expires_in;
    private String refresh_token;
    private String token_type;
    private String id_token;
    
    public String getAccess_token() {
        return access_token;
    }
    
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    
    public int getExpires_in() {
        return expires_in;
    }
    
    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
    
    public int getRefresh_expires_in() {
        return refresh_expires_in;
    }
    
    public void setRefresh_expires_in(int refresh_expires_in) {
        this.refresh_expires_in = refresh_expires_in;
    }
    
    public String getRefresh_token() {
        return refresh_token;
    }
    
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    
    public String getToken_type() {
        return token_type;
    }
    
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    
    public String getId_token() {
        return id_token;
    }
    
    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
    
    @Override
    public String toString() {
        return "OauthTokenResponse{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_expires_in=" + refresh_expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", id_token='" + id_token + '\'' +
                '}';
    }
}
