/**
 * Created by Seky on 3. 12. 2014.
 */

VisComponent = function () {
    DiagramComponent.call();
};

VisComponent.prototype = new DiagramComponent();

VisComponent.prototype._buildDom = function(manager) {
    DiagramComponent.prototype.buildDom(manager);
    this.dom.addClassName("mytimeline") ;
};

VisComponent.prototype.setRange = function (range) {
    this.diagram.setOptions(range);
    //this.diagram.redraw();
};

VisComponent.prototype.setData = function (data) {
    this.diagram.setItems(data);
    this.diagram.redraw();
};

VisComponent.prototype._createTimeline = function () {
    var options = {
        width: "100%",
        height: "300px",
        stack: false,
        margin: {
            item: 10, // minimal margin between items
            axis: 5   // minimal margin between items and the axis
        },
        orientation: 'top'
    };

    this.diagram = new vis.Timeline(this.getDiagramElement(), new vis.DataSet(), options);
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


VisComponent.prototype._createDynamicHistogram = function (items, groups) {
    var options = {
        width: "100%",
        height: "300px",
        legend: {
            right: {position: 'top-right'},
            left: {position: 'top-left'}
        },
        style: 'bar',
        barChart: {width: 1, align: 'center'}, // align: left, center, right
        drawPoints: true,
        dataAxis: {
            icons: false,
            popisY: "Pocet"
        },
        orientation: 'top'
    };

    this.diagram = new vis.Graph2d(this.getDiagramElement(), items, options, groups);
};

// ----- Pocet eventov
CountEventsCom = function () {
    VisComponent.call();
    this.title  = "Count of events";
    this.name = "count";
};
CountEventsCom.prototype = new VisComponent();

CountEventsCom.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('events', 'Count of events | Per day');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Pocet eventov divided
CountEventsDividedCom = function () {
    VisComponent.call();
    this.title  = "Count of events divided";
    this.name = "countDivided";
};
CountEventsDividedCom.prototype = new VisComponent();

CountEventsDividedCom.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('events', 'Count of events divided');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Changes of source codes
CodeChangesCom = function () {
    VisComponent.call();
    this.title  = "Changes of source codes";
    this.name = "loc";
};
CodeChanges.prototype = new VisComponent();

CodeChanges.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('changedInFuture', 'Changes of files in future');
    info.createGroup2('changedInHistory', 'Changes of files in past');
    info.createGroupRight("changedLines", "LOC changed");
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity dynamic-range-histogram
ActivityDynamicCom = function () {
    VisComponent.call();
    this.title  = "Activity dynamic-range-histogram";
    this.name = "activityHistogram";
};
ActivityDynamicCom.prototype = new VisComponent();

ActivityDynamicCom.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('Web', 'Web activities | Unique domains per duration');
    info.createGroup2('Ide', 'Ide activities | Changed LOC per duration');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity detail
ActivityDetailCom = function () {
    VisComponent.call();
    this.title  = "Activity dynamic-range-histogram";
    this.name = "activityDetail";
};
ActivityDetailCom.prototype = new VisComponent();

ActivityDetailCom.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.groups.add({id: 'A', content: 'Activity', className: 'graphGroupIde', drawPoints: {enabled: false}});
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Processes detail
ProcessesDetailCom = function () {
    VisComponent.call();
    this.title  = "Processes activity";
    this.name = "processDetail";
};
ProcessesDetailCom.prototype = new VisComponent();

ProcessesDetailCom.prototype.init = function(attributes, manager) {
    DiagramComponent.prototype.init(attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    this.diagram.setGroups(info.groups);
    this.updateData();
};
