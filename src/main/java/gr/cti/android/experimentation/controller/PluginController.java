package gr.cti.android.experimentation.controller;

import gr.cti.android.experimentation.controller.api.AndroidExperimentationWS;
import gr.cti.android.experimentation.model.ApiResponse;
import gr.cti.android.experimentation.model.Experiment;
import gr.cti.android.experimentation.model.Plugin;
import gr.cti.android.experimentation.repository.ExperimentRepository;
import gr.cti.android.experimentation.repository.PluginRepository;
import gr.cti.android.experimentation.service.ModelManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class PluginController {

    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginController.class);
    @Value("${plugins.dir}")
    String pluginsDir;

    @Autowired
    ModelManager modelManager;
    @Autowired
    ExperimentRepository experimentRepository;
    @Autowired
    PluginRepository pluginRepository;

    @RequestMapping(value = "/dynamixRepository/{pluginName}.jar", method = RequestMethod.GET)
    public String downloadPlugin(@PathVariable("pluginName") final String pluginName, final HttpServletResponse httpServletResponse) {
        try {
            final InputStream is = new BufferedInputStream(new FileInputStream(new File(pluginsDir + pluginName + ".jar")));
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + pluginName + ".jar\"");
            httpServletResponse.setContentType("data:text/plaincharset=utf-8");
            FileCopyUtils.copy(is, httpServletResponse.getOutputStream());
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
        return null;
    }


    /**
     * Lists all avalialalbe plugins in the system.
     *
     * @return a json list of all available plugins in the system.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin", method = RequestMethod.GET, produces = "application/json")
    public Set<Plugin> getPluginList(@RequestParam(value = "phoneId", required = false, defaultValue = "0") final int phoneId) {
        Experiment experiment = modelManager.getEnabledExperiments().get(0);
        if (phoneId == AndroidExperimentationWS.LIDIA_PHONE_ID || phoneId == AndroidExperimentationWS.MYLONAS_PHONE_ID) {
            experiment = experimentRepository.findById(7);
        }
        final Set<String> dependencies = new HashSet<>();
        for (final String dependency : experiment.getSensorDependencies().split(",")) {
            dependencies.add(dependency);
        }
        final Set<Plugin> plugins = modelManager.getPlugins(dependencies);
        LOGGER.info("getPlugins Called: " + plugins);
        return plugins;
    }

    /**
     * Register a new plugin to the backend.
     *
     * @return a json description of the plugin.
     */
    @ResponseBody
    @RequestMapping(value = "/api/v1/plugin", method = RequestMethod.POST, produces = "application/json")
    public Object addPlugin(HttpServletResponse response, @ModelAttribute final Plugin plugin) throws IOException {
        final ApiResponse apiResponse = new ApiResponse();
        final String contextType = plugin.getContextType();
        if (contextType == null
                || plugin.getName() == null
                || plugin.getImageUrl() == null
                || plugin.getFilename() == null
                || plugin.getDescription() == null
                || plugin.getRuntimeFactoryClass() == null
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
            } else if (plugin.getDescription() == null) {
                errorMessage = "description cannot be null";
            } else if (plugin.getRuntimeFactoryClass() == null) {
                errorMessage = "runtimeFactoryClass cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            final Set<Plugin> existingPlugins = pluginRepository.findByContextType(contextType);
            if (existingPlugins.isEmpty()) {
                LOGGER.info("addPlugin: " + plugin);
                //setInstall Url
                plugin.setInstallUrl("http://195.220.224.231:8080/dynamixRepository/" + plugin.getFilename());
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
     * Update an existing plugin.
     *
     * @return a json description of the plugin.
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
                || plugin.getFilename() == null
                || plugin.getDescription() == null
                || plugin.getRuntimeFactoryClass() == null
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
            } else if (plugin.getFilename() == null) {
                errorMessage = "filename cannot be null";
            } else if (plugin.getDescription() == null) {
                errorMessage = "description cannot be null";
            } else if (plugin.getRuntimeFactoryClass() == null) {
                errorMessage = "runtimeFactoryClass cannot be null";
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            final Set<Plugin> existingPlugins = pluginRepository.findByContextType(contextType);
            for (final Plugin existingPlugin : existingPlugins) {
                if (existingPlugin.getId() != pluginId) {
                    final String errorMessage = "this context type is already reserved by another plugin";
                    response.sendError(HttpServletResponse.SC_CONFLICT, errorMessage);
                }
            }
            if (!existingPlugins.isEmpty()) {
                LOGGER.info("updatePlugin: " + plugin);
                plugin.setId((int) pluginId);
                //setInstall Url
                plugin.setInstallUrl("http://195.220.224.231:8080/dynamixRepository/" + plugin.getFilename());
                pluginRepository.save(plugin);
                apiResponse.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setMessage("ok");
                apiResponse.setValue(plugin);
                return apiResponse;
            } else {
                LOGGER.error("plugin not found: " + plugin);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "a plugin with this contextType does not exist");
            }
        }
        return null;
    }

}
