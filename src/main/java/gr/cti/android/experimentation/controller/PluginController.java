package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.Plugin;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class PluginController extends BaseController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginController.class);

    /**
     * Lists all available plugins in the system.
     *
     * @param phoneId the id of the phone requesting the list of available plugins.
     * @return a json list of all available plugins in the system.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin", method = RequestMethod.GET, produces = "application/json")
    public Set<Plugin> getPluginList(
            @RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId,
            @RequestParam(value = "userId", required = false) final Long userId,
            @RequestParam(value = "type", required = false, defaultValue = "live") final String type) {
        if (userId != null) {
            return pluginRepository.findByUserId(userId);
        } else {
            return pluginRepository.findAll();
        }
    }

    /**
     * Register a new plugin to the backend.
     *
     * @param response the HTTP response object.
     * @param plugin   the plugin object to register.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin", method = RequestMethod.POST, produces = "application/json")
    public Object addPlugin(HttpServletRequest request, HttpServletResponse response, @ModelAttribute final Plugin plugin) throws IOException {
        LOGGER.info(request.getRemoteAddr());

        final ApiResponse apiResponse = new ApiResponse();
        final String contextType = plugin.getContextType();
        if (contextType == null
                || plugin.getName() == null
                || plugin.getImageUrl() == null
                || plugin.getFilename() == null
                || plugin.getInstallUrl() == null
                || plugin.getDescription() == null
                || plugin.getRuntimeFactoryClass() == null
                || plugin.getUserId() == null
                ) {
            LOGGER.info("wrong data: " + plugin);
            String errorMessage = "error";
            if (contextType == null) {
                errorMessage = "contextType cannot be null";
            } else if (plugin.getName() == null) {
                errorMessage = "name cannot be null";
            } else if (plugin.getImageUrl() == null) {
                errorMessage = "imageUrl cannot be null";
            } else if (plugin.getFilename() == null) {
                errorMessage = "filename cannot be null";
            } else if (plugin.getInstallUrl() == null) {
                errorMessage = "url cannot be null";
            } else if (plugin.getDescription() == null) {
                errorMessage = "description cannot be null";
            } else if (plugin.getRuntimeFactoryClass() == null) {
                errorMessage = "runtimeFactoryClass cannot be null";
            } else if (plugin.getUserId() == null) {
                errorMessage = "userId cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            final Set<Plugin> existingPlugins = pluginRepository.findByContextType(contextType);
            if (existingPlugins.isEmpty()) {
                LOGGER.info("addPlugin: " + plugin);
                pluginRepository.save(plugin);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(plugin);
                return apiResponse;
            } else {
                LOGGER.info("plugin exists: " + plugin);
                response.sendError(HttpServletResponse.SC_CONFLICT, "a plugin with this contextType already exists");
            }
        }
        return null;
    }

    /**
     * Get an existing plugin.
     *
     * @param response the HTTP response object.
     * @param pluginId the id of the plugin to update.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin/{pluginId}", method = RequestMethod.GET, produces = "application/json")
    public Object getPlugin(HttpServletResponse response, @PathVariable("pluginId") final long pluginId) throws IOException {

        final ApiResponse apiResponse = new ApiResponse();

        final Plugin storedPlugin = pluginRepository.findById((int) pluginId);
        if (storedPlugin != null) {
            LOGGER.info("getPlugin: " + storedPlugin);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(storedPlugin);
            return apiResponse;
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "no plugin found with the given id");
        }
        return null;
    }

    /**
     * Update an existing plugin.
     *
     * @param response the HTTP response object.
     * @param plugin   the plugin object to update.
     * @param pluginId the id of the plugin to update.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin/{pluginId}", method = RequestMethod.POST, produces = "application/json")
    public Object addPlugin(HttpServletResponse response, @ModelAttribute final Plugin plugin, @PathVariable("pluginId") final long pluginId) throws IOException {

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
            if (storedPlugin != null) {

                final Set<Plugin> existingPlugins = pluginRepository.findByContextType(contextType);
                for (final Plugin existingPlugin : existingPlugins) {
                    if (existingPlugin.getId() != pluginId) {
                        final String errorMessage = "this context type is already reserved by another plugin";
                        response.sendError(HttpServletResponse.SC_CONFLICT, errorMessage);
                    }
                }

                LOGGER.info("updatePlugin: " + plugin);
                plugin.setId((int) pluginId);
                if (plugin.getFilename() == null) {
                    plugin.setFilename(storedPlugin.getFilename());
                }
                if (plugin.getInstallUrl() == null) {
                    plugin.setInstallUrl(storedPlugin.getInstallUrl());
                }
                pluginRepository.save(plugin);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(plugin);
                return apiResponse;
            } else {
                LOGGER.error("plugin not found: " + plugin);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "a plugin with this id does not exist");
            }
        }
        return null;
    }

    /**
     * Delete an existing plugin.
     *
     * @param response the HTTP response object.
     * @param pluginId the id of the plugin to delete.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin/{pluginId}", method = RequestMethod.DELETE, produces = "application/json")
    public Object deletePlugin(HttpServletRequest request, HttpServletResponse response, @PathVariable("pluginId") final int pluginId) throws IOException {

        final ApiResponse apiResponse = new ApiResponse();
        final Plugin plugin = pluginRepository.findById(pluginId);
        if (plugin == null) {
            final String errorMessage = "no plugin found with this id";
            response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMessage);
        } else {
            pluginRepository.delete(plugin);
            apiResponse.setStatus(HttpServletResponse.SC_OK);
            apiResponse.setMessage("ok");
            apiResponse.setValue(plugin);
            return apiResponse;
        }
        return null;
    }
}
