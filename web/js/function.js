
// Format given date as "yyyy-mm-dd hh:ii:ss"
// @param datetime   A Date object.
function dateFormat(date) {
	var datetime =   date.getFullYear() + "-" +
			((date.getMonth()   <  9) ? "0" : "") + (date.getMonth() + 1) + "-" +
			((date.getDate()    < 10) ? "0" : "") +  date.getDate() + " " +
			((date.getHours()   < 10) ? "0" : "") +  date.getHours() + ":" +
			((date.getMinutes() < 10) ? "0" : "") +  date.getMinutes() + ":" +
			((date.getSeconds() < 10) ? "0" : "") +  date.getSeconds();
	return datetime;
}

function getSelectedRow() {
	var row = undefined;
	var sel = gTimeline.getSelection();
	if (sel.length) {
		if (sel[0].row != undefined) {
			row = sel[0].row;
		}
	}
	return row;
}

/*
function getRandomName() {
	var names = ["Algie", "Barney", "Grant", "Mick", "Langdon"];

	var r = Math.round(Math.random() * (names.length - 1));
	return names[r];
}

function getSelectedRow() {
	var row = undefined;
	var sel = gTimeline.getSelection();
	if (sel.length) {
		if (sel[0].row != undefined) {
			row = sel[0].row;
		}
	}
	return row;
}

function strip(html)
{
	var tmp = document.createElement("DIV");
	tmp.innerHTML = html;
	return tmp.textContent || tmp.innerText;
}

// Make a callback function for the select event

function onEdit(event) {
	var row = getSelectedRow();
	var content = gData.getValue(row, 2);
	var availability = strip(content);
	var newAvailability = prompt("Enter status\n\n" +
			"Choose from: Available, Unavailable, Maybe", availability);
	if (newAvailability != undefined) {
		var newContent = newAvailability;
		gData.setValue(row, 2, newContent);
		gData.setValue(row, 4, newAvailability.toLowerCase());
		gTimeline.draw(gData);
	}
};

function onNew() {
	alert("Clicking this NEW button should open a popup window where " +
			"a new status event can be created.\n\n" +
			"Apperently this is not yet implemented...");
};
*/