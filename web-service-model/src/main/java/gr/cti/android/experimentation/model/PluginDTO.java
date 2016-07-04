package gr.cti.android.experimentation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginDTO implements Serializable {
    private int id;
    private String name;
    private String contextType;
    private String runtimeFactoryClass;
    private String description;
    private String installUrl;
    private String imageUrl;
    private String filename;
    @JsonIgnore
    private Long userId;

    public PluginDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public String getRuntimeFactoryClass() {
        return runtimeFactoryClass;
    }

    public void setRuntimeFactoryClass(String runtimeFactoryClass) {
        this.runtimeFactoryClass = runtimeFactoryClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "contextType='" + contextType + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
