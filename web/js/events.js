function onLoad() {
    gGlobals = new Globals();
    var nowDate = gGlobals.serverDateFormatter.parse("2014-08-06T12:00:00.000");    // debug
    //var nowDate = new Date();
    var start = new Date(nowDate.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni

    // Inicializuj data
    gGlobals.initialize(start, nowDate);
    registerQTip();

    $('#startDate').datetimepicker({
        format:"d.m.Y H:i"
    });
    $('#endDate').datetimepicker({
        format:"d.m.Y H:i"
    });

    $('#select-links').selectize({
        plugins: ['remove_button'], // , 'drag_drop'
        delimiter: ',',
        persist: false,
        valueField: 'name',
        labelField: 'name',
        searchField: 'name',
        createOnBlur: true,
        create: true,
        options: [
            {name: 'Developer A'},
            {name: 'Developer B'},
            {name: 'Developer C'}
        ],
        onChange: function(value) {
            console.log(value);
        }
    });
}

function onRangeChange() {
    var range = gGlobals.timeline.getVisibleChartRange();
    gGlobals.setTime(range.start, range.end);
}

function onReSize() {
    if (gGlobals) {
        gGlobals.redraw();
    }
}

function onSelect() {

}

function onSetTime() {
    if (gGlobals) {
        gGlobals.timeline.setVisibleChartRange(gGlobals.getStart(), gGlobals.getEnd(), true);
        onRangeChange();
        onRangeChanged();
    }
}

function onSetCurrentTime() {
    if (gGlobals) {
        gGlobals.timeline.setVisibleChartRangeNow();
        onRangeChange();
        onRangeChanged();
    }
}

function onRangeChanged() {
    var range = gGlobals.timeline.getVisibleChartRange();
    console.log("onRangeChanged " + gGlobals.serverDateFormatter.format(range.start) + " " + gGlobals.serverDateFormatter.format(range.end));
    gGlobals.chunks.onRangeChanged(range.start, range.end);
    gGlobals.charts.redraw();
}
