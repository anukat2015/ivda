/**
 * Created by Seky on 31. 10. 2014.
 */
function IvdaService() {
    this.timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000 * 2;  // day light saving

    this.getTimeline = function (start, end, developer, callback) {
        var parameters = $.param({
            start: start.toISOString(),
            end: end.toISOString(),
            developer: developer
        });
        $.ajax({
            dataType: "json",
            url: "datatable?" + parameters,
            success: function (data, textStatus, jqXHR) {
                callback(data);
            }
        });
    };

    this.getProcesses = function (params, callback) {
        var parameters = $.param({
            start: params.range.start.toISOString(),
            end: params.range.end.toISOString(),
            developer: params.developer
        });
        $.ajax({
            dataType: "json",
            url: "processes?" + parameters,
            success: function (data, textStatus, jqXHR) {
                callback(data);
            }
        });
    };

    this.getData = function (name, params, callback) {
        // Dopis dalsie udaje do requestu
        var p = {};
        p.start = params.range.start.toISOString();
        p.end = params.range.end.toISOString();
        p.developer = params.developer;
        p.attribute = name;
        p.granularity = params.granularity;
        p.classify = 1;

        var instance = this;
        $.ajax({
            url: "stats?" + $.param(p),
            success: function (data, textStatus, jqXHR) {
                instance._convertDates(data);
                callback(data);
            }});
    };

    this.getDevelopers = function (callback) {
        $.ajax({
            url: "developers?",
            success: function (data, textStatus, jqXHR) {
                callback(data);
            }});
    };

    this._convertDate = function (date) {
        return new Date(date + this.timezoneOffset);  // local to utc time
    };

    /**
     * Prichadzajuce eventy zo sluzby je potrebne spracovat.
     * @param events
     */
    this._convertDates = function (events) {
        var item;
        for (var i = 0; i < events.length; i++) {
            item = events[i];
            if (item.start != null) {
                item.start = this._convertDate(item.start);
            }
            if (item.end != null) {
                item.end = this._convertDate(item.end);
            }
        }
    };
}


