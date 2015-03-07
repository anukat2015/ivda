/**
 * Created by Seky on 3. 12. 2014.
 */

VisComponent = function () {
    DiagramComponent.call();
    this.groups = ["MONTH", "DAY", "HOUR", "MINUTE"];
};

VisComponent.prototype = new DiagramComponent();

VisComponent.prototype._buildDom = function () {
    DiagramComponent.prototype._buildDom.call(this);
    this.dom.addClass("mytimeline");
};

VisComponent.prototype.setRange = function (range) {
    this.diagram.setOptions(range);
    this.attributes.range = range;
    this._updateDescription();
};

VisComponent.prototype.setData = function (data) {
    this.diagram.setItems(data);
    this.diagram.fit();
    this.diagram.redraw();
};

VisComponent.prototype.destroy = function () {
    this.diagram.destroy();
    DiagramComponent.prototype.destroy.call(this);
};

VisComponent.prototype._prepareDiagram = function () {
    var instance = this;
    this.diagram.on('rangechanged', function (properties) {
        instance.onMove(properties);
    });
    this.setRange(this.attributes.range);
};

/**
 * Vypracuj zanaranie objektov v grafe.
 * Tzv po kliknuti na stlpec sa vytvori rovnaky graf.
 * Novy graf zobrazuje casovy horizont daneho stlpca a ma mensiu granularitu.
 * @private
 */
VisComponent.prototype._zanaranie = function () {
    var nextGranularity = this.getNextGranularity();
    if (nextGranularity == null) {
        return; // dalsia uroven neexistuje
    }
    var instance = this;
    this.dom.on("dblclick", ".LineGraph .bar", function (event) {
        var bar = $(this);
        var x = bar.attr('x');
        if(x == undefined ) {
            return;
        }
        var shiftX = bar.parent().position().left;
        x = parseFloat(x) + shiftX;
        var endPosition = parseFloat(bar.attr('width')) + x;
        var atts = jQuery.extend(true, {}, instance.attributes);
        var toTime = instance.diagram.linegraph.body.util.toTime;
        atts.range = {
            start: toTime(x),
            end: toTime(endPosition)
        };
        atts.granularity = nextGranularity;
        instance.manager.create(instance.getName(), atts);
    });
};


VisComponent.prototype._createTimeline = function () {
    var options = {
        width: "100%",
        height: "700px",
        stack: true,
        margin: {
            item: 10, // minimal margin between items
            axis: 5   // minimal margin between items and the axis
        },
        orientation: 'top'
    };

    this.diagram = new vis.Timeline(this.getDiagramElement(), new vis.DataSet(), options);
    this._prepareDiagram();
};

VisComponent.prototype._registerNavigationBar = function () {
    this.dom.find(".vis.timeline .center > .content").append('   \
        <div class="timeline-navigation ui-widget ui-state-highlight ui-corner-all"  \
    style="position: absolute;right: 7px;top: 53%;">                   \
        <div class="timeline-navigation-zoom-in" title="Zoom in"><span class="ui-icon ui-icon-circle-zoomin"></span>  \
        </div>                                                                                                 \
        <div class="timeline-navigation-zoom-out" title="Zoom out"><span class="ui-icon ui-icon-circle-zoomout"></span>  \
        </div>    \
        <div class="timeline-navigation-move-left" title="Move left"><span  \
        class="ui-icon ui-icon-circle-arrow-w"></span></div>   \
        <div class="timeline-navigation-move-right" title="Move right"><span \
        class="ui-icon ui-icon-circle-arrow-e"></span></div>    \
    </div>');

};

VisComponent.prototype._createDynamicHistogram = function (items, groups, overlap) {
    var options = {
        width: "100%",
        height: "300px",
        legend: {
            right: {position: 'top-right'},
            left: {position: 'top-left'}
        },
        style: 'bar',
        barChart: {
            handleOverlap: overlap,
            width: 1,
            align: 'center'
        }, // align: left, center, right
        drawPoints: true,
        dataAxis: {
            icons: false,
            popisY: "Count"
        },
        orientation: 'top'
    };

    this.diagram = new vis.Graph2d(this.getDiagramElement(), items, options, groups);
    this._prepareDiagram();
    this._zanaranie();
};

