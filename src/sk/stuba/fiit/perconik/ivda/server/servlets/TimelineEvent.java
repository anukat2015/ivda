package sk.stuba.fiit.perconik.ivda.server.servlets;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seky on 22. 7. 2014.
 * Trieda MyDataTable definuje stlpce, ktore pouzivame v klientovi.
 */
@NotThreadSafe
public final class TimelineEvent implements Serializable {
    private static final long serialVersionUID = 2235473137254607516L;
    private Date start;
    private String group;
    private ClassName className;
    private String content;
    private Date end;
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private Object metadata;

    public TimelineEvent() {
    }

    public TimelineEvent(Date start, String group, ClassName className, String content, @Nullable Date end, @Nullable Object metadata) {
        this.start = start;
        this.group = group;
        this.className = className;
        this.content = content;
        this.end = end;
        this.metadata = metadata;
    }

    /**
     * Definovane CSS styli v klientovi.
     */
    public enum ClassName {
        UNAVAILABLE("unavailable"),
        AVAILABLE("available"),
        MAYBE("maybe");

        private final String name;

        ClassName(String type) {
            name = type;
        }

        public static ClassName fromValue(String value) {
            for (ClassName item : ClassName.values()) {
                if (item.name.equals(value)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(value);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public ClassName getClassName() {
        return className;
    }

    public void setClassName(ClassName className) {
        this.className = className;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
}
