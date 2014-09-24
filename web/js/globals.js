function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.serverDateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.charts = new ChartPanel();
    this.timeline = getTimeline();
    this.startInput = $('#startDate');
    this.endInput = $('#endDate');
    this.chunks = new ChunksLoader();
    this.dateFormat = 'd.m.Y H:i';

    this.initialize = function (start, end) {
        this.setTime(start, end);
        this.timeline.draw();
        this.timeline.setVisibleChartRange(start, end);
        this.chunks.loadRange(start, end, function () {
            console.log("finished loadRange");
            gGlobals.charts.redraw();
            gGlobals.timeline.render({
                animate: false
            });
        });
    };

    this.setTime = function (start, end) {
        this.startInput.val(start.dateFormat(this.dateFormat));
        this.endInput.val(end.dateFormat(this.dateFormat));
    };

    this.getStart = function () {
        return Date.parseDate(this.startInput.val(), this.dateFormat);
    };

    this.getEnd = function () {
        return Date.parseDate(this.startInput.val(), this.dateFormat);
    };

    this.getAjaxURL = function (parameters) {
        return "faces/ajax.xhtml?" + $.param(parameters);
    };

    this.getDevelopers = function () {
        return $('#select-links').val().split(',');
    };

    this.redraw = function () {
        this.timeline.redraw();
        this.charts.redraw();
    };

    this.getServiceURL = function (start, end) {
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(start),
            end: this.serverDateFormatter.format(end)
        });
        return restURL + parameters;
    };

}