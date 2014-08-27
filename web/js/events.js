function onRangeChange() {
    var range = gTimeline.getVisibleChartRange();
    gToolbar.setTime(range.start, range.end);
}
function onRangeChanged() {
    var range = gTimeline.getVisibleChartRange();
    console.log("onRangeChanged " + gDateFormater.format(range.start) + " " + gDateFormater.format(range.end));
    loadRange(range.start, range.end, gTimeline.size.contentWidth);
}

function onReSize() {
    gTimeline.redraw();
}

function onLoad() {
    // Inicializuj data
    gToolbar = new Toolbar();
    gDataTable = new google.visualization.Table(document.getElementById('datatable'));

    var start = gDateFormater.parse("2014-08-06T12:00:00.000");    // debug
    //var start = new Date();
    initializeTooltip();
    inicializeTimeline(start);
}