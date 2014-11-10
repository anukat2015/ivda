/**
 * Created by Seky on 31. 10. 2014.
 */
function IvdaService() {
    this.timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000 * 2;  // day light saving

    this.getTimelineURL = function (start, end, developer) {
        var restURL = "datatable?";
        var parameters = $.param({
            start: this.convertDate(start.getTime()).toISOString(),
            end: this.convertDate(end.getTime()).toISOString(),
            developer: developer
        });
        return restURL + parameters;
    };

    this.getProcessesURL = function (start, end, developer) {
        var restURL = "processes?";
        var parameters = $.param({
            start: this.convertDate(start.getTime()).toISOString(),
            end: this.convertDate(end.getTime()).toISOString(),
            developer: developer
        });
        return restURL + parameters;
    };

    this.getStatsURL = function (start, end, developer) {
        var chunkSize = 1000 * 60 * 60 * 24;
        var roundedStart = start.floor(chunkSize);
        var roundedEnd = end.ceil(chunkSize);
        var restURL = "stats?";
        var parameters = $.param({
            start: this.convertDate(roundedStart.getTime()).toISOString(),
            end: this.convertDate(roundedEnd.getTime()).toISOString(),
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
        return new Date(date);  // local to utc time
    };
}


