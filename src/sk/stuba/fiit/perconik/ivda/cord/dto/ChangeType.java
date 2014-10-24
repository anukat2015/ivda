package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 */
public enum ChangeType implements Serializable {
    UNCHANGED("unchanged"),
    ADD("add"),
    DELETE("delete"),
    MODIFY("modify"),
    RENAME("rename"),
    COPY("copy");

    final String value;

    ChangeType(String x) {
        value = x;
    }

    public String toString() {
        return value;
    }
}
