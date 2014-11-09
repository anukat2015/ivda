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
        barChart: {width: 0, align: 'center'}, // align: left, center, right
        drawPoints: true,
        dataAxis: {
            icons: false
        },
        orientation: 'top'
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

    $(".vis.timeline .center > .content").append('   \
        <div class="timeline-navigation ui-widget ui-state-highlight ui-corner-all"  \
    style="position: absolute;right: 7px;top: 53%;">                   \
        <div class="timeline-navigation-zoom-in" title="Zoom in"><span class="ui-icon ui-icon-circle-zoomin"></span>  \
        </div>                                                                                                 \
        <div class="timeline-navigation-zoom-out" title="Zoom out"><span class="ui-icon ui-icon-circle-zoomout"></span>  \
        </div>    \
        <div class="timeline-navigation-move-left" title="Move left"><span  \
        class="ui-icon ui-icon-circle-arrow-w"></span></div>   \
        <div class="timeline-navigation-move-right" title="Move right"><span \
        class="ui-icon ui-icon-circle-arrow-e"></span></div>    \
    </div>');
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


        if (item.group == "Ide") {
            label = item.metadata.content;
            if (label != undefined) {
                if (pathsMap[label] === undefined) {
                    pathsMap[label] = 1;
                } else {
                    pathsMap[label]++;
                }
            }
        }

        if (item.group == "Web") {
            label = item.metadata.content;
            if (label != undefined) {
                label = new URL(label).hostname;
                if (domainsMap[label] === undefined) {
                    domainsMap[label] = 1;
                } else {
                    domainsMap[label]++;
                }
            }
        }

        // Ak to ma metadata
        if (item.metadata != undefined) {
            label = item.metadata.y;
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

        }
    }
    // Show skupiny
    grouping.finish();
    var activities = this.graphAddGroups(grouping);
    this.graphs.activities.graph.setGroups(activities.groups);
    this.graphs.activities.graph.setItems(activities.items);
    this.graphs.activities.graph.redraw();


    // Zaujimave statistiky
    this.loadStats();

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

GraphPanel.prototype.loadStats = function () {
    var instance = this;
    var range = gGlobals.timeline.panel.getVisibleChartRange();
    var developer = gGlobals.toolbar.getDeveloper();
    var url = gGlobals.service.getStatsURL(new Date(range.start), new Date(range.end), developer);

    var histogram_graph = new GraphData();
    histogram_graph.createGroup2('events', 'Count of events | Per day');
    $.ajax({
        url: url,
        async: false
    }).then(function (data) {
        console.log(data);
        instance.graphs.changes.graph.setGroups(histogram_graph.groups);
        instance.graphs.changes.graph.setItems(data);
        instance.graphs.changes.graph.redraw();
    }, function (xhr, status, error) {
        gGlobals.alertError(error);
    });
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
    activities.groups.add({id: 'A', content: 'Activity', className: 'graphGroupIde', drawPoints: {enabled: false}});

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
            content: content,
            x: group.getFirstEvent().start,
            y: group.inGroup
        };
        activities.addItem(obj);
    }
    console.log(activities);
    return activities;
};
