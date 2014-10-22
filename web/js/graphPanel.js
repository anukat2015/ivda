/**
 * Created by Seky on 22. 10. 2014.
 */
GraphPanel = function () {
    this.component = $('#mygraph');
    this.graph = this.buildStructure();
    this.asynTask = undefined;

    // Zatial schovaj
    this.component.hide();
};

GraphPanel.prototype.buildStructure = function () {
    // specify options
    var options = {
        width: "100%",
        height: "100px",
        moveable: false,
        line: {width: 2.0},   // apply width to all lines
        legend: {toggleVisibility: true}
    };

    // Instantiate our graph object.
    var graph = new links.Graph(this.component);
    //google.visualization.events.addListener(graph, 'rangechange', graphOnrangechange);
    return graph;
};

GraphPanel.prototype.redraw = function () {
    if (this.asynTask) {
        clearTimeout(this.asynTask);
        delete this.asynTask;
    }
    var instance = this;
    this.asynTask = setTimeout(function () {
        // Run asynchronous task
        instance.draw();
    }, 1000);
};

GraphPanel.prototype.draw = function () {
    this.component.show();
    var stats = this.computeData();
    this.drawPanel(stats);
};

ChartPanel.prototype.drawPanel = function (stats) {
    var data = new google.visualization.DataTable();
    data.addColumn('datetime', 'time');
    Object.keys(stats.names).forEach(function (key) {
        data.addColumn('number', key);
    });
    Object.keys(stats.data).forEach(function (key) {
        data.addRow([ key, stats.lines[key]]);
    });
    this.hrapg.draw(data, this.metadataOptions);
};
/*
function graphOnrangechange() {
    if (gGlobals) {
        var range = this.graph.getVisibleChartRange();
        gGlobals.timeline.setVisibleChartRange(range.start, range.end, true);
    }
}
*/

GraphPanel.prototype.computeRow = function (item) {
    var row;
    row["timepoint"] = item.start;
    // Hodnoty pola casu pouzitia
    row["webValue"]
    row["ideValue"]
    row["processAValue"]
    row["processBValue"]
    row["processCValue"]
    row["processDValue"]

    // Specificke hodnoty
    row["changedLines"]
    row["changedInFuture"]
};

GraphPanel.prototype.computeData = function () {
    var graphStats = {};
    var graphLabels = {};
    var instance = this;
    // Procesy este neriesime
    gGlobals.timeline.getVisibleChartItems(function (index, item) {
        var type = item.content
        var row = instance.computeRow(item);
        graphStats.push(row);

        if (item.metadata.changedLines != undefined)

        // Activity histogram
        var row = item.content;
        if (typesMap[row] === undefined) {
            typesMap[row] = 1;
        } else {
            typesMap[row]++;
            return true;
        });
    };
