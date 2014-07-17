
function getInitialData(start, end) {
    loadRange(start, end);

	// Create and populate a data table.
	var data = new google.visualization.DataTable();
	data.addColumn('datetime', 'start');
	data.addColumn('datetime', 'end');
	data.addColumn('string', 'content');
	data.addColumn('string', 'group');
	data.addColumn('string', 'className');
	
	// create some random data
	var names = ["Algie", "Barney", "Chris"];
	for (var n = 0, len = names.length; n < len; n++) {
		var name = names[n];
		var now = new Date();
		var end = new Date(now.getTime() - 12 * 60 * 60 * 1000);
		for (var i = 0; i < 5; i++) {
			var start = new Date(end.getTime() + Math.round(Math.random() * 5) * 60 * 60 * 1000);
			var end = new Date(start.getTime() + Math.round(4 + Math.random() * 5) * 60 * 60 * 1000);

			var r = Math.round(Math.random() * 2);
			var availability = (r === 0 ? "Unavailable" : (r === 1 ? "Available" : "Maybe"));
			var group = availability.toLowerCase();
			var content = availability;
			data.addRow([start, end, content, name, group]);
		}
	}
	
	return data;
}

// Handle the simple data source query response
function handleSimpleDsResponse(response) {
    if (response.isError()) {
        alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
        return;
    }

    var data = response.getDataTable();
    var chart = new google.visualization.PieChart(document.getElementById('simple_div'));
    chart.draw(data, {width: 600, height: 150, is3D: true});
}


function loadRange(start, end) {
    var query = new google.visualization.Query('simpleexample?tq=select name,population');
    query.send(handleSimpleDsResponse);

}