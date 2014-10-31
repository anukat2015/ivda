/**
 * Created by Seky on 22. 10. 2014.
 */
GraphPanel = function () {
    var options;
    options = {      // Specify options
        width: "100%",
        height: "300px",
        // CatmullRom: false
        legend: {
            right: {position: 'top-right'},
            left: {position: 'top-left'}
        },
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
        width: "100%",
        height: "300px",
        stack: false,
        margin: {
            item: 10, // minimal margin between items
            axis: 5   // minimal margin between items and the axis
        },
        orientation: 'top'
    };

    var activities = {
        component: $('#graph-activity'),
        graph: new vis.Timeline(document.getElementById('graph-activity'), new vis.DataSet(), options)
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
    //this.hide();
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
    // Update position
    var range = gGlobals.timeline.panel.getVisibleChartRange();
    var instance = this;
    Object.keys(this.graphs).forEach(function (key) {
        instance.graphs[key].graph.setOptions(range);   // nastav okno na vidditelnu cast
    });

   // var lines =
        this.computeData();
    /*if (lines.isEmpty()) {
     this.hide();
     return;
     } else {
     this.show();
     } */

    //if (!this.showProcesses) {
    // Zobraz hned
    // this.drawPanel(lines);
    /*} else {
     // Inak donacitaj este zoznam procesov
     this.savedLines = lines;
     this.loadProcesses();
     }  */
};
/*
 GraphPanel.prototype.drawPanel = function (alldata) {


 // Draw changes graph
 var data;

 data = alldata.changes;
 this.graphs.changes.graph.setGroups(data.groups);
 this.graphs.changes.graph.setItems(data.items);
 this.graphs.changes.graph.redraw();

 data = alldata.activites;
 this.graphs.activites.graph.setGroups(data.groups);
 this.graphs.activites.graph.setItems(data.items);
 this.graphs.activites.graph.redraw();
 };
 */

GraphPanel.prototype.computeData = function () {
    var changes = new GraphData();
    changes.createGroup2('changedInFuture', 'Changed in future');
    changes.createGroup2('changedInHistory', 'Changed in past');
    changes.groups.add({id: "changedLines", content: "LOC changed"//, options: { yAxisOrientation: 'right' } // sposobuje abnormalne lagovanie
    });

    var grouping = new ProcessAsGroup();
    var pathsMap = {};
    var domainsMap = {};

    // Prechadzaj vsetky prvky, vypocitaj skupinu a popri tom dalsie vlasnosti
    var label, item;
    var items = gGlobals.timeline.panel.getSortedVisibleChartItems();
    for (var i = 0; i < items.length; i++) {
        item = items[i];
        grouping.processItem(item);

        // Ak to ma metadata
        if (item.metadata != undefined) {
            label = item.metadata.changedLines;
            if (label != undefined) {
                changes.addPoint("changedLines", item.start, label);
            }
            label = item.metadata.changedInFuture;
            if (label != undefined) {
                changes.addPoint("changedInFuture", item.start, label);
            }
            label = item.metadata.changedInHistory;
            if (label != undefined) {
                changes.addPoint("changedInHistory", item.start, label);
            }
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

        }
    }
    grouping.finish();


    this.graphs.changes.graph.setGroups(changes.groups);
    this.graphs.changes.graph.setItems(changes.items);
    this.graphs.changes.graph.redraw();

    var activities = this.graphAddGroups(grouping);
    this.graphs.activities.graph.setGroups(activities.groups);
    this.graphs.activities.graph.setItems(activities.items);
    this.graphs.activities.graph.redraw();


    var gdata, options, chart;

    // Files modifications
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

    // Domain visit, in counts
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
};

GraphPanel.prototype.normalizeTime = function (value) {
    return value / (1000 * 60); // jednotka v minutach
};
/*
 GraphPanel.prototype.loadProcesses = function () {
 var instance = this;
 var range = gGlobals.timeline.panel.getVisibleChartRange();
 var developer = gGlobals.toolbar.getDeveloper();
 var url = gGlobals.service.getProcessesURL(new Date(range.start), new Date(range.end), developer);

 $.ajax({
 url: url
 }).then(function (processes) {
 console.log(processes);
 instance.graphAddProcesses(instance.savedLines, processes);
 instance.drawPanel(instance.savedLines);
 }, function (xhr, status, error) {
 gGlobals.alertError(error);
 });
 };

 GraphPanel.prototype.graphAddProcesses = function (lines, processes) {
 var process, value;
 var service = gGlobals.service;
 for (var i = 0; i < processes.length; i++) {
 process = processes[i];
 value = this.normalizeTime(process.end - process.start);
 lines.addInterval(process.name, service.convertDate(process.start), service.convertDate(process.end), value);
 }
 };
 */
GraphPanel.prototype.graphAddGroups = function (grouping) {
    // Prechazaj skupiny a dopis udaje
    var activities = new GraphData();
    activities.createGroup2('A', 'Activity');

    var group, url, obj, content;
    for (var i = 0; i < grouping.groups.length; i++) {
        group = grouping.groups[i];
        //value = this.normalizeTime(group.getTimeInterval());
        url = group.getFirstEvent().metadata.link;
        if (url != undefined) {
            content = new URL(url).hostname;
        } else {
            content = group.getFirstEvent().content;
        }
        obj = {
            id: i,
            group: "A",
            start: group.getFirstEvent().start,
            end: group.getLastEvent().start,
            content: content
        };
        activities.addItem(obj);
    }
    console.log(activities);
    return activities;
};


