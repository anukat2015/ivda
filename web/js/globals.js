function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.dateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.charts = new ChartPanel();
    this.timeline = getTimeline();
    this.startInput = document.getElementById('startDate');
    this.endInput = document.getElementById('endDate');

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

    this.getEventEntityURL = function (eventID) {
        var restURL = "faces/event.xhtml?";
        var parameters = $.param({
            id: eventID
        });
        return restURL + parameters;
    }

    this.getDiffURL = function () {
        var restURL = "rest/timeline/filediff?";
        var parameters = $.param({
            version: 31517,
            old: 33408,
            path: "sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java"
        });
        return restURL + parameters;
    }

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
    }

    this.loadRange = function (start, end) {
        var query = new google.visualization.Query(this.getServiceURL(start, end));
        query.send(handleServiceResponse);
    }
}
