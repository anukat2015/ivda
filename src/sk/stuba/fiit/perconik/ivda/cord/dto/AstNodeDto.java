package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 5. 9. 2014.
 */
public class AstNodeDto implements Serializable {
    private String nodeType;
    private String name;
    private Integer startLine;
    private Integer endLine;
    private Integer startIndex;
    private Integer endIndex;
    private Integer commentStartLine;
    private Integer commentEndLine;
    private String modifier;

    private BaseType returnType;
    private List<Parameter> parameters;
    private List<BaseType> baseTypes;
    private List<AstNodeDto> childNodes;

    public AstNodeDto() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstNodeDto that = (AstNodeDto) o;

        if (baseTypes != null ? !baseTypes.equals(that.baseTypes) : that.baseTypes != null) return false;
        if (childNodes != null ? !childNodes.equals(that.childNodes) : that.childNodes != null) return false;
        if (commentEndLine != null ? !commentEndLine.equals(that.commentEndLine) : that.commentEndLine != null)
            return false;
        if (commentStartLine != null ? !commentStartLine.equals(that.commentStartLine) : that.commentStartLine != null)
            return false;
        if (endIndex != null ? !endIndex.equals(that.endIndex) : that.endIndex != null) return false;
        if (endLine != null ? !endLine.equals(that.endLine) : that.endLine != null) return false;
        if (modifier != null ? !modifier.equals(that.modifier) : that.modifier != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (nodeType != null ? !nodeType.equals(that.nodeType) : that.nodeType != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (returnType != null ? !returnType.equals(that.returnType) : that.returnType != null) return false;
        if (startIndex != null ? !startIndex.equals(that.startIndex) : that.startIndex != null) return false;
        if (startLine != null ? !startLine.equals(that.startLine) : that.startLine != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeType != null ? nodeType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (startLine != null ? startLine.hashCode() : 0);
        result = 31 * result + (endLine != null ? endLine.hashCode() : 0);
        result = 31 * result + (startIndex != null ? startIndex.hashCode() : 0);
        result = 31 * result + (endIndex != null ? endIndex.hashCode() : 0);
        result = 31 * result + (commentStartLine != null ? commentStartLine.hashCode() : 0);
        result = 31 * result + (commentEndLine != null ? commentEndLine.hashCode() : 0);
        result = 31 * result + (modifier != null ? modifier.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (baseTypes != null ? baseTypes.hashCode() : 0);
        result = 31 * result + (childNodes != null ? childNodes.hashCode() : 0);
        return result;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getCommentStartLine() {
        return commentStartLine;
    }

    public void setCommentStartLine(Integer commentStartLine) {
        this.commentStartLine = commentStartLine;
    }

    public Integer getCommentEndLine() {
        return commentEndLine;
    }

    public void setCommentEndLine(Integer commentEndLine) {
        this.commentEndLine = commentEndLine;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public BaseType getReturnType() {
        return returnType;
    }

    public void setReturnType(BaseType returnType) {
        this.returnType = returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<BaseType> getBaseTypes() {
        return baseTypes;
    }

    public void setBaseTypes(List<BaseType> baseTypes) {
        this.baseTypes = baseTypes;
    }

    public List<AstNodeDto> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<AstNodeDto> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("baseTypes", baseTypes).append("childNodes", childNodes).append("commentEndLine", commentEndLine).append("commentStartLine", commentStartLine).append("endIndex", endIndex).append("endLine", endLine).append("modifier", modifier).append("name", name).append("nodeType", nodeType).append("parameters", parameters).append("returnType", returnType).append("startIndex", startIndex).append("startLine", startLine).toString();
    }
}
