function getTimeline() {
    // Nastavenia timeline
    var options = {
        width: "100%",
        height: "60%",
        layout: "circle",
        style: 'circle',
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
    var timeline = new links.Timeline(document.getElementById('mytimeline'), options);
    google.visualization.events.addListener(timeline, 'rangechange', onRangeChange);
    google.visualization.events.addListener(timeline, 'rangechanged', onRangeChanged);
    google.visualization.events.addListener(timeline, 'select', onSelect);
    return timeline;
};

function getSelectedRow() {
    var row = undefined;
    var sel = gGlobals.timeline.getSelection();
    if (sel.length) {
        if (sel[0].row != undefined) {
            row = sel[0].row;
        }
    }
    return row;
};

function getSelectedValue(column) {
    var row = this.getSelectedRow();
    if (row == undefined) return;
    return gGlobals.timeline.getData().getValue(row, column);
};

function registerTooltips() {
    $(".timeline-event-dot-container").qtip({
        show: 'click',
        hide: 'unfocus',
        content: {
            text: function (event, api) {
                var entityID = getSelectedValue(5);
                if (entityID == undefined) {
                    return;
                }
                $.ajax({
                    url: getEventEntityURL(entityID)
                }).then(function (content) {
                    // Set the tooltip content upon successful retrieval
                    api.set('content.text', content);
                }, function (xhr, status, error) {
                    // Upon failure... set the tooltip content to error
                    api.set('content.text', status + ': ' + error);
                });
                return 'Loading...'; // Set some initial text
            }
        }
    });
};