VisComponent.prototype.init = function (attributes, manager) {
    DiagramComponent.prototype.init.call(this, attributes, manager);
};


// ----- Pocet eventov
CountEventsCom = function () {
    VisComponent.call();
    this.title = "Histogram of actions";
    this.name = "countEvents";
};
CountEventsCom.prototype = new VisComponent();

CountEventsCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup2('events', 'Count of actions');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Pocet eventov divided
CountEventsDividedCom = function () {
    VisComponent.call();
    this.title = "Histogram of actions, grouped by enviroment";
    this.name = "countEventsDivided";
};
CountEventsDividedCom.prototype = new VisComponent();

CountEventsDividedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Count of actions in Web browser');
    info.createGroup2('Ide', 'Count of actions in Ide enviroment');
    info.createGroup2('Mix', 'Count of another actions');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Changes of source codes
CodeChangesCom = function () {
    VisComponent.call();
    this.title = "Graph of source code adjustment";
    this.name = "locChanges";
    this.groups = ["MONTH", "DAY", "HOUR", "MINUTE", "PER_VALUE"];
};
CodeChangesCom.prototype = new VisComponent();

CodeChangesCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup3("changedLines", "Source code adjustments by LOC metric", "graphGroup1");
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity time grouped
ActivityTimeGroupedCom = function () {
    VisComponent.call();
    this.title = "Grouped duration of activities";
    this.name = "activityTimeGrouped";
    this.groups = ["MONTH", "DAY", "HOUR"];
};
ActivityTimeGroupedCom.prototype = new VisComponent();

ActivityTimeGroupedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Duration of activities in Browser, in' + this.getGranularity());
    info.createGroup2('Ide', 'Duration of activities in Ide enviroment, in' + this.getGranularity());
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity time grouped
ActivityLocDomainVisitsGrouped = function () {
    VisComponent.call();
    this.title = "Activities and their features, grouped by period";
    this.name = "activityLocDomainVisitsGrouped";
    this.groups = ["MONTH", "DAY", "HOUR"];
};
ActivityLocDomainVisitsGrouped.prototype = new VisComponent();

ActivityLocDomainVisitsGrouped.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Count of visited domains during activity in Browser, one bar is group of same type activity');
    info.createGroup2('Ide', 'Adjusted source code by LOC metric during activity in Ide, one bar is group of same type activity');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity Unique domains vs Changed LOC
ActivityDynamicCom = function () {
    VisComponent.call();
    this.title = "Activities and their features";
    this.name = "activityHistogram";
    this.groups = ["PER_VALUE"];
};
ActivityDynamicCom.prototype = new VisComponent();

ActivityDynamicCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup2('Web', 'Count of visited domains during activity in Browser, one bar is one activity');
    info.createGroup2('Ide', 'Adjusted source code by LOC metric during activity in Ide enviroment, one bar is one activity');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity detail
ActivityDetailCom = function () {
    VisComponent.call();
    this.title = "Detail of activities per enviroment";
    this.name = "activityDetail";
    this.groups = ["PER_VALUE"];
};
ActivityDetailCom.prototype = new VisComponent();

ActivityDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.createGroup2('Web', 'Activity in Web browser');
    info.createGroup2('Ide', 'Activity in Ide enviroment');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Processes detail
ProcessesDetailCom = function () {
    VisComponent.call();
    this.title = "Detail of runned OS processes";
    this.name = "processDetail";
    this.groups = ["PER_VALUE"];
};
ProcessesDetailCom.prototype = new VisComponent();

ProcessesDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.createGroup2('processes', 'List of process');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

ProcessesDetailCom.prototype.updateData = function () {
    var instance = this;
    gGlobals.service.getProcesses(this.attributes, function (data) {
        if (instance.attributes == undefined) {
            // Diagram bol zniceny
        } else {
            var x = instance.fixData(data);
            instance.setData(x);
        }
    });
};

ProcessesDetailCom.prototype.fixData = function (processes) {
    var items = new vis.DataSet();
    var service = gGlobals.service;
    for (var i = 0; i < processes.length; i++) {
        var process = processes[i];
        items.add({
            start: service._convertDate(process.start),
            end: service._convertDate(process.end),
            content: process.name,
            group: "processes"
        });
    }
    return items;
};
