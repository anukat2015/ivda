function getTimeline() {
    // Nastavenia timeline
    var windowHeight = $(window).height();
    var options = {
        width: "100%",
        height: "94%",
        layout: "circle",
        style: 'circle',
        axisOnTop: true,
        editable: false,
        showNavigation: true,
        zoomMin: 1000 * 60 * 1,             // 5 min
        zoomMax: 1000 * 60 * 60 * 24 * 7,     // tyzden
        showMajorLabels: true,
        //stackEvents: true,
        cluster: true,
        groupMinHeight: windowHeight * 0.1,
        groupWidth: '20px'
    };

    // Instantiate our timeline object.
    var timeline = new links.Timeline(document.getElementById('mytimeline'), options);
    google.visualization.events.addListener(timeline, 'rangechange', onRangeChange);
    google.visualization.events.addListener(timeline, 'rangechanged', onRangeChanged);
    google.visualization.events.addListener(timeline, 'select', onSelect);
    //timeline.setCurrentTime( new Date(new Date().getTime() - 2 * 60 * 60 * 1000));
    return timeline;
}

function registerQTip() {
    $(document).on("mouseenter", '.timeline-event-circle', function ($e) {
        $(this).qtip({
            overwrite: false,
            hide: 'unfocus',
            show: 'click',
            content: {
                text: function (event, api) {
                    var item = gGlobals.timeline.getSelected();
                    if (item === undefined || item.metadata === undefined) {
                        api.set('content.text', "Not selected entity.");
                    } else {
                        if (item.metadata.ajax != undefined) {
                            api.set('content.text', JSON.stringify(item.metadata.ajax) );
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
