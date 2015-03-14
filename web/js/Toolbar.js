/**
 * Created by Seky on 31. 10. 2014.
 */
function Toolbar() {
    this.dom = $('#toolbar');
    this.trash = $('#trash');
    this.startInput = $('#t-startDate');
    this.endInput = $('#t-endDate');
    this.dateFormat = 'd.m.Y H:i';
    this.developerPanel = $('#t-developer');
    this.featurePanel = $('#t-feature');
    this.granularityPanel = $('#t-granularity');
    this.lockButton = $("#t-lockMovement");
    this.sameScale = $("#t-sameScale");
    this.createButton = $("#t-submit");
    this.currentTime = $("#t-currentTime");

    this.trashVisible = false;
    //this.trashTooltipVisible = false;

    this.init = function () {
        // Stiahni mena vyvojarov
        var instance = this;
        gGlobals.service.getDevelopers(function (developers) {
            var data = developers.map(function (x) {
                return { name: x };
            });
            instance.setDeveloper(data);
        });

        // Priprav interaktivne datumy
        this.startInput.datetimepicker({
            format: this.dateFormat
        });
        this.endInput.datetimepicker({
            format: this.dateFormat
        });

        // Priprav tlacitka
        this.createButtons();
        this.createTooltips();

        // Nastav hodnoty pre dropdown menu
        this.setTime2(new Date("2014-12-06T12:00:00.000"));
        this.setFeature(gGlobals.graphs.getGraphs());
        var granularities = ["MONTH", "DAY", "HOUR", "MINUTE", "PER_VALUE"];
        this.setGranularity(granularities);

        // Vybuduj kos
        this.trash.droppable({
            accept: ".component",
            tolerance: "pointer",
            drop: function (e, ui) {
                var dropped = ui.draggable;
                var id = dropped.attr('id');
                gGlobals.graphs.destroy(id.substr(2));
            }
        });
    };

    this.createButtons = function() {
        var instance = this;
        this.createButton.button().click(function (event) {
                event.preventDefault();
                instance.createDiagram();
            });
        this.currentTime.button().click(function (event) {
                event.preventDefault();
                instance.setTime2(new Date());
            });
        this.lockButton.button({
            icons: {
                primary: "ui-icon-locked"
            }
        }).click(function (event) {
            var value = $(this).attr("value");
            var icon;
            if (value == "1") {
                icon = {};
                value = "0";
                gGlobals.graphs.setLockedMove(false);
            } else {
                icon = { primary: "ui-icon-locked"};
                value = "1";
                gGlobals.graphs.setLockedMove(true);
            }
            instance.lockButton.button("option", "icons", icon);
            $(this).attr("value", value);
        });
        this.sameScale.button({
            icons: {
                primary: "ui-icon-arrow-2-ne-sw"
            }
        }).click(function (event) {
            var value = $(this).attr("value");
            if (value == "1") {
                value = "0";
                gGlobals.graphs.setSameScale(false);
            } else {
                value = "1";
                gGlobals.graphs.setSameScale(true);
            }
            $(this).attr("value", value);
        });
    };

    this.showTrash = function () {
        if (this.trashVisible) return;
        this.trash.text("MOVE HERE TO TRASH");
        this.dom.hide();
        this.trash.show("highlight");
        this.trashVisible = true;
    };

    this.hideTrash = function () {
        if (!this.trashVisible) return;
        this.trash.hide();
        this.dom.show();
        this.trashVisible = false;
    };

    this.createTooltips = function () {
        var hideEffect = function() {
            $(this).hide('puff', 500);
        };
        var showEffect = function() {
                $(this).hide('slide', 500);
        };

        this.createButton.qtip({
            overwrite: false,
            hide: { effect: hideEffect },
            show: {
                event: false,
                effect: showEffect
            },
            content: 'Every graph you can move by grabbing title, or move to trash.'
        });
        this.sameScale.qtip({
            overwrite: false,
            hide: { effect: hideEffect },
            show: { effect: showEffect },
            content: 'Enable/Disable same scale at Y axis.'
        });
    };

    this.createDiagram = function () {
        $("#t-submit").qtip('toggle', true);
        // Vytvor diagram
        var atts = {
            developer: this.getDeveloper(),
            range: this.getTime(),
            granularity: this.getGranularity()
        };
        gGlobals.graphs.create(this.getFeature(), atts);
    };

    this.setTime = function (start, end) {
        this.startInput.val(start.dateFormat(this.dateFormat));
        this.endInput.val(end.dateFormat(this.dateFormat));
    };

    this.setTime2 = function (end) {
        var start = new Date(end.getTime() - 6 * 24 * 60 * 60 * 1000); // posledne 2 dni
        this.setTime(start, end);
    };

    this.getTime = function () {
        return {
            start: Date.parseDate(this.startInput.val(), this.dateFormat),
            end: Date.parseDate(this.endInput.val(), this.dateFormat)
        };
    };

    this.getDeveloper = function () {
        return this.developerPanel.val();
    };

    this.getFeature = function () {
        return this.featurePanel.val();
    };

    this.getGranularity = function () {
        return this.granularityPanel.val();
    };

    this.setDeveloper = function (items) {
        this.developerPanel.selectize({
            valueField: 'name',
            labelField: 'name',
            searchField: 'name',
            create: true,
            maxItems: 1,
            options: items
        });
    };

    this.setFeature = function (items) {
        var instance = this;
        this.featurePanel.selectize({
            valueField: 'id',
            labelField: 'name',
            searchField: 'name',
            sortField: 'name',
            create: false,
            maxItems: 1,
            options: items,
            onChange: function (id) {
                var com = gGlobals.graphs.find(id);
                var selectize = instance.granularityPanel[0].selectize;
                selectize.clearOptions();
                com.groups.forEach(function (x) {
                    selectize.addOption({ name: x });
                });
                // Defaultne vyber posledne
                instance._setDefaultGranularity(com.groups[com.groups.length - 1]);
            }
        });
    };

    this._setDefaultGranularity = function (id) {
        this.granularityPanel[0].selectize.setValue(id);
    };

    this.setGranularity = function (items) {
        this.granularityPanel.selectize({
            valueField: 'name',
            labelField: 'name',
            searchField: 'name',
            sortField: [[]],
            create: false,
            maxItems: 1,
            options: items.map(function (x) {
                return { name: x };
            })
        });
    };
}

