// https://developers.google.com/chart/interactive/docs/reference

function computeHistogram(columnIndex, data) {
    var row;
    var map = {};
    for (var rowIndex = 0; rowIndex < data.getNumberOfRows(); rowIndex++) {
        row = data.getValue(rowIndex, columnIndex);
        if (map[row] === undefined) {
            map[row] = 1;
        } else {
            map[row]++;
        }
    }
    return map;
}

function drawChart1(data) {
    var histogram = computeHistogram(2, data);
    var chartData = new google.visualization.DataTable();
    chartData.addColumn('string', 'Name');
    chartData.addColumn('number', 'Count');
    chartData.addRows(Object.keys(histogram).map(function (key) {
        return [ key, histogram[key] ]
    }));

    var options = {
        title: 'Developer Activities'
    };
    var chart = new google.visualization.PieChart(document.getElementById('piechart1'));
    chart.draw(chartData, options);
}


function computeHistogram2() {
    var options = gTimeline.options; // nastavenia neupravuj
    var step = jQuery.extend(true, {}, gTimeline.step); // deep copy celeho objektu

    step.start();
    var max = 0;
    var map = {};
    while (!step.end() && max < 1000) {
        max++;
        var text = step.getLabelMajor(options) + ":" + step.getLabelMinor(options);
        var cur = new Date(step.getCurrent().getTime());   // musime klonovat objekt
        step.next();
        var next = new Date(step.getCurrent().getTime());

        // Do with boundary
        var sum = 0;
        var items = getItems(cur, next, 5);
        items.forEach(
            function addNumber(value) {
                sum += parseInt(value);
            }
        );
        map[text] = sum;
    }
    return map;
}

function drawChart2() {
    var histogram = computeHistogram2();
    var chartData = new google.visualization.DataTable();
    chartData.addColumn('string', 'Date');
    chartData.addColumn('number', 'Changed lines');
    chartData.addRows(Object.keys(histogram).map(function (key) {
        return [ key, histogram[key] ]
    }));

    var options = {
        title: 'Developer Activities'
    };
    var chart = new google.visualization.PieChart(document.getElementById('piechart2'));
    chart.draw(chartData, options);
}