package sk.stuba.fiit.perconik.ivda.dto;

import javax.ws.rs.core.Link;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 19. 7. 2014.
 */
public class PagedResponse<T> implements Serializable{
    private Integer PageIndex;
    private Integer PageSize;
    private Integer PageCnt;
    private List<Link> Links;
    // Custom data
    private List<T> ResultSet;

    public Integer getPageSize() {
        return PageSize;
    }

    public void setPageSize(Integer pageSize) {
        PageSize = pageSize;
    }

    public Integer getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        PageIndex = pageIndex;
    }

    public Integer getPageCnt() {
        return PageCnt;
    }

    public void setPageCnt(Integer pageCnt) {
        PageCnt = pageCnt;
    }

    public List<Link> getLinks() {
        return Links;
    }

    public void setLinks(List<Link> links) {
        Links = links;
    }

    public List<T> getResultSet() {
        return ResultSet;
    }

    public void setResultSet(List<T> resultSet) {
        ResultSet = resultSet;
    }

    @Override
    public String toString() {
        return "PagedResponse{" +
                "PageIndex=" + PageIndex +
                ", PageSize=" + PageSize +
                ", PageCnt=" + PageCnt +
                ", Links=" + Links +
                ", ResultSet=" + ResultSet +
                '}';
    }

    public int getPageFirstItem()
    {
        return PageIndex * PageSize;
    }

    public int getPageLastItem()
    {
        int i = getPageFirstItem() + PageSize - 1;
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
        return (PageIndex + 1) * PageSize + 1 <= getPageCnt();
    }

    public void nextPage()
    {
        if (isHasNextPage())
        {
            PageIndex++;
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
            PageIndex--;
        }
    }
}

/*
{
   "ResultSet":[
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
   "PageIndex":1,
   "PageCnt":21109,
   "PageSize":10,
   "Links":[
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
