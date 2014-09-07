function onLoad() {
    gGlobals = new Globals();
    var nowDate = gGlobals.dateFormatter.parse("2014-08-06T12:00:00.000");    // debug
    //var nowDate = new Date();
    var start = new Date(nowDate.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni

    // Inicializuj data
    gGlobals.setTime(start, nowDate);
    gGlobals.timeline.draw();
    gGlobals.timeline.setVisibleChartRange(start, nowDate);
    gGlobals.chunks.loadRange(start, nowDate, function () {
        console.log("finished loadRange");
        gGlobals.charts.redraw();
        gGlobals.timeline.render({
            animate: false
        });
    });
    registerQTip();
    $('#startDate').datetimepicker({
        format:"yyyy-MM-dd'T'HH:mm:ss.SSS"
    });
    $('#endDate').datetimepicker({
        format:"yyyy-MM-dd'T'HH:mm:ss.SSS"
    });

    $('#startDate').qtip({content: {
        text: 'SSSSSSSSSSSSSSS'
    }});
    $('#endDate').qtip({content: {
        text: 'SSSSSSSSSSSSSSS'
    }});

    $('#select-links').selectize({
        plugins: ['remove_button', 'drag_drop'],
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
            console.log( JSON.stringify($('#select-links').val()) );
        }
    });
    $('#select-links').qtip({content: {
        text: 'SSSSSSSSSSSSSSS'
    }});
    /*gGlobals.table.draw(data, {
     allowHtml: true,
     showRowNumber: true,
     page: "enable",
     pageSize: 20
     });*/
}

function onRangeChange() {
    var range = gGlobals.timeline.getVisibleChartRange();
    gGlobals.setTime(range.start, range.end);
}

function onReSize() {
    gGlobals.timeline.redraw();
}

function registerQTip() {
    $(document).on("click", '.timeline-event-circle', function ($e) {
        $(this).qtip({
            overwrite: false,
            hide: 'unfocus',
            show: 'click',
            /*show: {
             ready: true // Needed to make it show on first mouseover event
             }, */
            content: {
                text: function (event, api) {
                    var item = gGlobals.timeline.getSelected();
                    if (item === undefined || item.metadata === undefined) {
                        api.set('content.text', "Not selected entity.");
                    } else {
                        if (item.metadata.ajax != undefined) {
                            console.log(item.metadata.ajax);
                        } else {
                            $.ajax({
                                url: gGlobals.getAjaxURL(item.metadata)
                            }).then(function (content) {
                                // Set the tooltip content upon successful retrieval
                                api.set('content.text', content);
                            }, function (xhr, status, error) {
                                // Upon failure... set the tooltip content to error
                                api.set('content.text', status + ': ' + error);
                            });
                        }
                    }
                    return 'Loading...'; // Set some initial text
                }
            }
        });
    });
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
    console.log("onRangeChanged " + gGlobals.dateFormatter.format(range.start) + " " + gGlobals.dateFormatter.format(range.end));
    gGlobals.chunks.onRangeChanged(range.start, range.end);
    gGlobals.charts.redraw();
}
