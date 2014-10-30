function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.serverDateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.charts = new ChartPanel();
    this.timeline = getTimeline();
    this.graph = new GraphPanel();
    this.startInput = $('#startDate');
    this.endInput = $('#endDate');
    this.loader = new ChunksLoader();
    this.dateFormat = 'd.m.Y H:i';
    this.preloader = new Preloader();
    this.timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000 * 2;  // day light saving
    this.showError = true;

    this.initialize = function (start, end) {
        this.setTime(start, end);
        this.timeline.draw();
        this.timeline.setVisibleChartRange(start, end);
        /*this.loader.loadRange(start, end, function () {
            console.log("finished loadRange");
            gGlobals.charts.redraw();
            gGlobals.graph.redraw();
            gGlobals.timeline.render({
                animate: false
            });
        });
        this.preloader.start(); */
    };

    this.setTime = function (start, end) {
        this.startInput.val(start.dateFormat(this.dateFormat));
        this.endInput.val(end.dateFormat(this.dateFormat));
    };

    this.getStart = function () {
        return Date.parseDate(this.startInput.val(), this.dateFormat);
    };

    this.getEnd = function () {
        return Date.parseDate(this.endInput.val(), this.dateFormat);
    };

    this.getAjaxURL = function (parameters) {
        return "faces/ajax.xhtml?" + $.param(parameters);
    };

    this.getDeveloper = function () {
        return $('#select-links').val();
    };

    this.redraw = function () {
        this.timeline.redraw();
        this.charts.redraw();
        this.graph.redraw();
    };

    this.converDate = function (date) {
        return new Date(date.getTime() + this.timezoneOffset);  // local to utc time
    };

    this.getTimelineServiceURL = function (start, end) {
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(this.converDate(start)),
            end: this.serverDateFormatter.format(this.converDate(end)),
            developer: this.getDeveloper()
        });
        return restURL + parameters;
    };

    this.getProcessesServiceURL = function (start, end) {
        var restURL = "processes?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(this.converDate(start)),
            end: this.serverDateFormatter.format(this.converDate(end)),
            developer: this.getDeveloper()
        });
        return restURL + parameters;
    };

    this.getDevelopersServiceURL = function() {
        return "rest/getDevelopers";
    };


    this.toggleMetric = function () {
        var prototyp = links.Timeline.ItemCircle.prototype;
        if (prototyp.computeSize == prototyp.computeSizeByChangedLines) {
            prototyp.computeSize = prototyp.computeSizeByChangesInFuture;
        } else {
            prototyp.computeSize = prototyp.computeSizeByChangedLines;
        }
        this.timeline.redraw();
    };

    this.toggleProcesses = function () {
        this.graph.showProcesses = !this.graph.showProcesses;
        this.graph.redraw();
    };


    /**
     * Data sa nepodarilo stiahnut
     * @param error
     */
    this.alertError = function (msg) {
        console.log(msg);
        if (this.showError) {
            alert(msg);
            this.showError = false;
        }
    };
}
