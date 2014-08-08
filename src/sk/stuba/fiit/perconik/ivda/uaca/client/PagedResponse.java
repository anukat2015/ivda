package sk.stuba.fiit.perconik.ivda.uaca.client;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 19. 7. 2014.
 * <p/>
 * Vseobecna datova entita, ktora reprezentuje odpoved z UACA sluzby.
 * Odpoved ma v JSONe tva:
 * <p/>
 * {
 * "ResultSet":[],
 * "pageIndex":1,
 * "pageCnt":21109,
 * "pageSize":10,
 * "links":[
 * {
 * "Rel":"previous",
 * "Href":"http://perconik.fiit.stuba.sk/UserActivity/api/useractivity?timefrom=2014-04-16T12:00Z&page=0&pagesize=10"
 * },
 * {
 * "Rel":"next",
 * "Href":"http://perconik.fiit.stuba.sk/UserActivity/api/useractivity?timefrom=2014-04-16T12:00Z&page=2&pagesize=10"
 * }
 * ]
 * }
 */
public class PagedResponse<T extends Serializable> implements Serializable {
    private Integer pageIndex;
    private Integer pageSize;
    private Integer pageCnt;
    private List<Link> links;
    // Custom data
    private List<T> ResultSet;

    public PagedResponse() {
    }

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

    public Integer getPageCnt() {
        return pageCnt;
    }

    public void setPageCnt(Integer pageCnt) {
        this.pageCnt = pageCnt;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<T> getResultSet() {
        return ResultSet;
    }

    public void setResultSet(List<T> resultSet) {
        this.ResultSet = resultSet;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("links", links).append("pageCnt", pageCnt).append("pageIndex", pageIndex).append("pageSize", pageSize).append("ResultSet", ResultSet).toString();
    }

    public boolean isHasNextPage() {
        return (pageIndex + 1) < pageCnt;
    }

    public void nextPage() {
        if (isHasNextPage()) {
            pageIndex++;
        }
    }

    public boolean isHasPreviousPage() {
        return getPageIndex() > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            pageIndex--;
        }
    }
}
