function onLoad() {
    gGlobals = new Globals();
    var nowDate = gGlobals.dateFormatter.parse("2014-08-06T12:00:00.000");    // debug
    //var nowDate = new Date();
    var start = new Date(nowDate.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni

    // Inicializuj data
    gGlobals.setTime(start, nowDate);
    gGlobals.timeline.draw();
    gGlobals.timeline.setVisibleChartRange(start, nowDate);
    gGlobals.loadRange(start, nowDate);
}

function onRangeChange() {
    var range = gGlobals.timeline.getVisibleChartRange();
    gGlobals.setTime(range.start, range.end);
}
function onRangeChanged() {
    var range = gGlobals.timeline.getVisibleChartRange();
    // console.log("onRangeChanged " + gGlobals.dateFormatter.format(range.start) + " " + gGlobals.dateFormatter.format(range.end));
    //loadRange(range.start, range.end, gGlobals.timeline.size.contentWidth);
    gGlobals.charts.redrawVisibleChart();
}

function onReSize() {
    gGlobals.timeline.redraw();
}

function onSelect() {

}

function onSetTime() {
    if (!gGlobals) return;
    gGlobals.timeline.setVisibleChartRange(gGlobals.getStart(), gGlobals.getEnd());
    onRangeChange();
    onRangeChanged();
}

function onSetCurrentTime() {
    if (!gGlobals) return;
    gGlobals.timeline.setVisibleChartRangeNow();
    onRangeChange();
    onRangeChanged();
}

function handleServiceResponse(response) {
    if (response.isError()) {
        alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
        return;
    }
    var data = response.getDataTable();
    /*gGlobals.table.draw(data, {
     allowHtml: true,
     showRowNumber: true,
     page: "enable",
     pageSize: 20
     });*/
    gGlobals.timeline.draw(data);
    setTimeout(function () {
        gGlobals.charts.drawActivityChart(data);
    }, 0);
    registerTooltips();
}