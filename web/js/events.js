
function onSelect() {
	var row = getSelectedRow();
	if (row == undefined) return;
	var item = gData.getValue(row, 2);
	console.log("item " + item + " selected");
}

function onRangeChange() {
	var range = gTimeline.getVisibleChartRange();
	document.getElementById('startDate').value = dateFormat(range.start);
	document.getElementById('endDate').value   = dateFormat(range.end);
}

function onRangeChanged() {
	var range = gTimeline.getVisibleChartRange();
	console.log("range changed" + dateFormat(range.start) + " " + dateFormat(range.end));
	loadRange(range.start, range.end, gTimeline.size.contentWidth);
}

function setTime() {
	if (!gTimeline) return;
	var newStartDate = new Date(document.getElementById('startDate').value);
	var newEndDate   = new Date(document.getElementById('endDate').value);
	gTimeline.setVisibleChartRange(newStartDate, newEndDate);
}

function setCurrentTime() {
	if (!gTimeline) return;
	gTimeline.setVisibleChartRangeNow();
	onRangeChange();
}