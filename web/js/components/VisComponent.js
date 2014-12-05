/**
 * Created by Seky on 3. 12. 2014.
 */

VisComponent = function () {
    DiagramComponent.call();
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
    this.diagram.clear();
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
    this._prepareDiagram();
};

VisComponent.prototype.init = function (attributes, manager) {
    DiagramComponent.prototype.init.call(this, attributes, manager);
};


// ----- Pocet eventov
CountEventsCom = function () {
    VisComponent.call();
    this.title = "Pocet eventov histogram";
    this.name = "count";
    this.grouped = true;
};
CountEventsCom.prototype = new VisComponent();

CountEventsCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('events', 'Count of actions');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Pocet eventov divided
CountEventsDividedCom = function () {
    VisComponent.call();
    this.title = "Pocet eventov rozdelenych podla skupin";
    this.name = "countDivided";
    this.grouped = true;
};
CountEventsDividedCom.prototype = new VisComponent();

CountEventsDividedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('Web', 'Count of Web actions');
    info.createGroup2('Ide', 'Count of Ide actions');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Changes of source codes
CodeChangesCom = function () {
    VisComponent.call();
    this.title = "Zmeny v zdrojovom kode";
    this.name = "locChanges";
};
CodeChangesCom.prototype = new VisComponent();

CodeChangesCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('changedInFuture', 'Changes of files in future');
    info.createGroup2('changedInHistory', 'Changes of files in past');
    info.createGroup2("changedLines", "LOC changed");   // createGroupRight
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity time grouped
ActivityTimeGroupedCom = function () {
    VisComponent.call();
    this.title = "Zoskupene trvanie pre aktivity";
    this.name = "activityTimeGrouped";
    this.grouped = true;
};
ActivityTimeGroupedCom.prototype = new VisComponent();

ActivityTimeGroupedCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('Web', 'Web activities duration');
    info.createGroup2('Ide', 'Ide activities duration');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity time grouped
ActivityLocDomainVisitsGrouped = function () {
    VisComponent.call();
    this.title = "Zoskupene LOC pre Ide, pocet navstivenych webov pre Web pre aktivity";
    this.name = "activityLocDomainVisitsGrouped";
    this.grouped = true;
};
ActivityLocDomainVisitsGrouped.prototype = new VisComponent();

ActivityLocDomainVisitsGrouped.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('Web', 'Unique domains pre Web activities');
    info.createGroup2('Ide', 'Changed LOC per Ide activities');
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Activity Unique domains vs Changed LOC
ActivityDynamicCom = function () {
    VisComponent.call();
    this.title = "Loc pre Ide, pocet navstivenych webov pre Web pre aktivity";
    this.name = "activityHistogram";
    this.grouped = false;
};
ActivityDynamicCom.prototype = new VisComponent();

ActivityDynamicCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createDynamicHistogram(new vis.DataSet(), new vis.DataSet());
    var info = new GraphData();
    info.createGroup2('Web', 'Unique domains pre Web activityy');
    info.createGroup2('Ide', 'Changed LOC per IDe activityy');
    this.diagram.setGroups(info.groups);
    this.updateData();
};

// ----- Activity detail
ActivityDetailCom = function () {
    VisComponent.call();
    this.title = "Vizualizacia detailu aktivit";
    this.name = "activityDetail";
    this.grouped = false;
};
ActivityDetailCom.prototype = new VisComponent();

ActivityDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    info.groups.add({id: 'A', content: 'Activity', className: 'graphGroupIde', drawPoints: {enabled: false}});
    this.diagram.setGroups(info.groups);
    this.updateData();
};


// ----- Processes detail
ProcessesDetailCom = function () {
    VisComponent.call();
    this.title = "Vizualizacia detailu procesov";
    this.name = "processDetail";
    this.grouped = false;
};
ProcessesDetailCom.prototype = new VisComponent();

ProcessesDetailCom.prototype.init = function (attributes, manager) {
    VisComponent.prototype.init.call(this, attributes, manager);
    this._createTimeline();
    var info = new GraphData();
    this.diagram.setGroups(info.groups);
    this.updateData();
};
