package sk.stuba.fiit.perconik.ivda.server.servlets;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seky on 22. 7. 2014.
 * Trieda MyDataTable definuje stlpce, ktore pouzivame v klientovi.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class IvdaEvent implements Serializable {
    private static final long serialVersionUID = 2235473137254607516L;
    private Date start;
    private Date end;
    private String group;
    private String content;
    private Integer y;

    public IvdaEvent() {
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
