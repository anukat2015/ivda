package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 * Contains informatio nabout AST parseerror
 */
public final class AstParseErrorDto implements Serializable {
    private static final long serialVersionUID = 985979756205158009L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstParseErrorDto that = (AstParseErrorDto) o;

        if (endColumn != null ? !endColumn.equals(that.endColumn) : that.endColumn != null) return false;
        if (endLine != null ? !endLine.equals(that.endLine) : that.endLine != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (startColumn != null ? !startColumn.equals(that.startColumn) : that.startColumn != null) return false;
        if (startLine != null ? !startLine.equals(that.startLine) : that.startLine != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (startLine != null ? startLine.hashCode() : 0);
        result = 31 * result + (endLine != null ? endLine.hashCode() : 0);
        result = 31 * result + (startColumn != null ? startColumn.hashCode() : 0);
        result = 31 * result + (endColumn != null ? endColumn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("endColumn", endColumn).append("endLine", endLine).append("level", level).append("message", message).append("startColumn", startColumn).append("startLine", startLine).toString();
    }
}
