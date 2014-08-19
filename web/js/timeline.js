function inicializeTimeline(nowDate) {
    // Nastavenia timeline
    var options = {
        width: "100%",
        height: "60%",
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
        groupMinHeight: 70
    };

    // Instantiate our timeline object.
    gTimeline = new links.Timeline(document.getElementById('mytimeline'), options);

    // Nastav hlavny cas odkedy budeme hladat eventy
    var start = new Date(nowDate.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni
    var end = nowDate;
    gTimeline.draw();
    gTimeline.setVisibleChartRange(start, end);

    // nacitaj uvodne data
    gToolbar.setTime(start, end);
    loadRange(start, end, gTimeline.size.contentWidth);
}

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
function getSelectedValue(column) {
    var row = getSelectedRow();
    if (row == undefined) return;
    return gData.getValue(row, column);
}
