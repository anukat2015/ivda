package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import sk.stuba.fiit.perconik.ivda.activity.dto.ApplicationEventDto;

import javax.ws.rs.core.UriBuilder;

@JsonIgnoreProperties({ "type" })
public class IdeEventDto extends ApplicationEventDto {
    private static final long serialVersionUID = -6256629269034699823L;
    /**
     * Name of the current project
     */
    private String projectName;
    /**
     * Name of the current solution/workspace
     */
    private String solutionName;

    /**
     * @return the {@link #projectName}
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param {@link #projectName}
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the {@link #solutionName}
     */
    public String getSolutionName() {
        return solutionName;
    }

    /**
     * @param {@link #solutionName}
     */
    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("ide");
    }
}
