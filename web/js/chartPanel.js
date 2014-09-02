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
    this.asynTask = undefined;
};

links.ChartPanel.prototype.computeStats = function () {
    var labels = this.computeLabels();
    var labelMap = {};
    var typesMap = {};
    gGlobals.timeline.getVisibleChartItems(function (index, item) {
        // Vypocitaj statistiky pre Label
        for (var i = 0; i < labels.length; i++) {
            var label = labels[i];
            if (gGlobals.timeline.checkIntersection(label.start, label.end, item)) {
                var value = parseInt(item.metadata);
                if (labelMap[label.label] === undefined) {
                    labelMap[label.label] = value;
                } else {
                    labelMap[label.label] += value;
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

    return {label: labelMap, types: typesMap};
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
    var stats = this.computeStats();
    console.log(stats);

    // Activity
    var activityData = new google.visualization.DataTable();
    activityData.addColumn('string', 'Name');
    activityData.addColumn('number', 'Count');
    Object.keys(stats.types).forEach(function (key) {
        activityData.addRow([ key, stats.types[key]]);
    });
    this.activityChart.draw(activityData, this.activityOptions);

    // Label
    var linesData = new google.visualization.DataTable();
    linesData.addColumn('string', 'Date');
    linesData.addColumn('number', 'Changed lines');
    Object.keys(stats.label).forEach(function (key) {
        linesData.addRow([ key, stats.label[key]]);
    });
    if (linesData.getNumberOfRows() > 0) {
        this.visibleChart.draw(linesData, this.visibleOptions);
    }
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
