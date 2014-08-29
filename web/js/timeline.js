function getTimeline() {
    // Nastavenia timeline
    var windowHeight = $(window).height();
    var options = {
        width: "100%",
        height: "90%",
        //minHeight: windowHeight * 0.8,
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
        groupMinHeight: windowHeight * 0.1
    };

    // Instantiate our timeline object.
    var timeline = new links.Timeline(document.getElementById('mytimeline'), options);
    google.visualization.events.addListener(timeline, 'rangechange', onRangeChange);
    google.visualization.events.addListener(timeline, 'rangechanged', onRangeChanged);
    google.visualization.events.addListener(timeline, 'select', onSelect);
    return timeline;
};

function registerTooltips() {
    $(".timeline-event-dot-container").qtip({
        show: 'click',
        hide: 'unfocus',
        content: {
            text: function (event, api) {
                var entityID = gGlobals.timeline.getSelectedValue(5);
                if (entityID == undefined) {
                    return;
                }
                $.ajax({
                    url: gGlobals.getEventEntityURL(entityID)
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
