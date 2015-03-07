// https://developers.google.com/chart/interactive/docs/reference

ChartPanel = function () {
    this.parent = undefined;
    this.asynTask = undefined;
};

ChartPanel.prototype.init = function (component) {
    this.parent = component;
    // gGlobals.graphs.visibleComponents[1].charts.parent.dom
    this.activityChart = new google.visualization.PieChart(this.parent.dom.find('.pieChart1')[0]);
    this.activityOptions = {
        title: 'Developer Activities',
        chartArea: {width: '100%', height: '100%', left: '20', top: '20'}
    };

    this.visibleChart = new google.visualization.PieChart(this.parent.dom.find('.pieChart2')[0]);
    this.visibleOptions = {
        title: 'Chart of visible objects per time',
        chartArea: {width: '100%', height: '100%', left: '20', top: '20'}
    };

    this.metadataChart = new google.visualization.PieChart(this.parent.dom.find('.pieChart3')[0]);
    this.metadataOptions = {
        title: 'Chart of changed lines of code',
        chartArea: {width: '100%', height: '100%', left: '20', top: '20'}
    };

    this.hideCharts();
};

ChartPanel.prototype.computeStats = function () {
    var labels = this.computeLabels();
    var visibleMap = {};
    var typesMap = {};
    var linesMap = {};
    // getVisibleItems()
    this.parent.diagram.getVisibleChartItems(function (index, item) {

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
                if (item.group.content == "Ide") {
                    var value = item.metadata.y;
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
        var row = item.group.content;
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
    var options = this.parent.diagram.options; // nastavenia neupravuj
    var step = jQuery.extend(true, {}, this.parent.diagram.step); // deep copy celeho objektu
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
    this.parent.dom.find('.charts').hide();
    this.parent.dom.find('.legenda').show("highlight");
    this.visibleCharts = false;
};

ChartPanel.prototype.showCharts = function () {
    this.parent.dom.find('.charts').show();
    this.parent.dom.find('.legenda').hide();
    this.visibleCharts = true;
};

ChartPanel.prototype.drawPanel = function () {
    var stats = this.computeStats();
    console.log(stats);

    // Pouzivatelovi sa nezobrazuju ziadne prvky
    var ziadnePrvky = $.isEmptyObject(stats.visible);
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
