package gr.cti.android.experimentation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: theodori
 * Date: 9/4/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Experiment implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;
    private String description;
    private String urlDescription;
    private Long timestamp;
    private String name;
    private String contextType;
    private String sensorDependencies;
    private String status;
    private Integer userId;
    private String url;
    private String filename;
    @JsonIgnore
    private Boolean enabled;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getSensorDependencies() {
        return sensorDependencies;
    }

    public void setSensorDependencies(String sensorDependencies) {
        this.sensorDependencies = sensorDependencies;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrlDescription() {
        return urlDescription;
    }

    public void setUrlDescription(String urlDescription) {
        this.urlDescription = urlDescription;
    }


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Experiment that = (Experiment) o;

        if (!Objects.equals(id, that.id)) return false;
        if (contextType != null ? !contextType.equals(that.contextType) : that.contextType != null) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (sensorDependencies != null ? !sensorDependencies.equals(that.sensorDependencies) : that.sensorDependencies != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return !(userId != null ? !userId.equals(that.userId) : that.userId != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (contextType != null ? contextType.hashCode() : 0);
        result = 31 * result + (sensorDependencies != null ? sensorDependencies.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }
}
