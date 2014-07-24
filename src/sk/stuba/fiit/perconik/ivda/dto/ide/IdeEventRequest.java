package sk.stuba.fiit.perconik.ivda.dto.ide;

import sk.stuba.fiit.perconik.ivda.dto.ApplicationEventDto;

import javax.ws.rs.core.UriBuilder;

public class IdeEventRequest extends ApplicationEventDto {
    /**
     * Name of the current project
     */
    private String projectName;
    /**
     * Name of the current solution/workspace
     */
    private String solutionName;

    public IdeEventRequest() {
    }

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