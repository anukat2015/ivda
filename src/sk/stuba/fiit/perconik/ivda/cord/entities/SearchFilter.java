package sk.stuba.fiit.perconik.ivda.cord.entities;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 * General SearchFilter for cord requests.
 */
public class SearchFilter implements Serializable {
    private static final long serialVersionUID = 7613591918186590775L;

    private Integer pageIndex;
    private Integer pageSize;

    public SearchFilter() {
        pageIndex = 0;
        pageSize = 100;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void nextPage() {
        pageIndex++;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
