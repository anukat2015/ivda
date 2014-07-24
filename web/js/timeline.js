// Called when the Visualization API is loaded.

function drawChart() {
    var data = google.visualization.arrayToDataTable([
        ['Task', 'Hours per Day'],
        ['Work', 11],
        ['Eat', 2],
        ['Commute', 2],
        ['Watch TV', 2],
        ['Sleep', 7]
    ]);

    var options = {
        title: 'My Daily Activities'
    };

    var gChart = new google.visualization.PieChart(document.getElementById('piechart'));
    gChart.draw(data, options);
}

function convertDateToUTC(date) {
    return new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
}

function createDateAsUTC(date) {
    return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()));
}

function inicializeTimeline(nowDate) {
    // Nastavenia timeline
    var options = {
        width: "100%",
        height: "50%",
        layout: "box",
        axisOnTop: true,
        eventMargin: 1,  // minimal margin between events
        eventMarginAxis: 0, // minimal margin beteen events and the axis
        editable: false,
        showNavigation: true,
        zoomMin: 1000 * 60 * 1,             // 5sec
        zoomMax: 1000 * 60 * 60 * 24 * 7,     // tyzden
        animate: true,
        animateZoom: true,
        //cluster: true,
        groupMinHeight: 100
    };

    // Instantiate our timeline object.
    gTimeline = new links.Timeline(document.getElementById('mytimeline'), options);
    google.visualization.events.addListener(gTimeline, 'rangechange', onRangeChange);
    google.visualization.events.addListener(gTimeline, 'rangechanged', onRangeChanged);
    google.visualization.events.addListener(gTimeline, 'select', onSelect);

    // Nastav hlavny cas odkedy budeme hladat eventy
    var start = new Date(nowDate.getTime() - 5 * 60 * 60 * 1000); // poslednych 5 min
    var end = nowDate;
    gTimeline.draw();
    gTimeline.setVisibleChartRange(start, end);
    onRangeChange();
    onRangeChanged();
}

function drawVisualization() {
    // Inicializuj data
    gDateFormater = new google.visualization.DateFormat({formatType: 'medium'});
    gDataTable = new google.visualization.Table(document.getElementById('datatable'));
    gData = new google.visualization.DataTable();

    var start = new Date("2014-07-23T14:47:12.000");    // debug
    //var start = new Date();
    inicializeTimeline(start);
    drawChart();
}

function handleServiceResponse(response) {
    if (response.isError()) {
        alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
        return;
    }
    gData = response.getDataTable();
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
        start: createDateAsUTC(start).toISOString(),
        end: createDateAsUTC(end).toISOString(),
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