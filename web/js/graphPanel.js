/**
 * Created by Seky on 22. 10. 2014.
 */
GraphPanel = function () {
    this.components = {
        changes: $('#graph-changes'),
        activity: $('#graph-activity')
    };
    this.options = {      // Specify options
        width: "100%",
        height: "300px",
        line: {width: 1.0, visible: false},   // apply properties to all lines
        legend: {
            toggleVisibility: true,
            width: "160px"
        },
        lines: [
            {visible: true}, // Povolime zozbrazit len prve 2
            {visible: true}
        ]
    };

    // Instantiate our graph object.
    this.graphs = {
        changes: new links.Graph(document.getElementById('graph-changes')),
        activity: new links.Graph(document.getElementById('graph-activity'))
    };
    this.savedLines = null;
    this.asynTask = undefined;
    this.showProcesses = false;

    // Zatial schovaj
    console.log("graph created");
    this.hide();
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

GraphPanel.prototype.show = function () {
    var instance = this;
    Object.keys(this.components).forEach(function (key) {
        instance.components[key].show();
    })
};

GraphPanel.prototype.hide = function () {
    var instance = this;
    Object.keys(this.components).forEach(function (key) {
        instance.components[key].hide();
    })
};

GraphPanel.prototype.draw = function () {
    var lines = this.computeData();
    if (!lines.hasData()) {
        this.hide();
        return;
    } else {
        this.show();
    }

    var developers = gGlobals.getDevelopers();
    if (!this.showProcesses || developers.length == 0) {
        // Zobraz hned
        this.drawPanel(lines);
    } else {
        // Inak donacitaj este zoznam procesov
        this.savedLines = lines;
        this.loadProcesses();
    }
};

GraphPanel.prototype.drawPanel = function (lines) {
    // Update position
    var range = gGlobals.timeline.getVisibleChartRange();
    this.options.start = range.start;
    this.options.end = range.end;
    //this.graphs.activity.setVisibleChartRange(range);
    //this.graphs.changes.setVisibleChartRange(range);
    var data;

    // Draw activity graph
    data = new Array();
    Object.keys(lines.lines).forEach(function (key) {
        if (key === "changedLines" || key === "changedInFuture") {
            // ignore
        } else {
            data.push(lines.lines[key]);
        }
    });
    if (data.length > 0) {
        this.graphs.activity.draw(data, this.options);
    }

    // Draw changes graph
    data = new Array();
    data.push(lines.lines.changedLines);
    data.push(lines.lines.changedInFuture);
    this.graphs.changes.draw(data, this.options);
};

GraphPanel.prototype.computeData = function () {
    var lines = new GraphLines();
    lines.createLine2('changedLines', 'Changed lines');
    lines.createLine2('changedInFuture', 'Changed in future');
    var grouping = new ProcessAsGroup();

    // Prechadzaj vsetky prvky, vypocitaj skupinu a popri tom dalsie vlasnosti
    var items = gGlobals.timeline.getSortedVisibleChartItems();
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        grouping.processItem(item);

        // Ak to ma metadata
        if (item.metadata != undefined) {
            if (item.metadata.changedLines != undefined) {
                lines.addPoint("changedLines", item.start, item.metadata.changedLines);
            }
            if (item.metadata.changedInFuture != undefined) {
                lines.addPoint("changedInFuture", item.start, item.metadata.changedInFuture);
            }
        }
    }

    grouping.finish();
    this.graphAddGroups(lines, grouping.groups);
    console.log(lines);
    return lines;
};

GraphPanel.prototype.normalizeTime = function (value) {
    return value / (1000 * 60); // jednotka v minutach
};

GraphPanel.prototype.loadProcesses = function () {
    var instance = this;
    var range = gGlobals.timeline.getVisibleChartRange();
    var url = gGlobals.getProcessesServiceURL(new Date(range.start), new Date(range.end));

    $.ajax({
        url: url
    }).then(function (processes) {
        console.log(processes);
        instance.graphAddProcesses(instance.savedLines, processes);
        instance.drawPanel(instance.savedLines);
    }, function (xhr, status, error) {
        alert(error);
    });
};

GraphPanel.prototype.graphAddProcesses = function (lines, processes) {
    var process, value;
    for (var i = 0; i < processes.length; i++) {
        process = processes[i];
        value = this.normalizeTime(process.end - process.start);
        lines.addInterval(process.name, new Date(process.start + gGlobals.timezoneOffset), new Date(process.end + gGlobals.timezoneOffset), value);
    }
};

GraphPanel.prototype.graphAddGroups = function (lines, groups) {
    // Prechazaj skupiny a dopis udaje
    var group, value;
    for (var i = 0; i < groups.length; i++) {
        group = groups[i];
        value = this.normalizeTime(group.getTimeInterval());
        lines.addInterval(group.getFirstEvent().content, group.getFirstEvent().start, group.getLastEvent().start, value);
    }
};


