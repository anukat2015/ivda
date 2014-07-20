package sk.stuba.fiit.perconik.ivda.dto;

import javax.ws.rs.core.Link;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 19. 7. 2014.
 */
public class PagedResponse<T> implements Serializable{
    private Integer pageIndex;
    private Integer pageSize;
    private Integer pageCnt;
    private List<Link> links;
    // Custom data
    private List<T> resultSet;

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
        return resultSet;
    }

    public void setResultSet(List<T> resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public String toString() {
        return "PagedResponse{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageCnt=" + pageCnt +
                ", links=" + links +
                ", resultSet=" + resultSet +
                '}';
    }

    public int getPageFirstItem()
    {
        return pageIndex * pageSize;
    }

    public int getPageLastItem()
    {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getPageCnt() - 1;
        if (i > count)
        {
            i = count;
        }
        if (i < 0)
        {
            i = 0;
        }
        return i;
    }

    public boolean isHasNextPage()
    {
        return (pageIndex + 1) * pageSize + 1 <= getPageCnt();
    }

    public void nextPage()
    {
        if (isHasNextPage())
        {
            pageIndex++;
        }
    }

    public boolean isHasPreviousPage()
    {
        return getPageIndex() > 0;
    }

    public void previousPage()
    {
        if (isHasPreviousPage())
        {
            pageIndex--;
        }
    }
}

/*
{
   "resultSet":[
      {
         "EventId":"cf915acd-3bfe-4a9a-a3f9-5c217e6c7d19",
         "EventTypeUri":"http://perconik.gratex.com/useractivity/event/web/tab/close",
         "Timestamp":"2014-07-18T12:00:51.596Z",
         "User":"steltecia\\krastocny",
         "Workstation":"karras-pc",
         "WasCommitForcedByUser":false,
         "SessionId":"15",
         "AppName":"Chrome",
         "AppVersion":"35.0.1916.153",
         "Url":"https://onedrive.live.com/options?ru=https%3a%2f%2fonedrive.live.com%2f",
         "TabId":"297"
      }
      }
   ],
   "pageIndex":1,
   "pageCnt":21109,
   "pageSize":10,
   "links":[
      {
         "Rel":"previous",
         "Href":"http://perconik.fiit.stuba.sk/UserActivity/api/useractivity?timefrom=2014-04-16T12:00Z&page=0&pagesize=10"
      },
      {
         "Rel":"next",
         "Href":"http://perconik.fiit.stuba.sk/UserActivity/api/useractivity?timefrom=2014-04-16T12:00Z&page=2&pagesize=10"
      }
   ]
}
 */
