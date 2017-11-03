package gr.cti.android.experimentation.controller.api;

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

import gr.cti.android.experimentation.controller.BaseController;
import gr.cti.android.experimentation.exception.NotAuthorizedException;
import gr.cti.android.experimentation.exception.PluginNotFoundException;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.Plugin;
import gr.cti.android.experimentation.model.PluginDTO;
import gr.cti.android.experimentation.model.PluginListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@RestController
@RequestMapping(value = {"/api/v1", "/v1"})
public class PluginController extends BaseController {
    
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginController.class);

    /**
     * Lists all available plugins in the system.
     *
     * @param principal the user principal.
     * @param phoneId   the id of the phone requesting the list of available plugins.
     * @return a json list of all available plugins in the system.
     */
    @RequestMapping(value = "/plugin", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public PluginListDTO getPluginList(
            final Principal principal,
            @RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId,
            @RequestParam(value = "type", required = false, defaultValue = "live") final String type)
            throws NotAuthorizedException {
        LOGGER.info(String.format("GET /plugin %s", principal));

        PluginListDTO pluginListDTO = new PluginListDTO();
        pluginListDTO.setPlugins(new ArrayList<>());
        final Set<Plugin> plugins = pluginRepository.findAll();
        for (Plugin plugin : plugins) {
            if (plugin.isEnabled()) {
                pluginListDTO.getPlugins().add(newPluginDTO(plugin));
            } else if (principal != null && plugin.getUserId() != null && plugin.getUserId().contains(principal.getName())) {
                pluginListDTO.getPlugins().add(newPluginDTO(plugin));
            }
        }
        return pluginListDTO;
    }

    /**
     * Register a new plugin to the backend.
     *
     * @param principal the user principal.
     * @param response  the HTTP response object.
     * @param pluginDTO the plugin object to register.
     */
    @RequestMapping(value = "/plugin", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public Object addPlugin(final Principal principal, HttpServletResponse response,
                            @ModelAttribute final PluginDTO pluginDTO) throws IOException, NotAuthorizedException {
        LOGGER.info(String.format("POST /plugin %s", principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }

        final ApiResponse apiResponse = new ApiResponse();
        final String contextType = pluginDTO.getContextType();
        if (contextType == null
                || pluginDTO.getName() == null
                || pluginDTO.getImageUrl() == null
                || pluginDTO.getFilename() == null
                || pluginDTO.getInstallUrl() == null
                || pluginDTO.getDescription() == null
                || pluginDTO.getRuntimeFactoryClass() == null
                ) {
            LOGGER.info("wrong data: " + pluginDTO);
            String errorMessage = "error";
            if (contextType == null) {
                errorMessage = "contextType cannot be null";
            } else if (pluginDTO.getName() == null) {
                errorMessage = "name cannot be null";
            } else if (pluginDTO.getImageUrl() == null) {
                errorMessage = "imageUrl cannot be null";
            } else if (pluginDTO.getFilename() == null) {
                errorMessage = "filename cannot be null";
            } else if (pluginDTO.getInstallUrl() == null) {
                errorMessage = "url cannot be null";
            } else if (pluginDTO.getDescription() == null) {
                errorMessage = "description cannot be null";
            } else if (pluginDTO.getRuntimeFactoryClass() == null) {
                errorMessage = "runtimeFactoryClass cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            final Set<Plugin> existingPlugins = pluginRepository.findByContextType(contextType);
            if (existingPlugins.isEmpty()) {
                LOGGER.info("addPlugin: " + pluginDTO);
                Plugin plugin = newPlugin(pluginDTO);
                plugin.setEnabled(false);
                plugin.setPublicList(false);
                plugin.setUserId(principal.getName());
                pluginRepository.save(plugin);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(pluginDTO);
                return apiResponse;
            } else {
                LOGGER.info("plugin exists: " + pluginDTO);
                response.sendError(HttpServletResponse.SC_CONFLICT, "a plugin with this contextType already exists");
            }
        }
        return null;
    }


    /**
     * Get an existing plugin.
     *
     * @param principal the user principal.
     * @param pluginId  the id of the plugin to update.
     */
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
    public Object getPlugin(final Principal principal, @PathVariable("pluginId") final long pluginId)
            throws PluginNotFoundException {
        LOGGER.info(String.format("GET /plugin/%d %s", pluginId, principal));

        final ApiResponse apiResponse = new ApiResponse();

        final Plugin storedPlugin = pluginRepository.findById((int) pluginId);
        if (storedPlugin != null) {
            LOGGER.info("getPlugin: " + storedPlugin);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(storedPlugin);
            return apiResponse;
        } else {
            throw new PluginNotFoundException();
        }
    }

    /**
     * Update an existing plugin.
     *
     * @param principal the user principal.
     * @param response  the HTTP response object.
     * @param plugin    the plugin object to update.
     * @param pluginId  the id of the plugin to update.
     */
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.POST, produces = APPLICATION_JSON)
    public Object addPlugin(final Principal principal, HttpServletResponse response,
                            @ModelAttribute final Plugin plugin,
                            @PathVariable("pluginId") final long pluginId) throws IOException, PluginNotFoundException, NotAuthorizedException {
        LOGGER.info(String.format("POST /plugin/%d %s", pluginId, principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }

        final ApiResponse apiResponse = new ApiResponse();
        final String contextType = plugin.getContextType();
        if (pluginId != plugin.getId()
                || contextType == null
                || plugin.getName() == null
                || plugin.getImageUrl() == null
//                || plugin.getFilename() == null
//                || plugin.getInstallUrl() == null
                || plugin.getDescription() == null
                || plugin.getRuntimeFactoryClass() == null
                || plugin.getUserId() == null
                ) {
            LOGGER.error("wrong data: " + plugin);
            String errorMessage = "error";
            if (pluginId != plugin.getId()) {
                errorMessage = "pluginId does not match submitted plugin description";
            } else if (contextType == null) {
                errorMessage = "contextType cannot be null";
            } else if (plugin.getName() == null) {
                errorMessage = "name cannot be null";
            } else if (plugin.getImageUrl() == null) {
                errorMessage = "imageUrl cannot be null";
//            } else if (plugin.getFilename() == null) {
//                errorMessage = "filename cannot be null";
//            } else if (plugin.getInstallUrl() == null) {
//                errorMessage = "url cannot be null";
            } else if (plugin.getDescription() == null) {
                errorMessage = "description cannot be null";
            } else if (plugin.getRuntimeFactoryClass() == null) {
                errorMessage = "runtimeFactoryClass cannot be null";
            } else if (plugin.getUserId() == null) {
                errorMessage = "userId cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {

            final Plugin storedPlugin = pluginRepository.findById((int) pluginId);
            if (storedPlugin == null) {
                throw new PluginNotFoundException();
            } else {
                if (!isPluginOfUser(plugin, principal)) {
                    throw new PluginNotFoundException();
                }

                final Set<Plugin> existingPlugins = pluginRepository.findByContextType(contextType);
                for (final Plugin existingPlugin : existingPlugins) {
                    if (existingPlugin.getId() != pluginId) {
                        final String errorMessage = "this context type is already reserved by another plugin";
                        response.sendError(HttpServletResponse.SC_CONFLICT, errorMessage);
                    }
                }

                LOGGER.info("updatePlugin: " + plugin);
                plugin.setId((int) pluginId);
                if (plugin.getFilename() == null || plugin.getFilename().equals("")) {
                    plugin.setFilename(storedPlugin.getFilename());
                }
                if (plugin.getInstallUrl() == null || plugin.getInstallUrl().equals("")) {
                    plugin.setInstallUrl(storedPlugin.getInstallUrl());
                }
                pluginRepository.save(plugin);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(plugin);
                return apiResponse;
            }
        }
        return null;
    }

    /**
     * Delete an existing plugin.
     *
     * @param principal the user principal.
     * @param response  the HTTP response object.
     * @param pluginId  the id of the plugin to delete.
     */
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    public Object deletePlugin(final Principal principal,
                               HttpServletResponse response, @PathVariable("pluginId") final int pluginId)
            throws NotAuthorizedException, PluginNotFoundException {
        LOGGER.info(String.format("DELETE /plugin/%d %s", pluginId, principal));

        if (principal == null) {
            throw new NotAuthorizedException();
        }

        final ApiResponse apiResponse = new ApiResponse();
        final Plugin plugin = pluginRepository.findById(pluginId);
        if (plugin == null) {
            throw new PluginNotFoundException();
        } else {
            if (!isPluginOfUser(plugin, principal)) {
                throw new PluginNotFoundException();
            }

            pluginRepository.delete(plugin);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(plugin);
            return apiResponse;
        }
    }
}
