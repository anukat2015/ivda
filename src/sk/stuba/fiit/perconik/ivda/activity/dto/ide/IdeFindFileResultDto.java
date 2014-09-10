package sk.stuba.fiit.perconik.ivda.activity.dto.ide;

import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.*;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeFindResultRowDto;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class IdeFindFileResultDto implements Serializable {
    public sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto file;
    private List<sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeFindResultRowDto> rows;
    
    public IdeFindFileResultDto(){
    }
    
	public IdeFindFileResultDto(sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto file, List<sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeFindResultRowDto> rows) {
		super();
		this.file = file;
		this.rows = rows;
	}
	
	/**
	 * @return the {@link #file}
	 */
	public sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto getFile() {
		return file;
	}
	/**
	 * @param {@link #file}
	 */
	public void setFile(sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto file) {
		this.file = file;
	}
	/**
	 * @return the {@link #rows}
	 */
	public List<sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeFindResultRowDto> getRows() {
		if(rows == null){
			rows = new ArrayList<sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeFindResultRowDto>();
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
