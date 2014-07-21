// Called when the Visualization API is loaded.

function drawChart() {
	var data = google.visualization.arrayToDataTable([
	  ['Task', 'Hours per Day'],
	  ['Work',     11],
	  ['Eat',      2],
	  ['Commute',  2],
	  ['Watch TV', 2],
	  ['Sleep',    7]
	]);

	var options = {
	  title: 'My Daily Activities'
	};

	var gChart = new google.visualization.PieChart(document.getElementById('piechart'));
	gChart.draw(data, options);
}
	  
function drawVisualization() {
	// Create and populate a data table.
    gData = new google.visualization.DataTable();
	var now = new Date();
	var start = new Date(now.getTime() - 4 * 60 * 60 * 1000);
	var end = new Date(now.getTime() + 8 * 60 * 60 * 1000);
    loadRange(start, end);

		
	// specify options
	var options = {
		width:  "100%",
		height: "50%",
		layout: "box",
		axisOnTop: true,
		eventMargin: 1,  // minimal margin between events
		eventMarginAxis: 0, // minimal margin beteen events and the axis
		editable: false,
		showNavigation: true,
		zoomMin: 1000 * 60 * 5,             // 15sec
		zoomMax: 1000 * 60 * 60 * 24 * 7,     // tyzden
		animate: true,
		animateZoom: true,
		//cluster: true,
		groupMinHeight: 100	
	};

	// Instantiate our timeline object.
	gTimeline = new links.Timeline(document.getElementById('mytimeline'), options);
    google.visualization.events.addListener(gTimeline, 'rangechange', onRangeChange);
    google.visualization.events.addListener(gTimeline, 'rangechanged', onRangeChanged);
	google.visualization.events.addListener(gTimeline, 'select', onSelect);
	
	gTimeline.draw(gData);
	gTimeline.setVisibleChartRange(start, end);
	onRangeChange();
	
	drawChart();
}

function handleServiceResponse(response) {
    if (response.isError()) {
        alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
        return;
    }
    gData = response.getDataTable();
    console.log(gData);
    gTimeline.draw(gData);
}


function loadRange(start, end) {
    var query = new google.visualization.Query('datatable?start=' + start.getTime() + '&end=' + end.getTime());
    query.send(handleServiceResponse);
}
/*
 $.ajax({
 url: 'http://example.com/',
 type: 'PUT',
 data: 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
 success: function() { alert('PUT completed'); }
 });
 */