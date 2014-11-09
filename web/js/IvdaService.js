/**
 * Created by Seky on 31. 10. 2014.
 */
function IvdaService() {
    this.serverDateFormatter = new JsSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    this.timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000 * 2;  // day light saving

    this.getTimelineURL = function (start, end, developer) {
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(this.convertDate(start.getTime())),
            end: this.serverDateFormatter.format(this.convertDate(end.getTime())),
            developer: developer
        });
        return restURL + parameters;
    };

    this.getProcessesURL = function (start, end, developer) {
        var restURL = "processes?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(this.convertDate(start.getTime())),
            end: this.serverDateFormatter.format(this.convertDate(end.getTime())),
            developer: developer
        });
        return restURL + parameters;
    };

    this.getStatsURL = function (start, end, developer) {
        var restURL = "stats?";
        var parameters = $.param({
            start: this.serverDateFormatter.format(this.convertDate(start.getTime())),
            end: this.serverDateFormatter.format(this.convertDate(end.getTime())),
            developer: developer,
            attribute: "count",
            granularity: "DAY"
        });
        return restURL + parameters;
    };

    this.getDevelopersURL = function () {
        return "developers";
    };

    this.convertDate = function (date) {
        return new Date(date + this.timezoneOffset);  // local to utc time
    };
}


