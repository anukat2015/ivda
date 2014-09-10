package sk.stuba.fiit.perconik.ivda.activity.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class ProcessDto implements Serializable {
    private static final long serialVersionUID = 1844954611978415458L;
    private String name;
    private int pid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
