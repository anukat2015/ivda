/**
 * Created by Seky on 22. 10. 2014.
 */
GraphPanel = function () {
    var options;
    options = {      // Specify options
        /*width: "100%",
         height: "300px",
         CatmullRom: false  */
        /*legend: {
            right: {position: 'top-right'},
            left: {position: 'top-left'}
        }, */
        style: 'bar',
        barChart: {width: 50, align: 'center'}, // align: left, center, right
        drawPoints: true,
        dataAxis: {
            icons: false
        },
        orientation: 'bottom'
    };

    var changes = {
        component: $('#graph-changes'),
        graph: new vis.Graph2d(document.getElementById('graph-changes'), new vis.DataSet(), options)
    };

    options = {
        style: 'bar',
        barChart: {width: 50, align: 'center', handleOverlap: 'sideBySide'}, // align: left, center, right
        drawPoints: false,
        dataAxis: {
            icons: true
        },
        orientation: 'top'
    };

    var activities = {
        component: $('#graph-activity'),
        graph: new vis.Graph2d(document.getElementById('graph-activity'), new vis.DataSet(), options)
    };

    this.graphs = {
        changes: changes,
        activities: activities
    };

    this.savedData = null;
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
    }, 1000);
};

GraphPanel.prototype.show = function () {
    var instance = this;
    Object.keys(this.graphs).forEach(function (key) {
        instance.graphs[key].component.show();
    });
};

GraphPanel.prototype.hide = function () {
    var instance = this;
    Object.keys(this.graphs).forEach(function (key) {
        instance.graphs[key].component.hide();
    });
};

GraphPanel.prototype.draw = function () {
    var lines = this.computeData();
    /*if (!lines.hasData()) {
     this.hide();
     return;
     } else {  */
    this.show();
    //}

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

GraphPanel.prototype.drawPanel = function (data) {
    // Update position
    var range = gGlobals.timeline.getVisibleChartRange();
    var instance = this;
    Object.keys(this.graphs).forEach(function (key) {
        instance.graphs[key].graph.setOptions(range);   // nastav okno na vidditelnu cast
    });

    // Draw activity graph
    /*data = new Array();
     Object.keys(lines.lines).forEach(function (key) {
     if (key === "changedLines" || key === "changedInFuture") {
     // ignore
     } else {
     data.push(lines.lines[key]);
     }
     });
     if (data.length > 0) {
     this.graphs.activity.draw(data, this.options);
     } */

    // Draw changes graph
    this.graphs.changes.graph.setGroups(data.groups);
    this.graphs.changes.graph.setItems(data.items);
};

GraphPanel.prototype.computeData = function () {
    var data = new GraphData();
    data.createGroup2('changedInFuture', 'Changed in future');
    data.createGroup2('changedInHistory', 'Changed in past');
    data.groups.add({id: "changedLines", content: "LOC changed"//, options: {}
    });
    var grouping = new ProcessAsGroup();
    var pathsMap = {};
    var domainsMap = {};


    // Prechadzaj vsetky prvky, vypocitaj skupinu a popri tom dalsie vlasnosti
    var label, item;
    var items = gGlobals.timeline.getSortedVisibleChartItems();
    for (var i = 0; i < items.length; i++) {
        item = items[i];
        grouping.processItem(item);

        // Ak to ma metadata
        if (item.metadata != undefined) {
            /*label = item.metadata.changedLines;
            if (label != undefined) {
                data.addPoint("changedLines", item.start, label);
            }
            label = item.metadata.changedInFuture;
            if (label != undefined) {
                data.addPoint("changedInFuture", item.start, label);
            }
            label = item.metadata.changedInHistory;
            if (label != undefined) {
                data.addPoint("changedInHistory", item.start, label);
            } */
            /*
            label = item.metadata.path;
            if (label != undefined) {
                if (pathsMap[label] === undefined) {
                    pathsMap[label] = 1;
                } else {
                    pathsMap[label]++;
                }
            }
            label = item.metadata.link;
            if (label != undefined) {
                label = new URL(label).hostname;
                if (domainsMap[label] === undefined) {
                    domainsMap[label] = 1;
                } else {
                    domainsMap[label]++;
                }
            }
            */
        }
    }
    grouping.finish();
    //this.graphAddGroups(data, grouping.groups);
    console.log(data);
    /*
    var gdata, options, chart;

    gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Path');
    gdata.addColumn('number', 'Edits');
    Object.keys(pathsMap).forEach(function (key) {
        gdata.addRow([ key, pathsMap[key]]);
    });
    options = {
        title: 'Files modifications, in counts'
    };
    console.log(gdata);
    chart = new google.visualization.Histogram(document.getElementById('histogram'));
    chart.draw(gdata, options);

    gdata = new google.visualization.DataTable();
    gdata.addColumn('string', 'Domain');
    gdata.addColumn('number', 'Visit');
    Object.keys(domainsMap).forEach(function (key) {
        gdata.addRow([ key, domainsMap[key]]);
    });
    options = {
        title: 'Domain visit, in counts'
    };
    console.log(gdata);
    chart = new google.visualization.Histogram(document.getElementById('histogram2'));
    chart.draw(gdata, options);
    */
    return data;
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


