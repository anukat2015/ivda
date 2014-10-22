/**
 * Created by Seky on 22. 10. 2014.
 */
GraphPanel = function () {
    this.component = $('#mygraph');
    this.options = {      // Specify options
        width: "100%",
        height: "100px",
        moveable: false,
        line: {width: 2.0},   // apply width to all lines
        legend: {toggleVisibility: true}
    };

    // Instantiate our graph object.
    this.graph = new links.Graph(this.component);
    this.savedLines = null;
    this.asynTask = undefined;

    // Zatial schovaj
    this.component.hide();
    console.log("graph created");
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
    }, 500);
};

GraphPanel.prototype.draw = function () {
    console.log("graph draw");
    this.component.show();
    this.savedLines = this.computeData();
    this.loadProcesses();
};

ChartPanel.prototype.drawPanel = function (lines) {
    var data = new Array;
    Object.keys(lines.lines).forEach(function (key) {
        data.push(lines.lines[key]);
    });
    this.graph.draw(data, this.options);
};

GraphPanel.prototype.computeData = function () {
    var lines = new GraphLines();
    var grouping = new ProcessAsGroup();

    // Prechadzaj vsetky prvky, vypocitaj skupinu a popri tom dalsie vlasnosti
    gGlobals.timeline.getVisibleChartItems(function (index, item) {
        // Ak to ma metadata
        if (item.metadata != undefined) {
            if (item.metadata.changedLines != undefined) {
                lines.addPoint("changedLines", item.start, item.metadata.changedLines);
            }
            if (item.metadata.changedInFuture != undefined) {
                lines.addPoint("changedInFuture", item.start, item.metadata.changedInFuture);
            }
        }

        grouping.processItem(item);
    });
    console.log(lines);

    // Prechazaj skupiny a dopis udaje
    console.log("graph computeData");
    for (var i = 0; i < grouping.groups.length; i++) {
        var group = grouping.groups[i];
        lines.addInterval(group.getFirstEvent().content, group.getFirstEvent().start, group.getLastEvent().start, group.getTimeInterval());
    }
    return lines;
};

GraphPanel.prototype.loadProcesses = function () {
    var instance = this;
    var range = gGlobals.timeline.getVisibleChartRange();
    var url = gGlobals.getProcessesServiceURL(new Date(range.start), new Date(range.end));
    console.log(url);
    $.ajax({
        url: url
    }).then(function (content) {
        // Set the tooltip content upon successful retrieval
        var processes = JSON.parse(content);
        console.log(processes);
        for (var i = 0; i < processes.length; i++) {
            var process = processes[i];
            var value = process.end.getTime() - process.start.getTime();
            instance.savedLines.addInterval(process.name, process.start, process.end, value);
        }
        this.drawPanel(instance.savedLines);
    }, function (xhr, status, error) {
        alert(error);
    });
};


