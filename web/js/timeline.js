function inicializeTimeline(nowDate) {
    // Nastavenia timeline
    var options = {
        width: "100%",
        height: "50%",
        layout: "dot",
        style: 'dot',
        axisOnTop: true,
        eventMargin: 2,  // minimal margin between events
        eventMarginAxis: 0, // minimal margin beteen events and the axis
        editable: false,
        showNavigation: true,
        zoomMin: 1000 * 60 * 1,             // 5 min
        zoomMax: 1000 * 60 * 60 * 24 * 7,     // tyzden
        animate: false,   // TODO: vypnut pri velkom pocte prvkov
        animateZoom: false,
        //cluster: true,
        showMajorLabels: true,
        stackEvents: true,
        //stackEvents: false,
        //cluster: true,
        snapEvents: true,
        groupMinHeight: 200
    };

    // Instantiate our timeline object.
    gTimeline = new links.Timeline(document.getElementById('mytimeline'), options);
    //google.visualization.events.addListener(gTimeline, 'rangechange', onRangeChange);
    //google.visualization.events.addListener(gTimeline, 'rangechanged', onRangeChanged);
    google.visualization.events.addListener(gTimeline, 'select', onSelect);

    // Nastav hlavny cas odkedy budeme hladat eventy
    //var start = new Date(nowDate.getTime() - 5 * 60 * 60 * 1000); // poslednych 5 min
    var start = new Date(nowDate.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni
    var end = nowDate;
    gTimeline.draw();
    gTimeline.setVisibleChartRange(start, end);

    // nacitaj uvodne data
    gToolbar.setTime(start, end);
    loadRange(start, end, gTimeline.size.contentWidth);
}

function initializeTooltip() {
    $(document).tooltip({
        items: ".timeline-event-dot-container",
        content: function () {
            var element = $(this);
            if (element.is("div")) {
                return element.children().text();
            }
        }
    });
}

function handleServiceResponse(response) {
    if (response.isError()) {
        alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
        return;
    }
    gData = response.getDataTable();
    /*var dateFormatter = new google.visualization.DateFormat(
     {timeZone: 4}
     );
     dateFormatter.format(gData, 0);
     dateFormatter.format(gData, 1); */
    //console.log(gData);
    gTimeline.draw(gData);
    gDataTable.draw(gData, {
        allowHtml: true,
        showRowNumber: true,
        page: "enable",
        pageSize: 20
    });
}


function loadRange(start, end, width) {
    var parameters = $.param({
        start: gDateFormater.format(start),
        end: gDateFormater.format(end),
        width: width
    });
    var query = new google.visualization.Query("datatable?" + parameters);
    query.send(handleServiceResponse);
}
/*
 $.ajax({
 url: 'http://example.com/',
 type: 'PUT',
 data: 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
 success: function() { alert('PUT completed'); }
 });
 */