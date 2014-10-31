function Timeline() {
    this.panel = undefined;

    this.init = function () {
        this.build();
        this.registerQTip();
    };

    this.registerQTip = function () {
        var instance = this;
        $(document).on("mouseenter", '.timeline-event-circle', function ($e) {
            $(this).qtip({
                overwrite: false,
                hide: 'unfocus',
                show: 'click',
                content: {
                    text: function (event, api) {
                        var item = instance.panel.getSelected();
                        if (item === undefined) {
                            return 'Not selected entity.';
                        } else if (item.isCluster) {
                            return "Toto je skupina udalosti.";
                        } else if (item.metadata == undefined) {
                            return 'Ziadne data.';
                        }
                        return instance.onItemMouseClick(api, item);
                    }
                }
            });
        });
    };

    this.onRangeChange = function () {
        /*
         var range = this.body.range.getRange();
         var left  = this.body.util.toScreen(range.start);
         var right = this.body.util.toScreen(range.end);
         */
        var instance = gGlobals.timeline;
        var range = instance.panel.getVisibleChartRange();
        gGlobals.toolbar.setTime(range.start, range.end);
    };

    this.onRangeChanged = function () {
        var instance = gGlobals.timeline;
        var range = instance.panel.getVisibleChartRange();
        gGlobals.loader.onRangeChanged(range.start, range.end);
        gGlobals.charts.redraw();
        gGlobals.graph.redraw();
    };

    this.build = function () {
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
            stackEvents: true,
            //cluster: true,
            groupMinHeight: windowHeight * 0.1,
            groupWidth: '20px'
        };

        // Instantiate our timeline object.
        this.panel = new links.Timeline(document.getElementById('mytimeline'), options);
        google.visualization.events.addListener(this.panel, 'rangechange', this.onRangeChange);
        google.visualization.events.addListener(this.panel, 'rangechanged', this.onRangeChanged);
    };

    this.onItemMouseClick = function (api, item) {
        var html = 'Loading...';
        if (item.metadata.link != undefined) {   // web
            html = '<a href="' + item.metadata.link + '">' + item.metadata.link + '</a>';
        } else if (item.metadata.changedLines != undefined) {  // ide
            html = 'Changed lines: ' + item.metadata.changedLines + '</br>'
                + 'Changed in future: ' + item.metadata.changedInFuture + '</br>'
                + 'Path: ' + item.metadata.path + '</br>'
                + 'Text: </br><pre>' + item.metadata.text + '</pre></br>';
        }
        return html;
    };
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
