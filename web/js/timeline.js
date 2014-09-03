function getTimeline() {
    // Nastavenia timeline
    var windowHeight = $(window).height();
    var options = {
        width: "100%",
        height: "90%",
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
        groupMinHeight: windowHeight * 0.1
    };

    // Instantiate our timeline object.
    var timeline = new links.Timeline(document.getElementById('mytimeline'), options);
    google.visualization.events.addListener(timeline, 'rangechange', onRangeChange);
    google.visualization.events.addListener(timeline, 'rangechanged', onRangeChanged);
    google.visualization.events.addListener(timeline, 'select', onSelect);
    //timeline.setCurrentTime( new Date(new Date().getTime() - 2 * 60 * 60 * 1000));
    return timeline;
}
