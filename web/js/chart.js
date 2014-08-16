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

function drawChart(data) {
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
    var chart = new google.visualization.PieChart(document.getElementById('piechart'));
    chart.draw(chartData, options);
}
