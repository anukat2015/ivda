package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class IdeFindFileResultDto implements Serializable {
    private static final long serialVersionUID = -4004040620580383522L;
    public IdeDocumentDto file;
    private List<IdeFindResultRowDto> rows;

    public IdeFindFileResultDto() {
    }

    public IdeFindFileResultDto(IdeDocumentDto file, List<IdeFindResultRowDto> rows) {
        super();
        this.file = file;
        this.rows = rows;
    }

    /**
     * @return the {@link #file}
     */
    public IdeDocumentDto getFile() {
        return file;
    }

    /**
     * @param {@link #file}
     */
    public void setFile(IdeDocumentDto file) {
        this.file = file;
    }

    /**
     * @return the {@link #rows}
     */
    public List<IdeFindResultRowDto> getRows() {
        if (rows == null) {
            rows = Collections.emptyList();
        }
        return rows;
    }

    /**
     * @param {@link #rows}
     */
    public void setRows(List<IdeFindResultRowDto> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
