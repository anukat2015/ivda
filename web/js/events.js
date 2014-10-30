function onLoad() {
    gGlobals = new Globals();
    var nowDate = gGlobals.serverDateFormatter.parse("2014-08-06T12:00:00.000");    // debug
    //var nowDate = new Date();
    var start = new Date(nowDate.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni

    // Inicializuj data
    $('#startDate').datetimepicker({
        format: "d.m.Y H:i"
    });
    $('#endDate').datetimepicker({
        format: "d.m.Y H:i"
    });

    $.ajax({
        dataType: "json",
        url: gGlobals.getDevelopersServiceURL(),
        cache: false,
        error: function (jqXHR, textStatus, errorThrown) {
            gGlobals.alertError("Server response status:" + textStatus);
        },
        success: function (developers, textStatus, jqXHR) {
            var items = developers.map(function (x) {
                return { name: x };
            });
            $('#select-links').selectize({
                plugins: ['remove_button'], // , 'drag_drop'
                maxItems: 1,
                delimiter: ',',
                persist: false,
                valueField: 'name',
                labelField: 'name',
                searchField: 'name',
                createOnBlur: true,
                create: true,
                options: items,
                onChange: function (value) {
                    gGlobals.loader.checkDevelopers();
                }
            });

            gGlobals.initialize(start, nowDate);
        }
    });
    registerQTip();
}
/*
 timeline.on('rangechange', function (properties) {
 properties.start
 properties.end
 });

 timeline.on('rangechanged', function (properties) {
 properties.start
 properties.end
 });

 timeline.on('rangechanged', function (properties) {
 properties.start
 properties.end
 });
 */
function onRangeChange() {
    /*
     var range = this.body.range.getRange();
     var left  = this.body.util.toScreen(range.start);
     var right = this.body.util.toScreen(range.end);
     */
    var range = gGlobals.timeline.getVisibleChartRange();
    gGlobals.setTime(range.start, range.end);
}

function onReSize() {
    if (gGlobals) {
        gGlobals.redraw();
    }
}

function onSetTime() {
    if (gGlobals) {
        // moveTo(time [, options])
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
    gGlobals.loader.onRangeChanged(range.start, range.end);
    gGlobals.preloader.start();
    gGlobals.charts.redraw();
    gGlobals.graph.redraw();
}
