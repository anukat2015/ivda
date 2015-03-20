/**
 * Created by Seky on 14. 3. 2015.
 */

HistogramComponent = function () {
    VisComponent.call();
};

HistogramComponent.prototype = new VisComponent();

HistogramComponent.prototype.getYRange = function (range) {
    var step = this.diagram.linegraph.yAxisLeft.step;
    if(step == undefined) return undefined;
    return {min: step._start, max: step._end};
};

HistogramComponent.prototype.setYRange = function (range) {
    var max = (range == undefined) ? undefined : range.max;
    this.diagram.linegraph.setOptions({dataAxis: {customRange: {left: {max: max}}}});
};

HistogramComponent.prototype.setData = function (data) {
    VisComponent.prototype.setData.call(this, data);
    this.manager.updateScale();
};

HistogramComponent.prototype._createDynamicHistogram = function (items, groups, overlap) {
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

/**
 * Vypracuj zanaranie objektov v grafe.
 * Tzv po kliknuti na stlpec sa vytvori rovnaky graf.
 * Novy graf zobrazuje casovy horizont daneho stlpca a ma mensiu granularitu.
 * @private
 */
HistogramComponent.prototype._zanaranie = function () {
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

// ----- Pocet eventov
CountEventsCom = function () {
    VisComponent.call();
    this.title = "Histogram of actions";
    this.name = "countEvents";
};
CountEventsCom.prototype = new HistogramComponent();

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
CountEventsDividedCom.prototype = new HistogramComponent();

CountEventsDividedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Count of actions in Web browser');
    info.createGroup2('Ide', 'Count of actions in IDE enviroment');
    info.createGroup2('Mix', 'Count of another actions');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Changes of source codes
CodeChangesCom = function () {
    HistogramComponent.call();
    this.title = "Graph of source code adjustment";
    this.name = "locChanges";
    this.groups = ["MONTH", "DAY", "HOUR", "MINUTE", "PER_VALUE"];
};
CodeChangesCom.prototype = new HistogramComponent();

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
    HistogramComponent.call();
    this.title = "Grouped duration of activities";
    this.name = "activityTimeGrouped";
    this.groups = ["MONTH", "DAY", "HOUR"];
};
ActivityTimeGroupedCom.prototype = new HistogramComponent();

ActivityTimeGroupedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Duration of activities in Web browser, in' + this.getGranularity());
    info.createGroup2('Ide', 'Duration of activities in IDE enviroment, in' + this.getGranularity());
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity time grouped
ActivityLocDomainVisitsGrouped = function () {
    HistogramComponent.call();
    this.title = "Activities and their features, grouped by period";
    this.name = "activityLocDomainVisitsGrouped";
    this.groups = ["MONTH", "DAY", "HOUR"];
};
ActivityLocDomainVisitsGrouped.prototype = new HistogramComponent();

ActivityLocDomainVisitsGrouped.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "stack");
    var info = new GraphData();
    info.createGroup2('Web', 'Count of visited domains during activity in Web browser, one bar is group of same type activity');
    info.createGroup2('Ide', 'Adjusted source code by LOC metric during activity in IDE, one bar is group of same type activity');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity Unique domains vs Changed LOC
ActivityDynamicCom = function () {
    HistogramComponent.call();
    this.title = "Activities and their features";
    this.name = "activityHistogram";
    this.groups = ["PER_VALUE"];
};
ActivityDynamicCom.prototype = new HistogramComponent();

ActivityDynamicCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet(), "overlap");
    var info = new GraphData();
    info.createGroup2('Web', 'Count of visited domains during activity in Web browser, one bar is one activity');
    info.createGroup2('Ide', 'Adjusted source code by LOC metric during activity in IDE enviroment, one bar is one activity');
    this.diagram.setGroups(info.groups);
    this.updateData();
};
