// https://developers.google.com/chart/interactive/docs/reference

links.ChartPanel = function () {
    this.activityChart = new google.visualization.PieChart(document.getElementById('pieChart1'));
    this.activityOptions = {
        title: 'Developer Activities'
    };
    this.visibleChart = new google.visualization.PieChart(document.getElementById('pieChart2'));
    this.visibleOptions = {
        title: 'Histogram of visible objects'
    }
};

links.ChartPanel.prototype.computeActivityHistogram = function (columnIndex, data) {
    var map = {};
    for (var rowIndex = 0; rowIndex < data.getNumberOfRows(); rowIndex++) {
        var row = data.getValue(rowIndex, columnIndex);
        if (map[row] === undefined) {
            map[row] = 1;
        } else {
            map[row]++;
        }
    }
    return map;
};

links.ChartPanel.prototype.drawActivityChart = function(data) {
    var histogram = computeHistogram(2, data);
    var chartData = new google.visualization.DataTable();
    chartData.addColumn('string', 'Name');
    chartData.addColumn('number', 'Count');
    chartData.addRows(Object.keys(histogram).map(function (key) {
        return [ key, histogram[key] ]
    }));
    this.activityChart.draw(chartData, this.activityOptions);
};


links.ChartPanel.prototype.computeSum = function (cur, next) {
    var sum = 0;
    gGlobals.timeline.getVisibleItems(cur, next, function (row) {
        sum += parseInt(gTimeline.getData().getValue(row, 5));
    });
    return sum;
};

links.ChartPanel.prototype.computeVisibleHistogram = function (supplier) {
    var options = gGlobals.timeline.options; // nastavenia neupravuj
    var step = jQuery.extend(true, {}, gTimeline.step); // deep copy celeho objektu
    step.start();
    var max = 0;
    while (!step.end() && max < 100) {
        max++;
        var text = step.getLabelMajor(options) + ":" + step.getLabelMinor(options);
        var cur = new Date(step.getCurrent().getTime());   // musime klonovat objekt
        step.next();
        var next = new Date(step.getCurrent().getTime());
        supplier(text, this.computeSum(cur, next));
    }
};

links.ChartPanel.prototype.drawVisibleChart = function () {
    var chartData = new google.visualization.DataTable();
    chartData.addColumn('string', 'Date');
    chartData.addColumn('number', 'Changed lines');
    this.computeVisibleHistogram(function (key, value) {
        chartData.addRow(key, value);
    });
    this.visibleChart.draw(chartData, this.visibleOptions);
};