function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.serverDateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.charts = new links.ChartPanel();
    this.timeline = getTimeline();
    this.startInput = $('#startDate');
    this.endInput = $('#endDate');
    this.chunks = new Chunks();
    this.dateFormat = 'd.m.Y H:i';

    this.setTime = function (start, end) {
        this.startInput.val(start.dateFormat(this.dateFormat));
        this.endInput.val( end.dateFormat(this.dateFormat));
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

    this.getServiceURL = function (start, end) {
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(start),
            end: this.serverDateFormatter.format(end),
            developers: $('#select-links').val()
            //width: this.timeline.size.contentWidth,
            //step: this.timeline.step.step,
            //scale: this.timeline.step.scale
        });
        return restURL + parameters;
    };

}