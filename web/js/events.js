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
    gData = new google.visualization.DataTable();

    var start = gDateFormater.parse("2014-08-06T12:00:00.000");    // debug
    //var start = new Date();
    initializeTooltip();
    inicializeTimeline(start);
}