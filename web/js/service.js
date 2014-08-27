
function getDatatableURL(start, end, width) {
    var restURL = "datatable?";
    var parameters = $.param({
        start: gDateFormater.format(start),
        end: gDateFormater.format(end),
        width: width
    });
    return restURL + parameters;
}

function getEventEntityURL(eventID) {
    var restURL = "faces/event.xhtml?";
    var parameters = $.param({
        id: eventID
    });
    return restURL + parameters;
}

function getDiffURL() {
    var restURL = "rest/timeline/filediff?";
    var parameters = $.param({
        version: 31517,
        old: 33408,
        path: "sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java"
    });
    return restURL + parameters;
}

function handleServiceResponse(response) {
    if (response.isError()) {
        alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
        return;
    }
    var data = response.getDataTable();
    drawChart1(data);
    gTimeline.draw(data);
    gDataTable.draw(data, {
        allowHtml: true,
        showRowNumber: true,
        page: "enable",
        pageSize: 20
    });
    initializeTooltip();
}


function loadRange(start, end, width) {
    var query = new google.visualization.Query( getDatatableURL(start, end, width) );
    query.send(handleServiceResponse);
}
