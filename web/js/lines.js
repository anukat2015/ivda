/**
 * Created by Seky on 22. 10. 2014.
 */

function GraphLines() {
    this.lines = {};
    this.createLine('changedLines', 'Changed lines');
    this.createLine('changedInFuture', 'Changed in future');

    this.createLine = function (type) {
        return this.createLine(type, type);
    };

    this.createLine = function (type, name) {
        if (!this.lines.hasOwnProperty(type)) {
            this.lines[type] = {
                'label': name,
                'data': []
            };
        }
        return this.lines[type].data;
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
}
