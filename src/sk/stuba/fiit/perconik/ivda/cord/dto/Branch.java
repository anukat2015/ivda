package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 */
public final class Branch implements Serializable {
    private static final long serialVersionUID = 1217658377635142108L;
    private String name;
    private String head;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;

        if (name != null ? !name.equals(branch.name) : branch.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("head", head).append("name", name).toString();
    }
}
