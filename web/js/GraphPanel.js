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

    /*
     this.graphs.changes.graph.setGroups(changes.groups);
     this.graphs.changes.graph.setItems(changes.items);
     this.graphs.changes.graph.redraw();
     */
    var activities = this.graphAddGroups(grouping);
    /*
    this.graphs.changes.graph.setGroups(activities.groups);
    this.graphs.changes.graph.setItems(activities.items);
    this.graphs.changes.graph.redraw();
    */
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

     /*
     var histogram = [
     ['Date', 'Count'],
     ['Mon Jun 02 00:00:00 CEST 2014',	128],
     ['Tue Jun 03 00:00:00 CEST 2014',	1093],
     ['Wed Jun 04 00:00:00 CEST 2014',	591],
     ['Thu Jun 05 00:00:00 CEST 2014',	995],
     ['Fri Jun 06 00:00:00 CEST 2014',	306],
     ['Sun Jun 08 00:00:00 CEST 2014',	115],
     ['Mon Jun 09 00:00:00 CEST 2014',	1008],
     ['Tue Jun 10 00:00:00 CEST 2014',	4080],
     ['Wed Jun 11 00:00:00 CEST 2014',	802],
     ['Thu Jun 12 00:00:00 CEST 2014',	2630],
     ['Fri Jun 13 00:00:00 CEST 2014',	3749],
     ['Sat Jun 14 00:00:00 CEST 2014',	3200],
     ['Sun Jun 15 00:00:00 CEST 2014',	4646],
     ['Mon Jun 16 00:00:00 CEST 2014',	1640],
     ['Tue Jun 17 00:00:00 CEST 2014',	3894],
     ['Wed Jun 18 00:00:00 CEST 2014',	1388],
     ['Thu Jun 19 00:00:00 CEST 2014',	1266],
     ['Fri Jun 20 00:00:00 CEST 2014',	2328],
     ['Sat Jun 21 00:00:00 CEST 2014',	739],
     ['Sun Jun 22 00:00:00 CEST 2014',	579],
     ['Mon Jun 23 00:00:00 CEST 2014',	1432],
     ['Tue Jun 24 00:00:00 CEST 2014',	9162],
     ['Wed Jun 25 00:00:00 CEST 2014',	2782],
     ['Thu Jun 26 00:00:00 CEST 2014',	2567],
     ['Fri Jun 27 00:00:00 CEST 2014',	2944],
     ['Sat Jun 28 00:00:00 CEST 2014',	1445],
     ['Sun Jun 29 00:00:00 CEST 2014',	3784],
     ['Mon Jun 30 00:00:00 CEST 2014',	47602],
     ['Tue Jul 01 00:00:00 CEST 2014',	1259],
     ['Wed Jul 02 00:00:00 CEST 2014',	18496],
     ['Thu Jul 03 00:00:00 CEST 2014',	16271],
     ['Fri Jul 04 00:00:00 CEST 2014',	1160],
     ['Sat Jul 05 00:00:00 CEST 2014',	53],
     ['Sun Jul 06 00:00:00 CEST 2014',	5437],
     ['Mon Jul 07 00:00:00 CEST 2014',	15895],
     ['Tue Jul 08 00:00:00 CEST 2014',	10244],
     ['Wed Jul 09 00:00:00 CEST 2014',	2845],
     ['Thu Jul 10 00:00:00 CEST 2014',	11991],
     ['Fri Jul 11 00:00:00 CEST 2014',	919],
     ['Sat Jul 12 00:00:00 CEST 2014',	532],
     ['Mon Jul 14 00:00:00 CEST 2014',	864],
     ['Tue Jul 15 00:00:00 CEST 2014',	6243],
     ['Wed Jul 16 00:00:00 CEST 2014',	9980],
     ['Thu Jul 17 00:00:00 CEST 2014',	1664],
     ['Fri Jul 18 00:00:00 CEST 2014',	395],
     ['Sat Jul 19 00:00:00 CEST 2014',	5],
     ['Sun Jul 20 00:00:00 CEST 2014',	458],
     ['Mon Jul 21 00:00:00 CEST 2014',	4004],
     ['Tue Jul 22 00:00:00 CEST 2014',	3283],
     ['Wed Jul 23 00:00:00 CEST 2014',	13569],
     ['Thu Jul 24 00:00:00 CEST 2014',	248],
     ['Fri Jul 25 00:00:00 CEST 2014',	462],
     ['Sat Jul 26 00:00:00 CEST 2014',	2],
     ['Mon Jul 28 00:00:00 CEST 2014',	601],
     ['Tue Jul 29 00:00:00 CEST 2014',	1024],
     ['Wed Jul 30 00:00:00 CEST 2014',	313],
     ['Thu Jul 31 00:00:00 CEST 2014',	556],
     ['Fri Aug 01 00:00:00 CEST 2014',	486],
     ['Sun Aug 03 00:00:00 CEST 2014',	5],
     ['Mon Aug 04 00:00:00 CEST 2014',	535],
     ['Tue Aug 05 00:00:00 CEST 2014',	385],
     ['Wed Aug 06 00:00:00 CEST 2014',	666],
     ['Thu Aug 07 00:00:00 CEST 2014',	301],
     ['Fri Aug 08 00:00:00 CEST 2014',	562],
     ['Sat Aug 23 00:00:00 CEST 2014',	22],
     ['Sun Aug 24 00:00:00 CEST 2014',	183],
     ['Mon Aug 25 00:00:00 CEST 2014',	96],
     ['Tue Aug 26 00:00:00 CEST 2014',	444],
     ['Wed Aug 27 00:00:00 CEST 2014',	46],
     ['Thu Aug 28 00:00:00 CEST 2014',	2284],
     ['Fri Aug 29 00:00:00 CEST 2014',	2276],
     ['Sat Aug 30 00:00:00 CEST 2014',	1121],
     ['Sun Aug 31 00:00:00 CEST 2014',	442],
     ['Mon Sep 01 00:00:00 CEST 2014',	449],
     ['Tue Sep 02 00:00:00 CEST 2014',	967],
     ['Wed Sep 03 00:00:00 CEST 2014',	452],
     ['Thu Sep 04 00:00:00 CEST 2014',	91],
     ['Fri Sep 05 00:00:00 CEST 2014',	1247],
     ['Sat Sep 06 00:00:00 CEST 2014',	732],
     ['Sun Sep 07 00:00:00 CEST 2014',	173],
     ['Mon Sep 08 00:00:00 CEST 2014',	2176],
     ['Wed Sep 10 00:00:00 CEST 2014',	48],
     ['Thu Sep 11 00:00:00 CEST 2014',	709],
     ['Fri Sep 12 00:00:00 CEST 2014',	112],
     ['Mon Sep 15 00:00:00 CEST 2014',	145],
     ['Tue Sep 16 00:00:00 CEST 2014',	955],
     ['Wed Sep 17 00:00:00 CEST 2014',	973],
     ['Thu Sep 18 00:00:00 CEST 2014',	1756],
     ['Fri Sep 19 00:00:00 CEST 2014',	391],
     ['Sun Sep 21 00:00:00 CEST 2014',	180],
     ['Mon Sep 22 00:00:00 CEST 2014',	1313],
     ['Tue Sep 23 00:00:00 CEST 2014',	915],
     ['Wed Sep 24 00:00:00 CEST 2014',	1886],
     ['Thu Sep 25 00:00:00 CEST 2014',	1601],
     ['Fri Sep 26 00:00:00 CEST 2014',	813],
     ['Sat Sep 27 00:00:00 CEST 2014',	38],
     ['Sun Sep 28 00:00:00 CEST 2014',	1079],
     ['Mon Sep 29 00:00:00 CEST 2014',	948],
     ['Tue Sep 30 00:00:00 CEST 2014',	1624],
     ['Wed Oct 01 00:00:00 CEST 2014',	1143],
     ['Thu Oct 02 00:00:00 CEST 2014',	891],
     ['Fri Oct 03 00:00:00 CEST 2014',	1328],
     ['Sat Oct 04 00:00:00 CEST 2014',	22],
     ['Sun Oct 05 00:00:00 CEST 2014',	306],
     ['Mon Oct 06 00:00:00 CEST 2014',	1143],
     ['Tue Oct 07 00:00:00 CEST 2014',	1262],
     ['Wed Oct 08 00:00:00 CEST 2014',	118],
     ['Thu Oct 09 00:00:00 CEST 2014',	3819],
     ['Fri Oct 10 00:00:00 CEST 2014',	352],
     ['Sat Oct 11 00:00:00 CEST 2014',	441],
     ['Sun Oct 12 00:00:00 CEST 2014',	4936],
     ['Mon Oct 13 00:00:00 CEST 2014',	4230],
     ['Tue Oct 14 00:00:00 CEST 2014',	817],
     ['Wed Oct 15 00:00:00 CEST 2014',	284],
     ['Thu Oct 16 00:00:00 CEST 2014',	392],
     ['Fri Oct 17 00:00:00 CEST 2014',	1],
     ['Sat Oct 18 00:00:00 CEST 2014',	101],
     ['Sun Oct 19 00:00:00 CEST 2014',	149]
     ];
     */

    var histogram = [
        [1401660000000, 128],
        [1401746400000, 1093],
        [1401832800000, 591],
        [1401919200000, 995],
        [1402005600000, 306],
        [1402178400000, 115],
        [1402264800000, 1008],
        [1402351200000, 4080],
        [1402437600000, 802],
        [1402524000000, 2630],
        [1402610400000, 3749],
        [1402696800000, 3200],
        [1402783200000, 4646],
        [1402869600000, 1640],
        [1402956000000, 3894],
        [1403042400000, 1388],
        [1403128800000, 1266],
        [1403215200000, 2328],
        [1403301600000, 739],
        [1403388000000, 579],
        [1403474400000, 1432],
        [1403560800000, 9162],
        [1403647200000, 2782],
        [1403733600000, 2567],
        [1403820000000, 2944],
        [1403906400000, 1445],
        [1403992800000, 3784],
        [1404079200000, 47602],
        [1404165600000, 1259],
        [1404252000000, 18496],
        [1404338400000, 16271],
        [1404424800000, 1160],
        [1404511200000, 53],
        [1404597600000, 5437],
        [1404684000000, 15895],
        [1404770400000, 10244],
        [1404856800000, 2845],
        [1404943200000, 11991],
        [1405029600000, 919],
        [1405116000000, 532],
        [1405288800000, 864],
        [1405375200000, 6243],
        [1405461600000, 9980],
        [1405548000000, 1664],
        [1405634400000, 395],
        [1405720800000, 5],
        [1405807200000, 458],
        [1405893600000, 4004],
        [1405980000000, 3283],
        [1406066400000, 13569],
        [1406152800000, 248],
        [1406239200000, 462],
        [1406325600000, 2],
        [1406498400000, 601],
        [1406584800000, 1024],
        [1406671200000, 313],
        [1406757600000, 556],
        [1406844000000, 486],
        [1407016800000, 5],
        [1407103200000, 535],
        [1407189600000, 385],
        [1407276000000, 666],
        [1407362400000, 301],
        [1407448800000, 562],
        [1408744800000, 22],
        [1408831200000, 183],
        [1408917600000, 96],
        [1409004000000, 444],
        [1409090400000, 46],
        [1409176800000, 2284],
        [1409263200000, 2276],
        [1409349600000, 1121],
        [1409436000000, 442],
        [1409522400000, 449],
        [1409608800000, 967],
        [1409695200000, 452],
        [1409781600000, 91],
        [1409868000000, 1247],
        [1409954400000, 732],
        [1410040800000, 173],
        [1410127200000, 2176],
        [1410300000000, 48],
        [1410386400000, 709],
        [1410472800000, 112],
        [1410732000000, 145],
        [1410818400000, 955],
        [1410904800000, 973],
        [1410991200000, 1756],
        [1411077600000, 391],
        [1411250400000, 180],
        [1411336800000, 1313],
        [1411423200000, 915],
        [1411509600000, 1886],
        [1411596000000, 1601],
        [1411682400000, 813],
        [1411768800000, 38],
        [1411855200000, 1079],
        [1411941600000, 948],
        [1412028000000, 1624],
        [1412114400000, 1143],
        [1412200800000, 891],
        [1412287200000, 1328],
        [1412373600000, 22],
        [1412460000000, 306],
        [1412546400000, 1143],
        [1412632800000, 1262],
        [1412719200000, 118],
        [1412805600000, 3819],
        [1412892000000, 352],
        [1412978400000, 441],
        [1413064800000, 4936],
        [1413151200000, 4230],
        [1413237600000, 817],
        [1413324000000, 284],
        [1413410400000, 392],
        [1413496800000, 1],
        [1413583200000, 101],
        [1413669600000, 149]
    ];

    var histogram_graph = new GraphData();
    histogram_graph.createGroup2('events', 'Count of events | Per day');
    for (i = 0; i < histogram.length - 1; i++) {
        //histogram_graph.addPoint("events", new Date(histogram[i][0]), histogram[i][1]);
        var obj = {
            group: "events",
            x: new Date(histogram[i][0]),
            end: new Date(histogram[i+1][0]),
            y: histogram[i][1]
        };
        histogram_graph.addItem(obj);
    }
    this.graphs.changes.graph.setGroups(histogram_graph.groups);
    this.graphs.changes.graph.setItems(histogram_graph.items);
    this.graphs.changes.graph.redraw();
    //gdata = google.visualization.arrayToDataTable(histogram);

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
