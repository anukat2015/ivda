/**
 * Created by Seky on 22. 10. 2014.
 */

function GraphData() {
    this.groups = new vis.DataSet();
    this.items = new vis.DataSet();
    this.itemIndex = 0;

    this.createGroup2 = function (type, name) {
        if (!this.groups.get(type)) {
            this.groups.add({id: type, content: name});
        }
    };

    this.createGroup = function (type) {
        this.createGroup2(type, type);
    };

    this.addPoint = function (type, date, value) {
        this.items.add({x: date, y: value, group: type});
    };

    /*
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
     */

    /*
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
     */
}
