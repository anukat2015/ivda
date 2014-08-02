package sk.stuba.fiit.perconik.ivda.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class ProcessDto implements Serializable {
    private String name;
    private int pid;

    public ProcessDto() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
