package sk.stuba.fiit.perconik.ivda.Client;

/**
 * Created by Seky on 22. 7. 2014.
 *
 */

/**
 * Used as request in search for events.
 * Defined at: http://perconik.fiit.stuba.sk/UserActivity/Default/ReferenceManual
 */
public interface IEventFilter {
    // Page[int]
    // Zero based index of the results page
    public int getPage();

    public void setPage(int page);

    // Demanded size of the results page.Default page size is 20items. Limited by server configuration to 100items
    public int getPageSize();

    public void setPageSize(int size);

    // atd
}

 /*
Attributes

Page [int]
Zero based index of the results page
PageSize [int]
Demanded size of the results page. Default page size is 20 items. Limited by server configuration to 100 items
TimeFrom [nullable DateTime]
Search for events with Timestamp later than TimeFrom
TimeTo [nullable DateTime]
Search for events with Timestamp earlier than TimeTo
EventTypeUri [string]
Search for events of types defined by URIs, which start with EventTypeUri - allows hierarchical type filtering
ExactType [bool]
Search for events of type defined by exact URI. Default is false
User [string]
Search for events from users with names starting with given string
Workstation [string]
Search for events from workstations with names starting with given string
Ascending [bool]
Defines results order. Results are ordered by Event Timestamp.
Default is false, which means results are ordered descending or from the newest to the oldest events
If true, results are ordered ascending or from the oldest to the newest events
*/