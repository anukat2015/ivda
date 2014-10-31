/**
 * Created by Seky on 31. 10. 2014.
 */
function Toolbar() {
    this.startInput = $('#startDate');
    this.endInput = $('#endDate');
    this.dateFormat = 'd.m.Y H:i';
    this.developerPanel = $('#select-links');

    this.init = function (developers, onchange) {
        this.startInput.datetimepicker({
            format: this.dateFormat
        });
        this.endInput.datetimepicker({
            format: this.dateFormat
        });
        this.setDeveloper(developers, onchange);
    };

    this.setTime = function (start, end) {
        this.startInput.val(start.dateFormat(this.dateFormat));
        this.endInput.val(end.dateFormat(this.dateFormat));
    };

    this.getStart = function () {
        return Date.parseDate(this.startInput.val(), this.dateFormat);
    };

    this.getEnd = function () {
        return Date.parseDate(this.endInput.val(), this.dateFormat);
    };

    this.getDeveloper = function () {
        return this.developerPanel.val();
    };

    this.setDeveloper = function (developers, onchange) {
        // Postav seleztize
        var items = developers.map(function (x) {
            return { name: x };
        });
        this.developerPanel.selectize({
            plugins: ['remove_button'], // , 'drag_drop'
            maxItems: 1,
            delimiter: ',',
            persist: false,
            valueField: 'name',
            labelField: 'name',
            searchField: 'name',
            createOnBlur: true,
            create: false,
            options: items,
            onChange: onchange
        });
    };
}


function onSetTime() {
    if (gGlobals && gGlobals.wasInit) {
        gGlobals.timeline.panel.setVisibleChartRange(gGlobals.toolbar.getStart(), gGlobals.toolbar.getEnd(), true);
        gGlobals.timeline.onRangeChange();
        gGlobals.timeline.onRangeChanged();
    }
}

function onSetCurrentTime() {
    if (gGlobals && gGlobals.wasInit) {
        gGlobals.timeline.panel.setVisibleChartRangeNow();
        gGlobals.timeline.onRangeChange();
        gGlobals.timeline.onRangeChanged();
    }
}
