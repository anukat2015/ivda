function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.dateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.charts = new links.ChartPanel();
    this.timeline = getTimeline();
    this.startInput = document.getElementById('startDate');
    this.endInput = document.getElementById('endDate');
    this.chunks = new Chunks();

    this.setTime = function (start, end) {
        this.startInput.value = this.dateFormatter.format(start);
        this.endInput.value = this.dateFormatter.format(end);
    };

    this.getStart = function () {
        return this.dateFormatter.parse(this.startInput.value);

    };

    this.getEnd = function () {
        return this.dateFormatter.parse(this.endInput.value);
    };

    this.getAjaxURL = function (parameters) {
        return "faces/ajax.xhtml?" + $.param(parameters);
    };

    this.getServiceURL = function (start, end) {
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.dateFormatter.format(start),
            end: this.dateFormatter.format(end),
            width: this.timeline.size.contentWidth,
            step: this.timeline.step.step,
            scale: this.timeline.step.scale
        });
        return restURL + parameters;
    };

}