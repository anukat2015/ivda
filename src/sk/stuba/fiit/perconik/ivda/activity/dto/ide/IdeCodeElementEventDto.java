package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import javax.ws.rs.core.UriBuilder;

public class IdeCodeElementEventDto extends IdeEventDto {
    private static final long serialVersionUID = -3080849544898075703L;
    /**
     * Type of code element.
     * It should be in form of:
     * http://perconik.gratex.com/useractivity/enum/idecodeelementevent/codeelementtype#[value]
     * where value is "class", "method", "property" ...
     */

    private String codeElementTypeUri;
    /**
     * Full name of the code element
     */
    private String elementFullName;

    /**
     * @return the {@link #codeElementTypeUri}
     */
    public String getCodeElementTypeUri() {
        return codeElementTypeUri;
    }

    /**
     * @param {@link #codeElementTypeUri}
     */
    public void setCodeElementTypeUri(String codeElementTypeUri) {
        this.codeElementTypeUri = codeElementTypeUri;
    }

    /**
     * @return the {@link #elementFullName}
     */
    public String getElementFullName() {
        return elementFullName;
    }

    /**
     * @param {@link #elementFullName}
     */
    public void setElementFullName(String elementFullName) {
        this.elementFullName = elementFullName;
    }

    @Override
    protected UriBuilder getDefaultEventTypeUri() {
        return super.getDefaultEventTypeUri().path("codeelement");
    }
}