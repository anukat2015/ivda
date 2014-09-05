package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class BaseType implements Serializable {
    private String typeName;
    private String modifier;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
