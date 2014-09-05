package sk.stuba.fiit.perconik.ivda.cord.dto;

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
}
