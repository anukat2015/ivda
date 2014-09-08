package sk.stuba.fiit.perconik.ivda.util.rest;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 5. 9. 2014.
 */
public abstract class Paged implements Serializable {
    private Integer pageIndex;
    private Integer pageSize;
    private List<Link> links;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("links", links).append("pageIndex", pageIndex).append("pageSize", pageSize).toString();
    }

    public abstract boolean isHasNextPage();

    public abstract boolean isEmpty();

    public abstract List getItems();

    public void nextPage() {
        if (isHasNextPage()) {
            pageIndex++;
        }
    }

    public boolean isHasPreviousPage() {
        return pageIndex > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            pageIndex--;
        }
    }


}

