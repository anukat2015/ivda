package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 5. 9. 2014.
 * Contains file content as AST
 */
public class AstParseResultDto implements Serializable {
    private List<AstParseErrorDto> parseErrors;
    private AstNodeDto syntaxTree;

    public AstParseResultDto() {
    }

    public List<AstParseErrorDto> getParseErrors() {
        return parseErrors;
    }

    public void setParseErrors(List<AstParseErrorDto> parseErrors) {
        this.parseErrors = parseErrors;
    }

    public AstNodeDto getSyntaxTree() {
        return syntaxTree;
    }

    public void setSyntaxTree(AstNodeDto syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstParseResultDto that = (AstParseResultDto) o;

        if (parseErrors != null ? !parseErrors.equals(that.parseErrors) : that.parseErrors != null) return false;
        if (syntaxTree != null ? !syntaxTree.equals(that.syntaxTree) : that.syntaxTree != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parseErrors != null ? parseErrors.hashCode() : 0;
        result = 31 * result + (syntaxTree != null ? syntaxTree.hashCode() : 0);
        return result;
    }
}
