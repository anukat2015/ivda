package sk.stuba.fiit.perconik.ivda.activity.client;

import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.util.rest.Paged;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 19. 7. 2014.
 * <p>
 * Vseobecna datova entita, ktora reprezentuje odpoved z UACA sluzby.
 * Odpoved ma v JSONe tvar:
 * <p>
 * {
 * "resultSet":[],
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
public class PagedResponse<T extends Serializable> extends Paged {
    private static final long serialVersionUID = 3892748839161178943L;
    private Integer pageCnt;
    private List<T> resultSet;

    public Integer getPageCnt() {
        return pageCnt;
    }

    public void setPageCnt(Integer pageCnt) {
        this.pageCnt = pageCnt;
    }

    public List<T> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<T> resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resultSet", resultSet).toString();
    }

    public boolean isHasNextPage() {
        return (getPageIndex() + 1) < pageCnt;
    }

    @Override
    public boolean isEmpty() {
        return getResultSet().isEmpty();
    }

    @Override
    public List getItems() {
        return resultSet;
    }
}
