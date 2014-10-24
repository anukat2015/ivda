package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class BaseType implements Serializable {
    private static final long serialVersionUID = 3505850979464232142L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseType baseType = (BaseType) o;

        if (modifier != null ? !modifier.equals(baseType.modifier) : baseType.modifier != null) return false;
        if (typeName != null ? !typeName.equals(baseType.typeName) : baseType.typeName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = typeName != null ? typeName.hashCode() : 0;
        result = 31 * result + (modifier != null ? modifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("modifier", modifier).append("typeName", typeName).toString();
    }
}
