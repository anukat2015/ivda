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

    this.addItem = function(item) {
        this.items.add(item);
    };


    this.isEmpty = function () {
        return $.isEmptyObject(this.items._data);
    };

}
