package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 * Containsinformationabout AST parseerror
 */
public class AstParseErrorDto implements Serializable {
    private String message;
    private String level;
    private Integer startLine;
    private Integer endLine;
    private Integer startColumn;
    private Integer endColumn;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public Integer getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    public Integer getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(Integer endColumn) {
        this.endColumn = endColumn;
    }
}
