function getSelectedRow() {
    var row = undefined;
    var sel = gTimeline.getSelection();
    if (sel.length) {
        if (sel[0].row != undefined) {
            row = sel[0].row;
        }
    }
    return row;
}

function onSelect() {
    var row = getSelectedRow();
    if (row == undefined) return;
    var item = gData.getValue(row, 2);
    console.log("item " + item + " selected");
}

// Format given date as "yyyy-mm-dd hh:ii:ss"
// @param datetime   A Date object.
function dateFormat(date) {
    var datetime = date.getFullYear() + "-" +
        ((date.getMonth() < 9) ? "0" : "") + (date.getMonth() + 1) + "-" +
        ((date.getDate() < 10) ? "0" : "") + date.getDate() + " " +
        ((date.getHours() < 10) ? "0" : "") + date.getHours() + ":" +
        ((date.getMinutes() < 10) ? "0" : "") + date.getMinutes() + ":" +
        ((date.getSeconds() < 10) ? "0" : "") + date.getSeconds();
    return datetime;
}

function onRangeChange() {
    var range = gTimeline.getVisibleChartRange();
    document.getElementById('startDate').value = dateFormat(range.start);
    document.getElementById('endDate').value = dateFormat(range.end);
}
function onRangeChanged() {
    var range = gTimeline.getVisibleChartRange();
    console.log("onRangeChanged     LOCAL FORMAT " + gDateFormater.format(range.start) + " " + gDateFormater.format(range.end));
    console.log("                   ISO FORMAT " + range.start.toISOString() + " " + range.end.toISOString());
    loadRange(range.start, range.end, gTimeline.size.contentWidth);
}

function setTime() {
    if (!gTimeline) return;
    var newStartDate = new Date(document.getElementById('startDate').value);
    var newEndDate = new Date(document.getElementById('endDate').value);
    gTimeline.setVisibleChartRange(newStartDate, newEndDate);
    onRangeChange();
    onRangeChanged();
}

function setCurrentTime() {
    if (!gTimeline) return;
    gTimeline.setVisibleChartRangeNow();
    onRangeChange();
    onRangeChanged();
}

function onReSize() {
    gTimeline.redraw();
}

function onLoad() {
    // Inicializuj data
    gDataTable = new google.visualization.Table(document.getElementById('datatable'));
    gDataTable.
    gData = new google.visualization.DataTable();

    var start = new Date("2014-08-06T12:00:00.000Z");    // debug
    //var start = new Date();
    initializeTooltip();
    inicializeTimeline(start);
    drawChart();
}