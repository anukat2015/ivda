package sk.stuba.fiit.perconik.ivda.uaca.dto.ide;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class IdeFindFileResultDto implements Serializable {
    public IdeDocumentDto file;
    private List<IdeFindResultRowDto> rows;
    
    public IdeFindFileResultDto(){
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
		if(rows == null){
			rows = new ArrayList<IdeFindResultRowDto>();
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
