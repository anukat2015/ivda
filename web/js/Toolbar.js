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

    this.init = function () {
        // Stiahni mena vyvojarov
        var instance = this;
        $.ajax({
            url: gGlobals.service.getDevelopersURL(),
            success: function (developers, textStatus, jqXHR) {
                var data = developers.map(function (x) {
                    return { name: x };
                });
                instance.setDeveloper(data);
            }
        });

        // Priprav interaktivne datumy
        this.startInput.datetimepicker({
            format: this.dateFormat
        });
        this.endInput.datetimepicker({
            format: this.dateFormat
        });

        // Priprav tlacitka
        $("#t-submit")
            .button()
            .click(function (event) {
                event.preventDefault();
                instance.createDiagram();
            });
        $("#t-currentTime")
            .button()
            .click(function (event) {
                event.preventDefault();
                instance.setTime2(new Date());
            });
        $("t-lockMovement").button();

        // Nastav hodnoty pre dropdown menu
        this.setTime2(new Date("2014-08-06T12:00:00.000"));
        this.setFeature(gGlobals.graphs.getGraphs());
        this.setGranularity(["WEEK", "DAY", "HOUR", "MINUTE", "PER_VALUE"]);


        // Vybuduj kos
        $( "#droppable" ).droppable({
            tolerance: "touch",
            drop: function( event, ui ) {
                instance.trash.innerHTML = "TRASHED";
                var id = ui.draggable.attr('id');
                gGlobals.graphs.destroy(id.substr(2));
            }
        });
    };

    this.showTrash = function() {
        this.trash.innerHTML = "MOVE HERE TO TRASH";
        this.dom.hide();
        this.trash.show("highlight");
    };

    this.hideTrash = function() {
        this.trash.hide();
        this.dom.show();
    };

    this.createDiagram = function () {
        // Vytvor diagram
        gGlobals.graphs.create(this.getDeveloper(), this.getFeature(), this.getTime(), this.getGranularity());
    };

    this.setTime = function (start, end) {
        this.startInput.val(start.dateFormat(this.dateFormat));
        this.endInput.val(end.dateFormat(this.dateFormat));
    };

    this.setTime2 = function (end) {
        var start = new Date(end.getTime() - 2 * 24 * 60 * 60 * 1000); // posledne 2 dni
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

    this.setDeveloper = function (developers) {
        this.developerPanel.selectize({
            create: true,
            options: items
        });
    };

    this.setFeature = function (items) {
        this.featurePanel.selectize({
            valueField: 'id',
            labelField: 'name',
            searchField: 'name',
            create: true,
            options: items
        });
    };

    this.setGranularity = function (items, onchange) {
        this.featurePanel.selectize({
            create: true,
            options: items
        });
    };
}

