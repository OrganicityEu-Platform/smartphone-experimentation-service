package gr.cti.android.experimentation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: theodori
 * Date: 9/4/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plugin implements Serializable {
    @Id
    @GeneratedValue
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

    public Plugin() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plugin that = (Plugin) o;

        if (id != that.id) return false;
        if (contextType != null ? !contextType.equals(that.contextType) : that.contextType != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (installUrl != null ? !installUrl.equals(that.installUrl) : that.installUrl != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(runtimeFactoryClass != null ? !runtimeFactoryClass.equals(that.runtimeFactoryClass) : that.runtimeFactoryClass != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (contextType != null ? contextType.hashCode() : 0);
        result = 31 * result + (runtimeFactoryClass != null ? runtimeFactoryClass.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (installUrl != null ? installUrl.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
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
