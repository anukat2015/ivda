/**
 * Created by Seky on 22. 10. 2014.
 */
GraphPanel = function () {
    this.savedData = null;
    this.asynTask = undefined;
    this.showProcesses = false;
    this.registerNavigationBar();

    this.graphs = {
        countEventsPerDay: this.createDynamicHistogram('graph-countEventsPerDay'),
        countEventsPerHour: this.createDynamicHistogram('graph-countEventsPerHour'),
        locChangesGlobal: this.createDynamicHistogram('graph-locChangesGlobal'),
        locChangesLocal: this.createDynamicHistogram('graph-locChangesLocal'),
        activityHistogram: this.createDynamicHistogram('graph-activityHistogram'),
        activities: this.createTimeline('graph-activities')
    };

    // Zatial schovaj
    //this.hide();
    console.log("graph created");
};

GraphPanel.prototype.createDynamicHistogram = function (name) {
    var options = {      // Specify options
        width: "100%",
        height: "300px",
        // CatmullRom: false
        legend: {
            right: {position: 'top-right'},
            left: {position: 'top-left'}
        },
        style: 'bar',
        barChart: {width: 1, align: 'center'}, // align: left, center, right
        drawPoints: true,
        dataAxis: {
            icons: false
        },
        orientation: 'top'
    };

    return {
        component: $('#' + name),
        graph: new vis.Graph2d(document.getElementById(name), new vis.DataSet(), options)
    };
};

GraphPanel.prototype.createTimeline = function (name) {
    var options = {
        width: "100%",
        height: "300px",
        stack: false,
        margin: {
            item: 10, // minimal margin between items
            axis: 5   // minimal margin between items and the axis
        },
        orientation: 'top'
    };

    return {
        component: $('#' + name),
        graph: new vis.Timeline(document.getElementById(name), new vis.DataSet(), options)
    };
};

GraphPanel.prototype.registerNavigationBar = function () {
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
    changes.createGroup2('changedInFuture', 'Changes of files in future');
    changes.createGroup2('changedInHistory', 'Changes of files in past');
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

        if (item.group.content == "Ide") {
            label = item.content;
            if (label != undefined) {
                if (pathsMap[label] === undefined) {
                    pathsMap[label] = 1;
                } else {
                    pathsMap[label]++;
                }
            }
        }

        if (item.group.content == "Web") {
            label = item.content;
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

    this.graphs.locChangesLocal.graph.setGroups(changes.groups);
    this.graphs.locChangesLocal.graph.setItems(changes.items);
    this.graphs.locChangesLocal.graph.redraw();


    // Zaujimave statistiky
    this.loadStats();
    this.buildValueHistograms(pathsMap, domainsMap);
};

GraphPanel.prototype.buildValueHistograms = function (pathsMap, domainsMap) {
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
    chart = new google.visualization.Histogram(document.getElementById('graph-fileModifications'));
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
    chart = new google.visualization.Histogram(document.getElementById('graph-domainVisits'));
    chart.draw(gdata, options);
};

GraphPanel.prototype.normalizeTime = function (value) {
    return value / (1000 * 60); // jednotka v minutach
};

GraphPanel.prototype.loadStaticsData = function () {
    var instance, developer;
    instance = this;
    developer = gGlobals.toolbar.getDeveloper();

    $.ajax({
        url: gGlobals.service.getStatsURL(new Date("2014-01-01T00:00:00.000"), new Date("2014-10-01T00:00:00.000"), developer, "count", "DAY"),
        success: function (data, textStatus, jqXHR) {
            gGlobals.service.convertDates(data);
            var info = new GraphData();
            info.createGroup2('events', 'Count of events | Per day');
            instance.graphs.countEventsPerDay.graph.setGroups(info.groups);
            instance.graphs.countEventsPerDay.graph.setItems(data);
            instance.graphs.countEventsPerDay.graph.redraw();
        }});
    $.ajax({
        url: gGlobals.service.getStatsURL(new Date("2014-08-03T00:00:00.000"), new Date("2014-08-07T00:00:00.000"), developer, "count", "HOUR"),
        success: function (data, textStatus, jqXHR) {
            gGlobals.service.convertDates(data);
            var info = new GraphData();
            info.createGroup2('events', 'Count of events | Per hour');
            instance.graphs.countEventsPerHour.graph.setGroups(info.groups);
            instance.graphs.countEventsPerHour.graph.setItems(data);
            instance.graphs.countEventsPerHour.graph.redraw();
        }});

    $.ajax({
        url: gGlobals.service.getStatsURL(new Date("2014-08-02T00:00:00.000"), new Date("2014-08-09T00:00:00.000"), developer, "loc", "PER_VALUE"),
        success: function (data, textStatus, jqXHR) {
            gGlobals.service.convertDates(data);
            var info = new GraphData();
            info.createGroup2('events', 'Changes of source codes | LOC per File');
            instance.graphs.locChangesGlobal.graph.setGroups(info.groups);
            instance.graphs.locChangesGlobal.graph.setItems(data);
            instance.graphs.locChangesGlobal.graph.redraw();
        }});


    $.ajax({
        url: gGlobals.service.getStatsURL(new Date("2014-08-01T00:00:00.000"), new Date("2014-08-31T00:00:00.000"), developer, "webDuration", "PER_VALUE"),
        success: function (data, textStatus, jqXHR) {
            // Activity at web pages
            var gdata = new google.visualization.DataTable();
            gdata.addColumn('string', 'Web domain');
            gdata.addColumn('number', 'Duration');
            data.forEach(function (key) {
                gdata.addRow([key.content, key.y]);
            });
            var options = {
                title: 'Duration of visit | Minutes per domain',
                height: 600
                //orientation: 'vertical'
            };
            console.log(gdata);
            chart = new google.visualization.ColumnChart(document.getElementById('graph-webDuration'));
            chart.draw(gdata, options);
        }});
};


GraphPanel.prototype.loadStats = function () {
    var instance, developer, url;
    instance = this;
    developer = gGlobals.toolbar.getDeveloper();

    var range = gGlobals.timeline.panel.getVisibleChartRange();
    $.ajax({
        url: gGlobals.service.getStatsURL(new Date(range.start), new Date(range.end), developer, "activity", "DAY"),
        success: function (data, textStatus, jqXHR) {
            gGlobals.service.convertDates(data);
            var info = new GraphData();
            info.createGroup2('Web', 'Web activities | Unique domains per duration');
            info.createGroup2('Ide', 'Ide activities | Changed LOC per duration');
            instance.graphs.activityHistogram.graph.setGroups(info.groups);
            instance.graphs.activityHistogram.graph.setItems(data);
            instance.graphs.activityHistogram.graph.redraw();
        }});
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
            y: group.inGroup
        };
        activities.addItem(obj);
    }
    console.log(activities);
    return activities;
};
