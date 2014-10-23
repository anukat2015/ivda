/**
 * Created by Seky on 22. 10. 2014.
 */

function GraphLines() {
    this.lines = {};

    this.createLine2 = function (type, name) {
        if (!this.lines.hasOwnProperty(type)) {
            this.lines[type] = {
                'label': name,
                'data': []
            };
        }
        return this.lines[type].data;
    };

    this.createLine = function (type) {
        return this.createLine2(type, type);
    };

    this.addPoint = function (type, date, value) {
        this.createLine(type).push({
            'date': date,
            'value': value
        });
    };

    this.addInterval = function (type, start, end, value) {
        var data = this.createLine(type);
        data.push({
            'date': start,
            'value': value
        });
        data.push({
            'date': end,
            'value': value
        });
    };

    this.hasData = function () {
        var keys = Object.keys(this.lines);
        for (var i = 0; i < keys.length; i++) {
            var data = this.lines[keys[i]].data;
            if (data.length > 0) {
                return true;
            }
        }
        return false;
    };
}
