function Globals() {
    this.table = new google.visualization.Table(document.getElementById('datatable'));
    this.serverDateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.charts = new links.ChartPanel();
    this.timeline = getTimeline();
    this.startInput = $('#startDate');
    this.endInput = $('#endDate');
    this.chunks = new links.ChunksLoader();
    this.dateFormat = 'd.m.Y H:i';
    this.developers = [];

    this.initialize = function (start, end) {
        this.developers = this.getDevelopers();
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

    this.checkDevelopers = function () {
        var actual = this.getDevelopers();
        var i, developer;
        var shouldRefresh = false;
        for (i = 0; i < actual.length; i++) {
            developer = actual[i];
            if (this.developers.indexOf(developer) == -1) {
                // Developera musime pridat ...
                this.timeline.getGroup(developer);
                shouldRefresh = true;
                // TODO: No ale treba donacitat este stare casti ....  lepsie je vsetko vymazat a stiahnut odznova asi

            }
        }
        for (i = 0; i < this.developers.length; i++) {
            developer = this.developers[i];
            if (actual.indexOf(developer) == -1) {
                // Developera musime zmazat ...
                this.timeline.deleteGroup(developer);
                shouldRefresh = true;
            }
        }

        if (shouldRefresh) {
            gGlobals.timeline.redraw();
            gGlobals.charts.redraw();
        }

        this.developers = actual;
    };

    this.redraw = function () {
        this.timeline.redraw();
        this.charts.redraw();
    };

    this.getServiceURL = function (start, end) {
        this.checkDevelopers();
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(start),
            end: this.serverDateFormatter.format(end),
            developers: this.developers.join()
            //width: this.timeline.size.contentWidth,
            //step: this.timeline.step.step,
            //scale: this.timeline.step.scale
        });
        return restURL + parameters;
    };

}