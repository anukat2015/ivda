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
}
