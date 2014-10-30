// https://developers.google.com/chart/interactive/docs/reference

ChartPanel = function () {
    this.activityChart = new google.visualization.PieChart(document.getElementById('pieChart1'));
    this.activityOptions = {
        title: 'Developer Activities',
        chartArea: {width: '100%', height: '100%', left: '20', top: '20'}
    };

    this.visibleChart = new google.visualization.PieChart(document.getElementById('pieChart2'));
    this.visibleOptions = {
        title: 'Chart of visible objects',
        chartArea: {width: '100%', height: '100%', left: '20', top: '20'}
    };

    this.metadataChart = new google.visualization.PieChart(document.getElementById('pieChart3'));
    this.metadataOptions = {
        title: 'Chart of changed lines',
        chartArea: {width: '100%', height: '100%', left: '20', top: '20'}
    };

    this.asynTask = undefined;
    this.hideCharts();
};

ChartPanel.prototype.computeStats = function () {
    var labels = this.computeLabels();
    var visibleMap = {};
    var typesMap = {};
    var linesMap = {};
    // getVisibleItems()
    gGlobals.timeline.getVisibleChartItems(function (index, item) {

        for (var i = 0; i < labels.length; i++) {
            var label = labels[i];
            if (checkIntersection(label.start, label.end, item)) {
                // Vypocitaj statistiky pre Label
                if (visibleMap[label.label] === undefined) {
                    visibleMap[label.label] = 1;
                } else {
                    visibleMap[label.label]++;
                }

                // Statistiky pre pocet zmenenych riadkov
                if (item.metadata.changedLines != undefined) {
                    var value = item.metadata.changedLines;
                    if (linesMap[label.label] === undefined) {
                        linesMap[label.label] = value;
                    } else {
                        linesMap[label.label] += value;
                    }
                }
                break;
            }
        }

        // Activity histogram
        var row = item.content;
        if (typesMap[row] === undefined) {
            typesMap[row] = 1;
        } else {
            typesMap[row]++;
        }
        return true;
    });

    return {visible: visibleMap, types: typesMap, lines: linesMap};
};

ChartPanel.prototype.computeLabels = function () {
    //gGlobals.timeline.timeAxis.step
    var options = gGlobals.timeline.options; // nastavenia neupravuj
    var step = jQuery.extend(true, {}, gGlobals.timeline.step); // deep copy celeho objektu
    step.start();
    var max = 0;
    var dates = [];
    while (!step.end() && max < 100) {
        max++;
        var text = step.getLabelMinor(options) + " of " + step.getLabelMajor(options);
        var cur = new Date(step.getCurrent().getTime());
        step.next();
        var next = new Date(step.getCurrent().getTime());
        dates.push({
            start: cur,
            end: next,
            label: text
        });
    }
    return dates;
};

ChartPanel.prototype.hideCharts = function () {
    $('#pieChart1').hide();
    $('#pieChart2').hide();
    $('#pieChart3').hide();
    $('#legenda').show();
    this.visibleCharts = false;
};

ChartPanel.prototype.showCharts = function () {
    $('#pieChart1').show();
    $('#pieChart2').show();
    $('#pieChart3').show();
    $('#legenda').hide();
    this.visibleCharts = true;
};

ChartPanel.prototype.drawPanel = function () {
    var stats = this.computeStats();
    console.log(stats);

    // Pouzivatelovi sa nezobrazuju ziadne prvky
    var ziadnePrvky = Object.keys(stats.visible).length == 0;
    if (this.visibleCharts && ziadnePrvky) {
        this.hideCharts();
        //return;
    } else if (!this.visibleCharts && !ziadnePrvky) {
        this.showCharts();
    }

    this.drawCharts(stats);
};

ChartPanel.prototype.drawCharts = function (stats) {
    var data;

    // Activity
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Name');
    data.addColumn('number', 'Count');
    Object.keys(stats.types).forEach(function (key) {
        data.addRow([ key, stats.types[key]]);
    });
    this.activityChart.draw(data, this.activityOptions);

    // Label
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Date');
    data.addColumn('number', 'Count');
    Object.keys(stats.visible).forEach(function (key) {
        data.addRow([ key, stats.visible[key]]);
    });
    this.visibleChart.draw(data, this.visibleOptions);

    // Changed lines
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Date');
    data.addColumn('number', 'Changed lines');
    Object.keys(stats.lines).forEach(function (key) {
        data.addRow([ key, stats.lines[key]]);
    });
    this.metadataChart.draw(data, this.metadataOptions);
};

ChartPanel.prototype.redraw = function () {
    if (this.asynTask) {
        clearTimeout(this.asynTask);
        delete this.asynTask;
    }
    var instance = this;
    this.asynTask = setTimeout(function () {
        // Run asynchronous task
        instance.drawPanel();
    }, 1000);
};
