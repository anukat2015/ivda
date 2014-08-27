function Toolbar() {
    this.startInput = document.getElementById('startDate');
    this.endInput = document.getElementById('endDate');

    this.setTime = function (start, end) {
        this.startInput.value = gDateFormater.format(start);
        this.endInput.value = gDateFormater.format(end);
    };

    this.getStart = function () {
        return gDateFormater.parse(this.startInput.value);

    };

    this.getEnd = function () {
        return gDateFormater.parse(this.endInput.value);
    };
}

function onSetTime() {
    if (!gTimeline) return;
    gTimeline.setVisibleChartRange(gToolbar.getStart(), gToolbar.getEnd());
    onRangeChange();
    onRangeChanged();
}

function onSetCurrentTime() {
    if (!gTimeline) return;
    gTimeline.setVisibleChartRangeNow();
    onRangeChange();
    onRangeChanged();
}