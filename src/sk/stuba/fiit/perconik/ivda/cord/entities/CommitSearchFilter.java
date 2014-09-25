package sk.stuba.fiit.perconik.ivda.cord.entities;

import java.util.Date;

/**
 * Created by Seky on 5. 9. 2014.
 * Filter for commit search.
 */
public final class CommitSearchFilter extends SearchFilter {
    private static final long serialVersionUID = -4253069753905540751L;

    private String sort; // commitDate or topo
    private Boolean asc;
    private String author;
    private Date since;
    private Date until;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public Date getUntil() {
        return until;
    }

    public void setUntil(Date until) {
        this.until = until;
    }
}
