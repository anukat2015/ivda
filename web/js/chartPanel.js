// https://developers.google.com/chart/interactive/docs/reference

links.ChartPanel = function () {
    this.activityChart = new google.visualization.PieChart(document.getElementById('pieChart1'));
    this.activityOptions = {
        title: 'Developer Activities',
        chartArea: {width: '100%', height: '100%', left: '5%', top: '15%'}
    };


    this.visibleChart = new google.visualization.PieChart(document.getElementById('pieChart2'));
    this.visibleOptions = {
        title: 'Histogram of visible objects',
        chartArea: {width: '100%', height: '100%', left: '5%', top: '15%'}
    };

    this.metadataChart = new google.visualization.PieChart(document.getElementById('pieChart3'));
    this.metadataOptions = {
        title: 'Chart of changed lines',
        chartArea: {width: '100%', height: '100%', left: '5%', top: '15%'}
    };

    this.asynTask = undefined;
};

links.ChartPanel.prototype.computeStats = function () {
    var labels = this.computeLabels();
    var visibleMap = {};
    var typesMap = {};
    var linesMap = {};
    gGlobals.timeline.getVisibleChartItems(function (index, item) {

        for (var i = 0; i < labels.length; i++) {
            var label = labels[i];
            if (gGlobals.timeline.checkIntersection(label.start, label.end, item)) {
                // Vypocitaj statistiky pre Label
                if (visibleMap[label.label] === undefined) {
                    visibleMap[label.label] = 1;
                } else {
                    visibleMap[label.label]++;
                }

                // Statistiky pre pocet zmenenych riadkov
                var value = parseInt(item.metadata.changedLines);
                if (linesMap[label.label] === undefined) {
                    linesMap[label.label] = value;
                } else {
                    linesMap[label.label] += value;
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
    });

    return {visible: visibleMap, types: typesMap, lines: linesMap};
};

links.ChartPanel.prototype.computeLabels = function () {
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

links.ChartPanel.prototype.drawCharts = function () {
    var data;
    var stats = this.computeStats();
    console.log(stats);

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

links.ChartPanel.prototype.redraw = function () {
    if (this.asynTask) {
        clearTimeout(this.asynTask);
        delete this.asynTask;
    }
    this.asynTask = setTimeout(function () {
        // Run asynchronous task
        gGlobals.charts.drawCharts();
    }, 1000);
};
